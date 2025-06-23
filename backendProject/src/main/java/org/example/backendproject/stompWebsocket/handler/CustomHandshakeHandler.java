package org.example.backendproject.stompWebsocket.handler;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    // http -> websocket으로 전환될 때 요청 url에서 닉네임을 추출해 사용자를 식별하는 핸들러
    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        String nickname = getNickname(request.getURI().getQuery());
        System.out.println(nickname);
        return new StompPrincipal(nickname);
    }

    private String getNickname(String query){
        if (query == null || !query.contains("nickname=")){
            return "닉네임 없음";
        }
        else {
            return query.split("nickname=")[1];
        }
    }
}
