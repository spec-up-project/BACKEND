package com.neekly_report.whirlwind.user;

import com.neekly_report.whirlwind.common.CommonDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
            @NotBlank(message = "이메일은 필수입니다.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            private String email;
            private String password;
        }

        @Getter @Setter @NoArgsConstructor
        public static class LoginRequest {
            @NotBlank
            @Email
            private String email;
            @NotBlank
            private String password;
        }

        @Getter @Setter
        public static class TokenReissueRequest {
            private String refreshToken;
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
            private String email;
            private String password;
            private String adminYn;
            private String useYn;
            private LocalDateTime createDate;
            private LocalDateTime modifyDate;
        }

        @Getter @Setter @AllArgsConstructor
        public static class LoginResponse {
            private String accessToken;
            private String refreshToken;
            private String userName;
            private String email;
        }
    }
}
