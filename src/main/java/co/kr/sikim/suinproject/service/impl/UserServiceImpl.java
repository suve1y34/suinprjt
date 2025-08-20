package co.kr.sikim.suinproject.service.impl;

import co.kr.sikim.suinproject.domain.User;
import co.kr.sikim.suinproject.mapper.UserMapper;
import co.kr.sikim.suinproject.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> getUser(Long userId) {
        return Optional.ofNullable(userMapper.selectUserById(userId));
    }

    @Override
    public Optional<User> getUserByEmail(String userEmail) {
        return Optional.ofNullable(userMapper.selectUserByEmail(userEmail));
    }

    @Override
    public boolean checkDuplicatedUserEmail(String userEmail) {
        return userMapper.existsUserByUserEmail(userEmail);
    }
}
