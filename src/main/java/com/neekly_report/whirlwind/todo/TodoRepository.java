package com.neekly_report.whirlwind.todo;

import com.neekly_report.whirlwind.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, String> {

    List<Todo> findByUser_tUserUid(String tUserUid);

    List<Todo> findByUser_tUserUidAndStatus(String tUserUid, String status);

    List<Todo> findByUser_tUserUidAndPriority(String tUserUid, String priority);

    List<Todo> findByUser_tUserUidAndDueDateBeforeAndStatusNot(String tUserUid, LocalDateTime dueDate, String status);

    Optional<Todo> findBytTodoUidAndUser_tUserUid(String tTodoUid, String tUserUid);

    List<Todo> findByUser_tUserUidAndTitleContainingOrDescriptionContaining(String tUserUid, String title, String description);

    List<Todo> findByUser_tUserUidAndDueDateBetween(String tUserUid, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT t FROM Todo t WHERE t.user.tUserUid = :tUserUid AND t.status = :status AND t.modifyDate BETWEEN :startTime AND :endTime")
    List<Todo> findCompletedTodosBetween(@Param("tUserUid") String tUserUid, @Param("status") String status, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT COUNT(t) FROM Todo t WHERE t.user.tUserUid = :tUserUid AND t.status = :status")
    long countByUserAndStatus(@Param("tUserUid") String tUserUid, @Param("status") String status);

    @Query("SELECT COUNT(t) FROM Todo t WHERE t.user.tUserUid = :tUserUid AND t.priority = :priority AND t.status != 'COMPLETED'")
    long countByUserAndPriorityAndNotCompleted(@Param("tUserUid") String tUserUid, @Param("priority") String priority);
}
