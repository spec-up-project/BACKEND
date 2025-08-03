package com.neekly_report.whirlwind.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "T_WEEKLY_REPORT")
@Getter
@Setter
@NoArgsConstructor
public class WeeklyReport extends Common {

    @Id
    @Column(name = "REPORT_ID", nullable = false, updatable = false)
    private String reportId;

    @Column(name = "CONTENT")
    private String content;
}
