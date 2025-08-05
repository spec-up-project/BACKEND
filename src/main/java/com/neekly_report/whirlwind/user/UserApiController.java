package com.neekly_report.whirlwind.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    public UserDTO.Response.UserRegisterResponse register(@Validated @RequestBody UserDTO.Request.UserRegisterRequest dto) {
        return userService.register(dto);
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public UserDTO.Response.LoginResponse login(@Validated @RequestBody UserDTO.Request.LoginRequest dto) {
        return userService.login(dto);
    }

    @Operation(summary = "Access Token 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody UserDTO.Request.TokenReissueRequest dto) {
        String newAccessToken = userService.reissueToken(dto.getRefreshToken());
        return ResponseEntity.ok(newAccessToken);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        userService.logout(userId);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}
