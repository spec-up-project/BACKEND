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

    @Id @Comment("일정ID")
    @Column(nullable = false, updatable = false)
    private String T_PLAN_UID;

    @Comment("제목")
    private String PLAN_TITLE;

    @Comment("내용")
    private String PLAN_CONTENT;

    @Comment("예정시작일")
    @Temporal(TemporalType.TIMESTAMP)
    private Date PLAN_FROM_DATE;

    @Comment("예정종료일")
    @Temporal(TemporalType.TIMESTAMP)
    private Date PLAN_END_DATE;

    @Comment("삭제여부")
    private String DEL_YN;

    @ManyToOne
    @JoinColumn(name = "T_USER_UID")
    private User user;

}
