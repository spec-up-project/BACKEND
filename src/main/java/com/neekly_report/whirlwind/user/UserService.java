package com.neekly_report.whirlwind.user;

import com.neekly_report.whirlwind.common.jwt.JwtUtil;
import com.neekly_report.whirlwind.common.jwt.RefreshToken;
import com.neekly_report.whirlwind.common.jwt.RefreshTokenRepository;
import com.neekly_report.whirlwind.dto.UserDto;
import com.neekly_report.whirlwind.entity.User;
import com.neekly_report.whirlwind.exception.EmailAlreadyUsedException;
import com.neekly_report.whirlwind.exception.UsernameAlreadyUsedException;
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

        User user = User.builder()
                .userName(dto.getUserName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .adminYn("N")
                .useYn("Y")
                .build();
        User responseUser = userRepository.save(user);

        return UserDto.Response.UserRegisterResponse.builder()
                .tUserUid(responseUser.getTUserUid())
                .userName(responseUser.getUserName())
                .email(responseUser.getEmail())
                .password(responseUser.getPassword())
                .adminYn(responseUser.getAdminYn())
                .useYn(responseUser.getUseYn())
                .createDate(responseUser.getCreateDate())
                .modifyDate(responseUser.getModifyDate())
                .build();
    }

    public UserDto.Response.LoginResponse login(UserDto.Request.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateToken(user.getTUserUid(), user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getTUserUid(), user.getEmail());

        // RefreshToken 저장(갱신)
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .tUserUid(user.getTUserUid())
                        .refreshToken(refreshToken)
                        .build()
        );

        return new UserDto.Response.LoginResponse(accessToken, refreshToken, user.getUserName(), user.getEmail());
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
