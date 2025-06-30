package org.example.backendproject.board.elasticsearch.repository;


import org.example.backendproject.board.elasticsearch.dto.BoardEsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BoardEsRepository extends ElasticsearchRepository<BoardEsDocument, String> {


    //문서 id로 데이터 삭제 하는 쿼리 메서드
    void deleteById(String id);
}
