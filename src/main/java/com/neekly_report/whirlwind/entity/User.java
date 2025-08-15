package com.neekly_report.whirlwind.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_USER")
public class User extends Common {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "T_USER_UID", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    private String userUid;

    @Comment("이름")
    @Column(name = "USER_NAME", nullable = false)
    private String userName;

    @Comment("이메일")
    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Comment("비밀번호")
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Comment("관리자 여부")
    @Column(name = "ADMIN_YN")
    private String adminYn;

    @Comment("사용 여부")
    @Column(name = "USE_YN")
    private String useYn;

    // 양방향 연관관계
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Todo> todos = new ArrayList<>();
}
