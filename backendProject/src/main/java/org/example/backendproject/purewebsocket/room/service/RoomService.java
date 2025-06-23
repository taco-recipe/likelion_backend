package org.example.backendproject.purewebsocket.room.service;

import lombok.RequiredArgsConstructor;
import org.example.backendproject.purewebsocket.room.entity.ChatRoom;
import org.example.backendproject.purewebsocket.room.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // 이거 뭐임 이거하니깐 레포지토리 생성자 안해도 됨
public class RoomService {

    private final RoomRepository roomRepository;

    public ChatRoom createRoom(String roomId) {

        return roomRepository.findByRoomId(roomId)
                .orElseGet(()->{
                    ChatRoom newRoom = new ChatRoom();
                    newRoom.setRoomId(roomId);
                    return roomRepository.save(newRoom);
                });
    }

    public List<ChatRoom> findAllRooms() {
        return roomRepository.findAll();
    }
}
