package com.neekly_report.whirlwind.user;

import com.neekly_report.whirlwind.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserServiceImp {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDTO.Response.UserRegisterResponse createUser(UserDTO.Request.UserRegisterRequest dto) {
        User user = User.builder()
                .tUserUid(UUID.randomUUID().toString())
                .userName(dto.getUserName())
                .password(dto.getPassword())
                .adminYn("N")
                .useYn("Y")
                .build();
        User responseUser = userRepository.save(user);

        return UserDTO.Response.UserRegisterResponse.builder()
                .tUserUid(responseUser.getTUserUid())
                .userName(responseUser.getUserName())
                .password(responseUser.getPassword())
                .adminYn(responseUser.getAdminYn())
                .useYn(responseUser.getUseYn())
                .createDate(responseUser.getCreateDate())
                .modifyDate(responseUser.getModifyDate())
                .build();
    }


}
