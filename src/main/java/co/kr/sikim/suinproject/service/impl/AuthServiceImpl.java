package co.kr.sikim.suinproject.service.impl;

import co.kr.sikim.suinproject.config.JwtTokenProvider;
import co.kr.sikim.suinproject.domain.User;
import co.kr.sikim.suinproject.dto.auth.AuthLoginResponse;
import co.kr.sikim.suinproject.dto.auth.UserResponse;
import co.kr.sikim.suinproject.exception.UnauthorizedException;
import co.kr.sikim.suinproject.mapper.UserMapper;
import co.kr.sikim.suinproject.service.AuthService;
import co.kr.sikim.suinproject.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userSer;
    private final UserMapper uMapper;
    private final PasswordEncoder pwEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(UserService userService,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.userSer = userService;
        this.uMapper = userMapper;
        this.pwEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public AuthLoginResponse login(String email, String rawPassword) {
        User user = userSer.getUserByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!pwEncoder.matches(rawPassword, user.getUserPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getUserId(), user.getUserEmail());

        UserResponse userDto = new UserResponse();
        userDto.setUserId(user.getUserId());
        userDto.setEmail(user.getUserEmail());
        userDto.setUserName(user.getUserName());
        userDto.setNickname(user.getNickname());
        return new AuthLoginResponse(token, userDto);
    }

    @Transactional
    @Override
    public void resetPassword(Long userId) {
        String hash = pwEncoder.encode("0000");
        int n = uMapper.updateUserPasswordById(userId, hash);
        if (n == 0) throw new IllegalArgumentException("user not found: " + userId);

        try {
            uMapper.updateUserMustChangePasswordFlagById(userId, true);
        } catch (Exception e) {}
    }
}