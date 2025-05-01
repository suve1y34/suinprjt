package co.kr.sikim.suinproject.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.kr.sikim.suinproject.domain.User;
import co.kr.sikim.suinproject.dto.user.request.ChangePasswordRequest;
import co.kr.sikim.suinproject.dto.user.request.LoginRequest;
import co.kr.sikim.suinproject.dto.user.request.ResetPasswordRequest;
import co.kr.sikim.suinproject.dto.user.request.SignupRequest;
import co.kr.sikim.suinproject.dto.user.request.UpdateMyInfoRequest;
import co.kr.sikim.suinproject.dto.user.response.DuplicateCheckResponse;
import co.kr.sikim.suinproject.dto.user.response.LoginResponse;
import co.kr.sikim.suinproject.dto.user.response.UserInfoResponse;
import co.kr.sikim.suinproject.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;

public interface UserService {
    void sinupUser(SignupRequest request);
    
    LoginResponse loginUser(LoginRequest request);

    DuplicateCheckResponse isIdDuplicate(String memberId);

    DuplicateCheckResponse isN0icknameDuplicate(String nickname);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(ChangePasswordRequest request);

    UserInfoResponse getUserInfo(String memberId);

    UserInfoResponse updateUserInfo(String memberId, UpdateMyInfoRequest request);

    void withdrawUser(String memberId);
}