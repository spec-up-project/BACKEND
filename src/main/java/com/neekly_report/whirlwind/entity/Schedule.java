package com.neekly_report.whirlwind.entity;

import com.neekly_report.whirlwind.dto.ScheduleDto;
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
@ToString(exclude = {"scheduleUid", "user", "rawText"})
public class Schedule extends Common {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "T_SCHEDULE_UID", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    private String scheduleUid;

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

    @Comment("종일 여부")
    @Column(name = "IS_ALL_DAY")
    private Boolean isAllDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "T_USER_UID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "T_CATEGORY_UID", nullable = false)
    private Category category;

    public ScheduleDto.Response.CalendarEvent toScheduleEvent() {
        return ScheduleDto.Response.CalendarEvent.builder()
                .scheduleUid(getScheduleUid())
                .title(getTitle())
                .content(getContent())
                .startTime(getStartTime())
                .endTime(getEndTime())
                .source(getSource())
                .rawText(getRawText())
                .createDate(getCreateDate())
                .modifyDate(getModifyDate())
                .build();
    }

    public ScheduleDto.Response.ScheduleEvent toSchedulePreview() {
        return ScheduleDto.Response.ScheduleEvent.builder()
                .title(getTitle())
                .content(getContent())
                .startTime(getStartTime())
                .endTime(getEndTime())
                .rawText(getRawText())
                .build();
    }

    // DTO 변환 메서드들
    public ScheduleDto.Response.CalendarEvent toCalendarEvent() {
        return ScheduleDto.Response.CalendarEvent.builder()
                .scheduleUid(getScheduleUid())
                .title(getTitle())
                .content(getContent())
                .startTime(getStartTime())
                .endTime(getEndTime())
                .source(getSource())
                .rawText(getRawText())
                .createDate(getCreateDate())
                .modifyDate(getModifyDate())
                .build();
    }

    public ScheduleDto.Response.ScheduleEvent toEventPreview() {
        return ScheduleDto.Response.ScheduleEvent.builder()
                .title(getTitle())
                .content(getContent())
                .startTime(getStartTime())
                .endTime(getEndTime())
                .rawText(getRawText())
                .build();
    }
}

