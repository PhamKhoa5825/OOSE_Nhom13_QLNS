package com.example.qlns.Repository;

import com.example.qlns.Entity.TaskUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// =============================================
// TV3 - TaskUpdateRepository
// =============================================
public interface TaskUpdateRepository extends JpaRepository<TaskUpdate, Long> {
    List<TaskUpdate> findByTaskIdOrderByUpdatedAtDesc(Long taskId);
}
