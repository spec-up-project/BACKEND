package com.neekly_report.whirlwind.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "T_SUMMARY_LOG")
public class SummaryLog extends Common {

    @Id
    @Column(name = "T_LOG_UID", nullable = false, updatable = false)
    private String logId;

    @Column(name = "CONTENT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "T_USER_UID")
    private User user;
}

