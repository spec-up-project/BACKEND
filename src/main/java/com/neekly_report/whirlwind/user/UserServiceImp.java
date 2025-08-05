package com.neekly_report.whirlwind.user;

import com.neekly_report.whirlwind.entity.User;

import java.util.List;

public interface UserServiceImp {
    List<User> getAllUsers();

    UserDTO.Response.UserRegisterResponse register(UserDTO.Request.UserRegisterRequest dto);

    UserDTO.Response.LoginResponse login(UserDTO.Request.LoginRequest request);
}
