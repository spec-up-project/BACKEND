package com.neekly_report.whirlwind.schedule;

import com.neekly_report.whirlwind.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {

    List<Schedule> findByUser_tUserUid(String tUserUid);

    List<Schedule> findByUser_tUserUidAndStartTimeBetween(String tUserUid, LocalDateTime startTime, LocalDateTime endTime);

    List<Schedule> findByUser_tUserUidAndTitleContainingOrContentContaining(String tUserUid, String title, String content);

    @Query("SELECT s FROM Schedule s WHERE s.user.tUserUid = :tUserUid AND s.startTime >= :startTime ORDER BY s.startTime ASC")
    List<Schedule> findUpcomingSchedules(@Param("tUserUid") String tUserUid, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT s FROM Schedule s WHERE s.user.tUserUid = :tUserUid AND DATE(s.startTime) = DATE(:date)")
    List<Schedule> findByUserAndDate(@Param("tUserUid") String tUserUid, @Param("date") LocalDateTime date);

    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.user.tUserUid = :tUserUid AND s.startTime BETWEEN :startTime AND :endTime")
    long countByUserAndTimeBetween(@Param("tUserUid") String tUserUid, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}