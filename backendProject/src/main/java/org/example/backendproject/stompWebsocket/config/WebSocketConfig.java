package org.example.backendproject.stompWebsocket.config;

import org.example.backendproject.stompWebsocket.handler.CustomHandshakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // Prefix <- 메세지의 목적지를 구분하기 위한 접두어

        /** 구동용 prefix**/
        // /topic 일반 채팅을 받을 접두어
        // /queue 귓말을 받을 접두어
        registry.enableSimpleBroker("/topic","/queue"); // 구독용 경로 서버 -> 클라이언트


        /** 전송용 prfix **/
        // 클라이언트가 서버에게 메세지를 보내는 접두어
        /**클라이언트가 서버에 메세지를 보낼 떄 사용하는 경로 접두어**/
        registry.setApplicationDestinationPrefixes("/app"); // 전송용 경로 클라이언트 -> 서버

        // /user 특정 사용자에게 메세지를 보낼 접두어
        /**서버가 특정 사용자에게 메세지를 보낼때 틀라이언트가 구독할 경로 접두어**/
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setHandshakeHandler(new CustomHandshakeHandler())
                .setAllowedOriginPatterns("*");
    }


}
