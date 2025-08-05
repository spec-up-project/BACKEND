package com.neekly_report.whirlwind.user;

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

    @PostMapping("/register")
    public UserDTO.Response.UserRegisterResponse createUser(@Validated @RequestBody UserDTO.Request.UserRegisterRequest dto) {
        return userService.register(dto);
    }

    @PostMapping("/login")
    public UserDTO.Response.LoginResponse login(@Validated @RequestBody UserDTO.Request.LoginRequest dto) {
        return userService.login(dto);
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody UserDTO.Request.TokenReissueRequest dto) {
        String newAccessToken = userService.reissueToken(dto.getRefreshToken());
        return ResponseEntity.ok(newAccessToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        userService.logout(userId);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}
