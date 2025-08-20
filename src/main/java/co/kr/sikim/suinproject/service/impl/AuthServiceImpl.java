package co.kr.sikim.suinproject.service.impl;

import co.kr.sikim.suinproject.config.JwtTokenProvider;
import co.kr.sikim.suinproject.domain.User;
import co.kr.sikim.suinproject.dto.auth.AuthLoginResponse;
import co.kr.sikim.suinproject.dto.auth.UserResponse;
import co.kr.sikim.suinproject.exception.UnauthorizedException;
import co.kr.sikim.suinproject.service.AuthService;
import co.kr.sikim.suinproject.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(UserService userService,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public AuthLoginResponse login(String email, String rawPassword) {
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(rawPassword, user.getUserPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getUserId(), user.getUserEmail());

        UserResponse userDto = new UserResponse(
                user.getUserId(),
                user.getUserName(),
                user.getUserEmail(),
                user.getNickname()
        );
        return new AuthLoginResponse(token, userDto);
    }
}