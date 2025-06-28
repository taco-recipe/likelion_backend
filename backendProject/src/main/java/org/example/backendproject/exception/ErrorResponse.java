package org.example.backendproject.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int code; // 상태코드
    private String message; // 커스텀 에러메세지
    private String detail; // 살제 에러 메세지


}
