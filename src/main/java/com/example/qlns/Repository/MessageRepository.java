package com.example.qlns.Repository;

import com.example.qlns.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// =============================================
// TV4 - MessageRepository
// =============================================
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRoomIdOrderByCreatedAtAsc(Long roomId);

    // Polling: chỉ lấy tin nhắn mới hơn lastId
    @Query("SELECT m FROM Message m WHERE m.room.id = :roomId AND m.id > :lastId ORDER BY m.createdAt ASC")
    List<Message> findNewMessages(@Param("roomId") Long roomId,
                                  @Param("lastId") Long lastMessageId);

    List<Message> findByRoomIdAndIdGreaterThan(Long roomId, Long lastId);
}
