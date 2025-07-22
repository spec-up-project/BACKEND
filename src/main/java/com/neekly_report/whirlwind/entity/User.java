package com.neekly_report.whirlwind.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "T_USER")
public class User extends Common {

    @Id @Comment("사용자UID")
    @Column(nullable = false, updatable = false)
    private String T_USER_UID;
    @Comment("이름")
    private String USER_NAME;
    @Comment("비밀번호")
    private String PASSWORD;
    @Comment("관리자 여부")
    private String ADMIN_YN;
    @Comment("사용 여부")
    private String USE_YN;

}
