package com.neekly_report.whirlwind.dto;

import com.neekly_report.whirlwind.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;


@Builder
public class UserDto {
    @Getter
    public static class UserDetail extends CommonDto implements UserDetails {
        private final User user;

        public UserDetail(User user) {
            this.user = user;
        }

        public String getUserUid() {
            return user.getUserUid();
        }

        public String getEmail() {
            return user.getEmail();
        }

        public String getUserName() {
            return user.getUserName();
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            // 권한이 필요하다면 이후 구현 (ex. ROLE_USER, ROLE_ADMIN)
            return Collections.emptyList();
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getEmail(); // 로그인 ID로 email 사용
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return "Y".equals(user.getUseYn());
        }
    }

    public static class Request {
        @Getter
        @Setter
        @NoArgsConstructor
        public static class UserRegisterRequest {
            @NotBlank(message = "이름은 필수입니다.")
            private String userName;
            
            @NotBlank(message = "이메일은 필수입니다.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            private String email;

            @NotBlank(message = "비밀번호는 필수입니다.")
            @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
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
            private String userUid;
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
