package com.neekly_report.whirlwind.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "T_PLAN")
@NoArgsConstructor
public class Plan extends Common {

    @Id
    @Comment("일정ID")
    @Column(name = "T_PLAN_UID", nullable = false, updatable = false)
    private String tPlanUid;

    @Comment("제목")
    @Column(name = "PLAN_TITLE")
    private String planTitle;

    @Comment("내용")
    @Column(name = "PLAN_CONTENT")
    private String planContent;

    @Comment("예정시작일")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PLAN_FROM_DATE")
    private Date planFromDate;

    @Comment("예정종료일")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PLAN_END_DATE")
    private Date planEndDate;

    @Comment("삭제여부")
    @Column(name = "DEL_YN")
    private String delYn;

    @ManyToOne
    @JoinColumn(name = "T_USER_UID")
    private User user;
}
