package org.example.backendproject.purewebsocket.room.repository;


import org.example.backendproject.purewebsocket.room.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<ChatRoom,Long> {

    Optional<ChatRoom> findByRoomId(String roomId);
}
