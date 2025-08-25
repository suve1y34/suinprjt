package co.kr.sikim.suinproject.service.impl;

import co.kr.sikim.suinproject.domain.User;
import co.kr.sikim.suinproject.mapper.UserMapper;
import co.kr.sikim.suinproject.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper uMapper;
    public UserServiceImpl(UserMapper userMapper) {
        this.uMapper = userMapper;
    }

    @Override
    public Optional<User> getUser(Long userId) {
        return Optional.ofNullable(uMapper.selectUserById(userId));
    }

    @Override
    public Optional<User> getUserByEmail(String userEmail) {
        return Optional.ofNullable(uMapper.selectUserByEmail(userEmail));
    }

    @Override
    public boolean checkDuplicatedUserEmail(String userEmail) {
        return uMapper.existsUserByUserEmail(userEmail);
    }
}
