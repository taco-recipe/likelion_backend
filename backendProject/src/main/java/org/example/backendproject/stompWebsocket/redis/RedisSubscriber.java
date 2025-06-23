package org.example.backendproject.stompwebsocket.redis;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.backendproject.stompwebsocket.dto.ChatMessage;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener{

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {

        try {
            String msgBody = new String(message.getBody());
            ChatMessage chatMessage = objectMapper.readValue(msgBody, ChatMessage.class);

            if (chatMessage.getTo() != null && !chatMessage.getTo().isEmpty()) {
                // 귓속말
                simpMessagingTemplate.convertAndSendToUser(chatMessage.getTo(), "/queue/private", chatMessage);
            } else {
                // 일반 메시지
                simpMessagingTemplate.convertAndSend("/topic/room." + chatMessage.getRoomId(), chatMessage);
            }
        }
        catch (Exception e) {
            e.getMessage();
        }
    }
}
