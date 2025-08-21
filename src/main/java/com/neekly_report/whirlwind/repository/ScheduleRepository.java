package com.neekly_report.whirlwind.repository;

import com.neekly_report.whirlwind.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {

    List<Schedule> findByUser_userUid(String userUid);

    List<Schedule> findByUser_userUidAndStartTimeBetween(String userUid, LocalDateTime startTime, LocalDateTime endTime);

    Schedule findByScheduleUidAndUser_userUid(String scheduleUid, String userUid);

    List<Schedule> findByUser_userUidAndTitleContainingOrContentContaining(String userUid, String title, String content);

    @Modifying
    void deleteByScheduleUidAndUser_userUid(String scheduleUid, String userUserUid);
}