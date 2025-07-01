package org.example.backendproject.board.searchlog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchLogMessage {

    //kafka로 주고 받는 메세지 포멧

    private String keyword;     //검색된 키워드
    private String userId;      //검색한 유저ID
    private String searchedAt;  // 검색한 시간
}
