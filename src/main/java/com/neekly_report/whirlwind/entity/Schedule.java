package com.neekly_report.whirlwind.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_SCHEDULE")
public class Schedule extends Common {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "T_SCHEDULE_UID", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    private String tScheduleUid;

    @Comment("일정 제목")
    @Column(name = "TITLE", nullable = false)
    private String title;

    @Comment("내용")
    @Column(name = "CONTENT")
    private String content;

    @Comment("시작 시간")
    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Comment("종료 시간")
    @Column(name = "END_TIME")
    private LocalDateTime endTime;

    @Comment("원본 입력 텍스트")
    @Column(name = "RAW_TEXT", columnDefinition = "TEXT")
    private String rawText;

    @Comment("입력 소스")
    @Column(name = "SOURCE") // TEXT, FILE 등
    private String source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "T_USER_UID", nullable = false)
    private User user;
}

