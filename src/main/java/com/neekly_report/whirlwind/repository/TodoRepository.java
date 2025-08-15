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

    Optional<Todo> findBytTodoUidAndUser_userUid(String tTodoUid, String userUid);

    List<Todo> findByUser_userUidAndTitleContainingOrDescriptionContaining(String userUid, String title, String description);

    List<Todo> findByUser_userUidAndDueDateBetween(String userUid, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Todo> findCompletedTodosBetween(@Param("userUid") String userUid, @Param("status") String status, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    long countByUserAndStatus(@Param("userUid") String userUid, @Param("status") String status);

    long countByUserAndPriorityAndNotCompleted(@Param("userUid") String userUid, @Param("priority") String priority);
}
