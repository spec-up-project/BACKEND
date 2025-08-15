package com.neekly_report.whirlwind.service;

import com.neekly_report.whirlwind.entity.RefreshToken;
import com.neekly_report.whirlwind.repository.RefreshTokenRepository;
import com.neekly_report.whirlwind.dto.UserDto;
import com.neekly_report.whirlwind.entity.User;
import com.neekly_report.whirlwind.exception.EmailAlreadyUsedException;
import com.neekly_report.whirlwind.exception.UsernameAlreadyUsedException;
import com.neekly_report.whirlwind.mapper.UserMapper;
import com.neekly_report.whirlwind.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserDto.Response.UserRegisterResponse register(UserDto.Request.UserRegisterRequest dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyUsedException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByUserName(dto.getUserName())) {
            throw new UsernameAlreadyUsedException("이미 사용 중인 사용자 이름입니다.");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setAdminYn("N");
        user.setUseYn("Y");

        User responseUser = userRepository.save(user);
        return userMapper.toRegisterResponse(responseUser);
    }

    public UserDto.Response.LoginResponse login(UserDto.Request.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateToken(user.getUserUid(), user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserUid(), user.getEmail());

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .tUserUid(user.getUserUid())
                        .refreshToken(refreshToken)
                        .build()
        );

        return userMapper.toLoginResponse(user, accessToken, refreshToken);
    }

    public String reissueToken(String refreshToken) {
        RefreshToken saved = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 리프레시 토큰입니다."));

        Claims claims = jwtUtil.validateAndGetClaims(refreshToken);
        String tUserUid = claims.getSubject();
        String email = (String) claims.get("email");

        return jwtUtil.generateToken(tUserUid, email);
    }

    public void logout(String tUserUid) {
        refreshTokenRepository.deleteById(tUserUid);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일의 사용자를 찾을 수 없습니다."));
        return new UserDto.UserDetail(user);
    }
}
