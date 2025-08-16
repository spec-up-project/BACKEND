package com.neekly_report.whirlwind.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "T_WEEKLY_REPORT")
public class WeeklyReport extends Common {

    @Id
    @UuidGenerator
    @GeneratedValue(generator = "UUID")
    @Column(name = "T_REPORT_UID", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    private String reportUid;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CONTENT", columnDefinition = "MEDIUMTEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "T_USER_UID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MAIN_CATEGORY_UID")
    private Category mainCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUB_CATEGORY_UID")
    private Category subCategory;
}
