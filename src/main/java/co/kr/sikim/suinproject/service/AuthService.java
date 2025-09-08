package co.kr.sikim.suinproject.service;

import co.kr.sikim.suinproject.domain.User;
import co.kr.sikim.suinproject.dto.auth.AuthLoginResponse;
import co.kr.sikim.suinproject.dto.auth.RegisterRequest;
import co.kr.sikim.suinproject.dto.auth.UserResponse;

public interface AuthService {
    AuthLoginResponse login(String email, String rawPassword);
    void resetPassword(Long userId); // 비밀번호 초기화

    UserResponse createUser(RegisterRequest req);

    // OAuth2 로그인 성공 시: email로 사용자 upsert
    // 신규: 기본 책장 생성 후 사용자 정보 반환
    User upsertOAuthUser(String email, String name);
}
