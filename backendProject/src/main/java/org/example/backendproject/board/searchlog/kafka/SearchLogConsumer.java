package org.example.backendproject.board.searchlog.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backendproject.board.searchlog.domain.SearchLogDocument;
import org.example.backendproject.board.searchlog.dto.SearchLogMessage;
import org.example.backendproject.board.searchlog.service.SearchLogEsService;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchLogConsumer {

    //카프카에서 메세지를 ㅋ거내서 엘라스틱 서치로 넘기는 클래스
    private final SearchLogEsService searchLogEsService;

    @KafkaListener(
            topics = "search-log", //구독한 토픽이름
            groupId =  "search-log-group", // 이 컨슈머가 어떤 컨슈머 그룹에 속하는지
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(SearchLogMessage message) {

        log.info("카프카에서 메시지 수신 : {}",message);
        SearchLogDocument doc = SearchLogDocument.builder()
                .keyword(message.getKeyword())
                .userId(message.getUserId())
                .searchedAt(message.getSearchedAt())
                .build();

        //엘라스틱 서치에 저장
        searchLogEsService.save(doc);
    }

}
