package com.neekly_report.whirlwind.common;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.util.Date;

@Getter
@Setter
public class Common {
    @Temporal(TemporalType.TIMESTAMP)
    @Comment("생성일")
    private Date createDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Comment("수정일")
    private Date modifyDate;
}
