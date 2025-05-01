package co.kr.sikim.suinproject.mapper.user;

import org.apache.ibatis.annotations.Mapper;

import co.kr.sikim.suinproject.domain.User;
import co.kr.sikim.suinproject.dto.user.request.ChangePasswordRequest;
import co.kr.sikim.suinproject.dto.user.request.LoginRequest;
import co.kr.sikim.suinproject.dto.user.request.ResetPasswordRequest;
import co.kr.sikim.suinproject.dto.user.request.SignupRequest;
import co.kr.sikim.suinproject.dto.user.request.UpdateMyInfoRequest;
import co.kr.sikim.suinproject.dto.user.response.DuplicateCheckResponse;
import co.kr.sikim.suinproject.dto.user.response.LoginResponse;
import co.kr.sikim.suinproject.dto.user.response.UserInfoResponse;

@Mapper
public interface UserMapper {
    void signupUser(SignupRequest request);

    User loginUser(LoginRequest request);

    int isIdDuplicate(String memberId);

    int isNicknameDuplicate(String nickname);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(ChangePasswordRequest request);

    User getUserInfo(String memberId);

    User updateUserInfo(String memberId, UpdateMyInfoRequest request);

    void withdrawUser(String memberId);
}
