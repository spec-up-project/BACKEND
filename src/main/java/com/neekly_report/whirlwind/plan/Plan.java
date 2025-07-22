package com.neekly_report.whirlwind.plan;

import com.neekly_report.whirlwind.common.Common;
import com.neekly_report.whirlwind.user.User;
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

    @ManyToOne
    @JoinColumn(name = "tUserUid")
    private User user;

}
