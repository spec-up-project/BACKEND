package com.neekly_report.whirlwind.api;

import com.neekly_report.whirlwind.dto.UserDto;
import com.neekly_report.whirlwind.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {
    private final UserService userService;

    @Operation(summary = "회원가입")
    @PostMapping("/register")
    public UserDto.Response.UserRegisterResponse register(@Validated @RequestBody UserDto.Request.UserRegisterRequest dto) {
        return userService.register(dto);
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public UserDto.Response.LoginResponse login(@Validated @RequestBody UserDto.Request.LoginRequest dto) {
        return userService.login(dto);
    }

    @Operation(summary = "Access Token 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody UserDto.Request.TokenReissueRequest dto) {
        String newAccessToken = userService.reissueToken(dto.getRefreshToken());
        return ResponseEntity.ok(newAccessToken);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        UserDto.UserDetail userDetail = (UserDto.UserDetail) authentication.getPrincipal();
        String userId = userDetail.getTUserUid();
        userService.logout(userId);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    public ResponseEntity<?> subjectInsert() {
        return ResponseEntity.ok("");
    }

}
