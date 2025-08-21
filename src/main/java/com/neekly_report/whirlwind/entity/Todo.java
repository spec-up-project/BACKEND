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
@Table(name = "T_TODO")
public class Todo extends Common {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "T_TODO_UID", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    private String todoUid;

    @Comment("할일 제목")
    @Column(name = "TITLE", nullable = false)
    private String title;

    @Comment("할일 설명")
    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @Comment("우선순위")
    @Column(name = "PRIORITY") // HIGH, MEDIUM, LOW
    private String priority;

    @Comment("분류")
    @Column(name = "CATEGORY") // TODAY, THIS_WEEK, LATER
    private String category;

    @Comment("상태")
    @Column(name = "STATUS") // DONE, TODO
    private String status;

    @Comment("마감 기한")
    @Column(name = "DUE_DATE")
    private LocalDateTime dueDate;

    @Comment("원본 텍스트")
    @Column(name = "RAW_TEXT", columnDefinition = "TEXT")
    private String rawText;

    @Comment("입력 소스")
    @Column(name = "SOURCE")
    private String source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "T_USER_UID", nullable = false)
    private User user;
}

