package com.neekly_report.whirlwind.plan;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "T_PLAN")
public class Plan {

    @Id
    @Comment("일정ID")
    private String tPlanUid;

    @Comment("제목")
    private String planTitle;

    @Comment("내용")
    private String planContent;

    @Comment("예정시작일")
    @Temporal(TemporalType.TIMESTAMP)
    private Date planFromDate;

    @Comment("예정종료일")
    @Temporal(TemporalType.TIMESTAMP)
    private Date planEndDate;

    @Comment("삭제여부")
    private String delYn;

    @Temporal(TemporalType.TIMESTAMP)
    @Comment("생성일")
    private Date createDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Comment("수정일")
    private Date modifyDate;

}
