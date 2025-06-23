package org.example.backendproject.purewebsocket.config;

import org.example.backendproject.purewebsocket.handler.ChatWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

//@Configuration
//@EnableWebSocket //웹소켓 사용할거임 이라고 알려주는 어노테이션
// stomp의 webSocketConfig와 충돌이 일어나서 주석처리
public class webSocketConfig implements WebSocketConfigurer {


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWebSocketHandler(),"/ws-chat")
                .setAllowedOrigins("*"); //어떤 출처(Origin)에서 오는 WebSocket 요청이든 전부 허용
                // ws-chat 엔드포인트로 요청할 수 있는지 결정하는 보안 정책 설 CORS
                //* <- 모든 도메인 접근 가능

    }
}
