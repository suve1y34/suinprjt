package co.kr.sikim.suinproject.service.user;

import org.springframework.stereotype.Service;

import co.kr.sikim.suinproject.domain.User;
import co.kr.sikim.suinproject.dto.user.request.UserSignupRequest;
import co.kr.sikim.suinproject.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper uMapper;

    public void handleCreateUser(UserSignupRequest req) {
        User user = new User();
        user.setMemberId(req.getMemberId());
        user.setPassword(req.getPassword());
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setCountryCode(req.getCountryCode());
        user.setPhone(req.getPhone());
        user.setRoleNo(req.getRoleNo());
        user.setStatusNo(req.getStatusNo());

        uMapper.insertUser(user);
    }
}