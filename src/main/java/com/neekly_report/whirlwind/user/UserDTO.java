package com.neekly_report.whirlwind.user;

import com.neekly_report.whirlwind.common.CommonDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Builder
public class UserDTO {

    public static class Request {
        @Getter
        @Setter
        @NoArgsConstructor
        public static class UserRegisterRequest {
            private String userName;
            private String password;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class UserRequest {
            private String tUserUid;
        }
    }

    public static class Response {
        @Getter
        @Setter
        @NoArgsConstructor
        @Builder
        @AllArgsConstructor
        public static class UserRegisterResponse {
            private String tUserUid;
            private String userName;
            private String password;
            private String adminYn;
            private String useYn;
            private LocalDateTime createDate;
            private LocalDateTime modifyDate;
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
}
