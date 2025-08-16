package com.neekly_report.whirlwind.repository;

import com.neekly_report.whirlwind.entity.WeeklyReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<WeeklyReport, String> {
    List<WeeklyReport> findWeeklyReportByUser_UserUid(String userUserUid);
    WeeklyReport findByReportUid(String reportUid);
}
