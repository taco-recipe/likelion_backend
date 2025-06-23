package org.example.backendproject.purewebsocket.dto;

import lombok.Getter;

//데이터 -> 자바 객체
@Getter
public class ChatMessage {

    private String roomId;
    private String message;
    private String from;
}
