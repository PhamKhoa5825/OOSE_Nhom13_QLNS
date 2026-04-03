package com.example.qlns.Repository;

import com.example.qlns.Entity.ChatRoom;
import com.example.qlns.Enum.ChatRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// =============================================
// TV4 - ChatRoomRepository
// =============================================
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByType(ChatRoomType type);

    Optional<ChatRoom> findByDepartmentId(Long departmentId);

    // Tìm phòng chat riêng giữa 2 user
    @Query("SELECT cr FROM ChatRoom cr JOIN ChatRoomMember m1 ON m1.room = cr " +
            "JOIN ChatRoomMember m2 ON m2.room = cr " +
            "WHERE cr.type = 'PRIVATE' AND m1.user.id = :uid1 AND m2.user.id = :uid2")
    Optional<ChatRoom> findPrivateRoom(@Param("uid1") Long userId1,
                                       @Param("uid2") Long userId2);
}
