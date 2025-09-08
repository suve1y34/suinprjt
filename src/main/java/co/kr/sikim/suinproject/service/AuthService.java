package co.kr.sikim.suinproject.service;

import co.kr.sikim.suinproject.dto.auth.AuthLoginResponse;
import co.kr.sikim.suinproject.dto.auth.RegisterRequest;
import co.kr.sikim.suinproject.dto.auth.UserResponse;

public interface AuthService {
    AuthLoginResponse login(String email, String rawPassword);
    void resetPassword(Long userId); // 비밀번호 초기화

    UserResponse createUser(RegisterRequest req);
}
