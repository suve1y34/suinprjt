package co.kr.sikim.suinproject.service.user;

import org.springframework.stereotype.Service;

import co.kr.sikim.suinproject.dto.user.request.ChangePasswordRequest;
import co.kr.sikim.suinproject.dto.user.request.LoginRequest;
import co.kr.sikim.suinproject.dto.user.request.ResetPasswordRequest;
import co.kr.sikim.suinproject.dto.user.request.SignupRequest;
import co.kr.sikim.suinproject.dto.user.request.UpdateMyInfoRequest;
import co.kr.sikim.suinproject.dto.user.response.DuplicateCheckResponse;
import co.kr.sikim.suinproject.dto.user.response.LoginResponse;
import co.kr.sikim.suinproject.dto.user.response.UserInfoResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {@Override
    public void sinupUser(SignupRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sinupUser'");
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loginUser'");
    }

    @Override
    public DuplicateCheckResponse isIdDuplicate(String memberId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isIdDuplicate'");
    }

    @Override
    public DuplicateCheckResponse isN0icknameDuplicate(String nickname) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isN0icknameDuplicate'");
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resetPassword'");
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
    }

    @Override
    public UserInfoResponse getUserInfo(String memberId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserInfo'");
    }

    @Override
    public UserInfoResponse updateUserInfo(String memberId, UpdateMyInfoRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUserInfo'");
    }

    @Override
    public void withdrawUser(String memberId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'withdrawUser'");
    }
    
}
