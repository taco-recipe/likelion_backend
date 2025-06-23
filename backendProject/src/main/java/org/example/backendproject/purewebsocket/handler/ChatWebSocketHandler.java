package org.example.backendproject.purewebsocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backendproject.purewebsocket.dto.ChatMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    //세션 객체 synchronizedSet은
    //Collections.synchronizedSet <- 여러 스레드가 동시에 이 객체에 접근할 때 동시성 문제를
    private Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    //json -> java 객체로 || java 객체 -> json 변환해줌
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 방 번호와 세션묶음을 매핑해서 방들 생성
    private final Map<String,Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();


    //클라이언트가 웹소켓 세션에 접속했을 때 호출
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);


        sessions.add(session);
        System.out.println("접속된 클라이언트 세션 ID: "+ session.getId());
    }

    // 클라이언트가 보내는 메세지를 서버가 받았을 때 호출
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);

        // json -> java dto 객체
        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
        //payload  클라이언트가 서버로 보낸 “텍스트 메시지의 실제 내용(String)“을 가져오는 메서드

        String roomId = chatMessage.getRoomId(); // 클라이언트에게 받은 메세지에서 roomID를 추출

        if (!rooms.containsKey(roomId)) {// 방을 관리하는 객체에 현재 세션이 들어가있는 방이 있는지 확인
            rooms.put(roomId, ConcurrentHashMap.newKeySet()); //없으면 새로 만듬
        }
        rooms.get(roomId).add(session); // 해당 방 세션 추가

        for (WebSocketSession s : rooms.get(roomId)) {
//        for (WebSocketSession s : sessions) { //이건 방이 아니였을 때
            if (s.isOpen()) {
                //java 객체 -> 문자열
                s.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
                System.out.println("전송된 메세지 ="+chatMessage.getMessage());
            }


        }

    }

    // 클라이언트가 연결이 끊겼을 때 호출
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

        sessions.remove(session);

        //연결이 해제되면 소속되어있는 방에서 제고
        for (Set<WebSocketSession> room : rooms.values()) {
            room.remove(session);
        }
    }
}
