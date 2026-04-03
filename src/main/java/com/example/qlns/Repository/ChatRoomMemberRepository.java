package com.example.qlns.Repository;

import com.example.qlns.Entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// =============================================
// TV4 - ChatRoomMemberRepository
// =============================================
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    List<ChatRoomMember> findByUserId(Long userId);

    List<ChatRoomMember> findByRoomId(Long roomId);

    boolean existsByRoomIdAndUserId(Long roomId, Long userId);
}
