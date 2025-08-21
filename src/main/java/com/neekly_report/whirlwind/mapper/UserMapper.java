package com.neekly_report.whirlwind.mapper;

import com.neekly_report.whirlwind.dto.UserDto;
import com.neekly_report.whirlwind.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // RegisterRequest → Entity
    User toEntity(UserDto.Request.UserRegisterRequest dto);

    // Entity → RegisterResponse
    UserDto.Response.UserRegisterResponse toRegisterResponse(User entity);

    // Entity → LoginResponse (일부 필드만 사용)
    default UserDto.Response.LoginResponse toLoginResponse(User entity, String accessToken, String refreshToken) {
        return new UserDto.Response.LoginResponse(
                accessToken,
                refreshToken,
                entity.getUserName(),
                entity.getEmail()
        );
    }
}
