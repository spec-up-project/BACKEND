package com.neekly_report.whirlwind.user;

import com.neekly_report.whirlwind.dto.UserDto;
import com.neekly_report.whirlwind.entity.User;

import java.util.List;

public interface UserServiceImp {
    List<User> getAllUsers();

    UserDto.Response.UserRegisterResponse register(UserDto.Request.UserRegisterRequest dto);

    UserDto.Response.LoginResponse login(UserDto.Request.LoginRequest request);
}
