package co.kr.sikim.suinproject.service;

import co.kr.sikim.suinproject.dto.auth.AuthLoginResponse;

public interface AuthService {
    AuthLoginResponse login(String email, String rawPassword);
}
