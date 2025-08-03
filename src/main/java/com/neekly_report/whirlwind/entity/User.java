package com.neekly_report.whirlwind.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "T_USER")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends Common {

    @Id
    @Comment("사용자UID")
    @Column(name = "T_USER_UID", nullable = false, updatable = false)
    private String tUserUid;

    @Comment("이름")
    @Column(name = "USER_NAME")
    private String userName;

    @Comment("비밀번호")
    @Column(name = "PASSWORD")
    private String password;

    @Comment("관리자 여부")
    @Column(name = "ADMIN_YN")
    private String adminYn;

    @Comment("사용 여부")
    @Column(name = "USE_YN")
    private String useYn;
}
