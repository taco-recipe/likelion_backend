package org.example.backendproject.board.elasticsearch.service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backendproject.board.elasticsearch.dto.BoardEsDocument;
import org.example.backendproject.board.elasticsearch.repository.BoardEsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardEsService {

    //엘라스틱 서치에 명령을 전달하는 공식 자바 api
    private final ElasticsearchClient client;

    private final BoardEsRepository repository;

    public void save(BoardEsDocument document) {
        repository.save(document);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    //검색 키워드와 페이지 번호와 크기를 받아서 엘라스틱 서치에서 검색하는 메서드
    //검색된 정보와 페이징 정보도 함께 반환하도록 하기 위해 page 객체 사용
    public Page<BoardEsDocument> search(String keyword, int page, int size) {

        try{
            //엘라스틱 서치에서 페이지을 위한 시작 위치를 계산하는 변수
            int from = page*size;

            Query query;

            //검색어가 없으면 모든 문서를 검색하는 matchAll 쿼리
            if (keyword == null || keyword.isBlank()) {
                query = (Query) MatchAllQuery.of(m -> m)._toQuery(); // 전체 문서를 가져오는 쿼리
                //MatchAllQuery는 엘라스틱 서치에서 조건 없이 모든 문서를 검색할떄 사용하는 쿼리
            }
            //검색어가 있으면
            else{
                //boolquery 는 복수 조건을 조합할 떄 사용하는 ㅜ커리
                //이 쿼리 안에서 여러개의 조건을 나열
                //예를 들어서 백엔드라는 키워드가 들어왔능 떄 이 백엔드 키워드를 어떻게 분석해서 데이터를 보옂루 것인가 작성
                query = (Query) BoolQuery.of(b -> {

                    //PrefixQuery는 해당 필드가 특정 단어로 시작하는지 검사하는 쿼리
                    //MatchQuery는 해당 단어가 포함되어 있는지 검사하는 쿼리

                    /**
                     * must : 모두 일치해야함(and)
                     * should : 하나라도 일치하면 됨 (or)
                     * must_not : 해당 조건을 만족하면 제외
                     * filter : must와 유사하지만, 점수(score)를 계산하지 않음 (성능 최적화용)
                     */
                    //접두어 검색
                    b.should(PrefixQuery.of (p-> p.field("title").value(keyword))._toQuery());
                    b.should(PrefixQuery.of (p-> p.field("content").value(keyword))._toQuery());

                    //초성검색
                    b.should(PrefixQuery.of (p-> p.field("title.chosung").value(keyword))._toQuery());
                    b.should(PrefixQuery.of (p-> p.field("content.chosung").value(keyword))._toQuery());

                    //중간 글자 검색(match만 가능)
                    b.should(MatchQuery.of(m -> m.field("title.ngram").query(keyword))._toQuery());
                    b.should(MatchQuery.of(m -> m.field("content.ngram").query(keyword))._toQuery());

                    // fuzziness: "AUTO"는  오타 허용 검색 기능을 자동으로 켜주는 설정 -> 유사도 계산을 매번 수행하기 때문에 느림
                    //짧은 키워드에는 사용 xxx
                    //오타 허용 (오타허용은 match만 가능 )
                    if (keyword.length()>=3){
                        b.should(MatchQuery.of(m ->m.field("title").query(keyword).fuzziness("AUTO"))._toQuery());
                        b.should(MatchQuery.of(m ->m.field("content").query(keyword).fuzziness("AUTO"))._toQuery());
                    }
                    return b;
                })._toQuery();
            }

            //SearchRequest는 엘라스틱 서치에서 검색하기 위한 검색 요청 객체
            //인덱스 명, 페이징 정보,쿼리를 포함한 검색 요청
            SearchRequest request = SearchRequest.of(s -> s
                    .index("board-index")
                    .from(from)
                    .size(size)
                    .query(query)

                    //정렬
                    .sort( sort -> sort
                            .field(f->f
                                    .field("created_date")
                                    .order(SortOrder.Desc)))
            );

            //엘라스틱 서치의 검색 결과를 담고 있는 응답 객체
            SearchResponse<BoardEsDocument> response =
                    //엘라스틱 서치에 명령을 전달하는 자바api검색요청을 담아 응답객체로 변환
                    client.search(request, BoardEsDocument.class);

            log.info("Search response error: {}", response);

            //위 응답 객체에서 받은 검색 결과 중 문서만 추출해 리스트로 받음
            // hit는 엘라스틱 서치에서 검색된 문서 1개를 감싸고 있는 객체
            List<BoardEsDocument> content = response.hits() // 엘라스틱 서치 응답에서 hits(문서 검색결과)wㅓㄴ체를 꺼냄
                    .hits() // 검색 결과 안에 개별 리스트를 가져옴
                    .stream()// 자바 stream api를 사용
                    .map(Hit::source) // 각 hit 객체에서 실제 문서를 꺼내는 작업
                    .collect((Collectors.toList())); // 위에서 꺼낸 객체를 자바 List 넣음


            // 전체 검색 결과 수 (총 문서의 개수)
            long total = response.hits().total().value();

            return new PageImpl<>(content, PageRequest.of(from, size), total);

        }catch (IOException e){
            log.error("검색오류 : " + e.getMessage());
            throw new RuntimeException("검색 중 오류 발생",e);
        }
    }

    public void bulkIndexInsert(List<BoardEsDocument> documents) throws IOException {
        int batchSize = 1000;
        for (int i = 0; i < documents.size(); i+=batchSize) {
            //현재 batch의 끝 인덱스를 구함
            int end = Math.min(i+batchSize, documents.size());
            //현재 batch 단위의 문서 리스트를 잘라냄
            List<BoardEsDocument> batch = documents.subList(i, end);
            //엘라스틱 서치의  bulk 요청을 담ㅇ르 빌더 생성
            BulkRequest.Builder br = new BulkRequest.Builder();

            for (BoardEsDocument document : batch) {
                br.operations(op -> op //operations()로 하나하나 문서를 담음
                        .index(idx -> idx // 인덱스에 문서를 저장하ㄱ는 작업
                                .index("board-index") // 인덱스명
                                .id(String.valueOf(document.getId())) // 수동으로 id 저장
                                .document(document) // 실제 저장할 문서 객체
                        )
                );

            }

            //bulk 요청 tlfgod : batch 단위로 엘라스틱 에 색인 수행
            BulkResponse response = client.bulk(br.build());
            //벌크 작업 중 에러가 있는 경우 로그 출력
            if(response.errors()){
                for (BulkResponseItem item : response.items()){
                    if(item.error() != null){
                        //실패한 문서의 id와 여러 내용을 출력
                        log.error("엘라스틱 서치 벌크 색인 작업중 오류 실패 : {}, 오류 : {}",item.id(),item.error());

                    }
                }
            }

        }


    }

    public List<String> getTopSearchKeyword(){

        //TermsAggregation 엘라스틱 서치의 집계 메서드
        TermsAggregation termsAggregation = TermsAggregation.of( t -> t
                .field("keyword.keyword") // 집계 기준 필드
                .size(10));                 // 상위 10개만 불러오기


        //집계 요청
        SearchRequest request = SearchRequest.of( s-> s
                .index("search-log-index") // 집계를 가져올 인덱스 이름
                .size(0)                    // 집계만 가져오고 검색 겨로가는 가져오지 않음
                .aggregations("top_keywords",a -> a.terms(termsAggregation))); // 인기 검색어 집계

        try{
            //집계 응답
            SearchResponse<Void> response = client.search(request,Void.class);
            return response.aggregations()// 응답 결과에서 집계 결과만 꺼냄
                    .get("top_keywords")    // 위에서 내가 집계 여청한 이름
                    .sterms()   // String terms로 변환
                    .buckets()// 집계 결과 버킷 리스트로
                    .array()//버킷 리스트를 배열로
                    .stream()// 배열을 스트림으로 변환
                    .map(buket -> buket.key().stringValue()) // 버킷의 ket값을 문자열로 꺼냄
                    .map(Object ::toString) // string으로 반환
                    .collect(Collectors.toList()); // 스트링 결과를 리스트로 모아서 반환
        }
        catch (IOException e){
            throw new RuntimeException("검색어 통계 조회 중 오류 발생",e);
        }
    }


}
