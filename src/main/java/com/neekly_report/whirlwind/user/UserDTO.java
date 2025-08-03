package com.neekly_report.whirlwind.user;

import com.neekly_report.whirlwind.common.CommonDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Builder
public class UserDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserRequest {
        private String tUserUid;
    }

    @EqualsAndHashCode(callSuper = true)
    @Getter
    @NoArgsConstructor
    public static class UserResponse extends CommonDTO {
        private String tUserUid;
        private String userName;
        private String password;
        private String adminYn;
        private String useYn;
    }
}
