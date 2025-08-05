package com.neekly_report.whirlwind.schedule;

import com.neekly_report.whirlwind.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    List<Schedule> findByUser_tUserUid(String tUserUid);
}
