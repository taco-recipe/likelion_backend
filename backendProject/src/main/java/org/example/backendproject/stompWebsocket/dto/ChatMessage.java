package org.example.backendproject.stompWebsocket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor  // 이거도 뭐임
@AllArgsConstructor // 이거 뭐임
public class ChatMessage {

    private String message;
    private String from;
    private String to;      // 귓속말 받을 사람
    private String roomId;

}
