package org.example.backendproject.board.elasticsearch.controller;

import lombok.RequiredArgsConstructor;
import org.example.backendproject.board.elasticsearch.dto.BoardEsDocument;
import org.example.backendproject.board.elasticsearch.service.BoardEsService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardEsController {

    private final BoardEsService boardEsService;

    @GetMapping("/elasticsearch")

    //엘라스틱 서치 검색 결과를 page 형태로 감싼 다음 http 응답을 json으로 반환
    public ResponseEntity<Page<BoardEsDocument>> elasticSearch(@RequestParam String keyword,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(boardEsService.search(keyword, page, size));

    }
}
