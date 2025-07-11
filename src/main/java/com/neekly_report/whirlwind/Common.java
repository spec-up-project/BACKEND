package com.neekly_report.whirlwind;

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
    @Comment("이름")
    private Date createDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Comment("이름")
    private Date modifyDate;
}
