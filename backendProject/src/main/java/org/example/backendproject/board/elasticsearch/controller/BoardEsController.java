package org.example.backendproject.board.elasticsearch.controller;

import lombok.RequiredArgsConstructor;
import org.example.backendproject.board.elasticsearch.dto.BoardEsDocument;
import org.example.backendproject.board.elasticsearch.service.BoardEsService;
import org.example.backendproject.board.searchlog.dto.SearchLogMessage;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardEsController {

    private final BoardEsService boardEsService;

    private final KafkaTemplate<String,SearchLogMessage> kafkaTemplate;

    @GetMapping("/elasticsearch")

    //엘라스틱 서치 검색 결과를 page 형태로 감싼 다음 http 응답을 json으로 반환
    public ResponseEntity<Page<BoardEsDocument>> elasticSearch(@RequestParam String keyword,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {

        // 검색어 정보 카프카 전송
        String UsearId = "1";
        String SearchedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        SearchLogMessage message = new SearchLogMessage(keyword, UsearId, SearchedAt);
        kafkaTemplate.send("search-log", message); //search-log 토픽으로 메세지 전달

        return ResponseEntity.ok(boardEsService.search(keyword, page, size));

    }

    @GetMapping("/top-keywords")
    public ResponseEntity<?> getTopKeyword(){
        List<String> keywords = boardEsService.getTopSearchKeyword();
        return ResponseEntity.ok(keywords);
    }
}
