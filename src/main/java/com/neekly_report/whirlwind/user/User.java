package com.neekly_report.whirlwind.user;

import com.neekly_report.whirlwind.common.Common;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "T_USER")
public class User extends Common {

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

}
