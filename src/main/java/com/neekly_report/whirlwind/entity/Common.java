package com.neekly_report.whirlwind.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Common {

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Comment("생성일")
    @Column(updatable = false)
    private Date CREATE_DATE;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Comment("수정일")
    private Date MODIFY_DATE;
}
