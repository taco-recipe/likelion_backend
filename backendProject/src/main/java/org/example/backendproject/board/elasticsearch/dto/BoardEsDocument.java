package org.example.backendproject.board.elasticsearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backendproject.board.dto.BoardDTO;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "board-index")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardEsDocument {

    //엘라스틱 서치에 적용될 문서를 자바 객체로 정의한 클래스
    //엘라스틱 전용 dot
    @Id
    private String id;
    private String title;
    private String content;
    private String username;
    private Long userId;
    private String created_date;
    private String updated_date;
    private Long view_count = 0L;

    public static BoardEsDocument from(BoardDTO dto) {
        return BoardEsDocument.builder()
                .id(String.valueOf(dto.getId()))
                .title(dto.getTitle())
                .content(dto.getContent())
                .username(dto.getUsername())
                .userId(dto.getUser_id())
                .created_date(dto.getCreated_date() != null ? dto.getCreated_date().toString() : null)
                .updated_date(dto.getUpdated_date() != null ? dto.getUpdated_date().toString() : null)
                .view_count(dto.getViewCount())
                .build();
    }

}
