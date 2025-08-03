package com.neekly_report.whirlwind.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "T_SUMMARY_LOG")
@Getter
@Setter
@NoArgsConstructor
public class SummaryLog extends Common {

    @Id
    @Column(name = "LOG_ID", nullable = false, updatable = false)
    private String logId;

    @Column(name = "CONTENT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
}

