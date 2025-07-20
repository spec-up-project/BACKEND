package com.neekly_report.whirlwind.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "T_USER")
public class User {

    @Id
    @Comment("사용자UID")
    private String tUserUid;
    @Comment("이름")
    private String userName;
    @Comment("비밀번호")
    private String password;
    @Comment("관리자 여부")
    private String adminYn;
    @Comment("사용 여부")
    private String useYn;

    @Temporal(TemporalType.TIMESTAMP)
    @Comment("생성일")
    private Date createDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Comment("수정일")
    private Date modifyDate;

}
