package com.neekly_report.whirlwind.repository;

import com.neekly_report.whirlwind.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, String> {

    List<Todo> findByUser_userUid(String userUid);

    List<Todo> findByUser_userUidAndStatus(String userUid, String status);

    List<Todo> findByUser_userUidAndPriority(String userUid, String priority);

    List<Todo> findByUser_userUidAndDueDateBeforeAndStatusNot(String userUid, LocalDateTime dueDate, String status);

    Optional<Todo> findByTodoUidAndUser_userUid(String todoUid, String userUid);

    List<Todo> findByUser_userUidAndTitleContainingOrDescriptionContaining(String userUid, String title, String description);

    List<Todo> findByUser_userUidAndDueDateBetween(String userUid, LocalDateTime startDate, LocalDateTime endDate);
}
