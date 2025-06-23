package org.example.backendproject.stompWebsocket.controller;

import lombok.RequiredArgsConstructor;
import org.example.backendproject.stompWebsocket.dto.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

//    @MessageMapping("/chat.sendMessage")
//    @SendTo("/topic/public")
//    public ChatMessage sendMessage(ChatMessage message) {
//        return message;
//    }
    // 경로가지고 스텀프에서 자동으로 메세지를 받고 보냄

    //서버가 클라이언트에게 수동으로 메세지를 보낼 수 있게 하는 클래스
    private final SimpMessagingTemplate template;

    @Value("${PROJECT_NAME:web Server}")
    private String instansName;

    // 동적으로 방 생성 기능
    // 스텀프에서 확장해서 동적으로 방 지정해서 전달
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage message) {
        message.setMessage(instansName+" "+message.getMessage());

        if (message.getTo() != null && !message.getTo().isEmpty()) {
            // 귓속말
            //내 아이디로 귓속말경로를 환성화 함
            template.convertAndSendToUser (message.getTo(), "/queue/private", message);
            System.out.println("Sent message to room "+message.getTo());
            System.out.println("Sent message to user "+message.getFrom());
            System.out.println("Sent message to room "+message.getMessage());
        } else {
            //message에서 roomID를 추출해서 해당 roomId를 구동하고 있는 클리이언ㄷ트에게 메세지를 전달
            template.convertAndSend("/topic/"+message.getRoomId(), message);
            System.out.println("Sent message to room "+message.getRoomId());
            System.out.println("Sent message to room "+message.getMessage());
        }

    }

}

