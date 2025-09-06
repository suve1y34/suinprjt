package co.kr.sikim.suinproject.service.impl;

import co.kr.sikim.suinproject.domain.User;
import co.kr.sikim.suinproject.dto.auth.UserResponse;
import co.kr.sikim.suinproject.dto.auth.UserUpdateRequest;
import co.kr.sikim.suinproject.mapper.UserMapper;
import co.kr.sikim.suinproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    @Override
    public UserResponse updateUser(Long userId, UserUpdateRequest req) {
        User current = uMapper.selectUserById(userId);
        if (current == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");

        // 닉네임 중복 검사(본인 제외)
        if (req.getNickname() != null && !req.getNickname().isBlank()) {
            boolean dup = uMapper.existsUserByNicknameAndNotUserId(req.getNickname(), userId);
            if (dup) throw new ResponseStatusException(HttpStatus.CONFLICT, "nickname already exists");
        }

        User toUpdate = new User();
        toUpdate.setUserId(userId);
        if (req.getNickname() != null && !req.getNickname().isBlank()) toUpdate.setNickname(req.getNickname().trim());

        int n = uMapper.updateUser(toUpdate);
        if (n == 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nothing to update");

        User fresh = uMapper.selectUserById(userId);

        UserResponse r = new UserResponse();
        r.setUserId(fresh.getUserId());
        r.setEmail(fresh.getUserEmail());
        r.setUserName(fresh.getUserName());
        r.setNickname(fresh.getNickname());
        r.setCreatedDatetime(fresh.getCreatedDatetime() != null ? fresh.getCreatedDatetime().format(DT) : null);
        r.setModifiedDatetime(fresh.getModifiedDatetime() != null ? fresh.getModifiedDatetime().format(DT) : null);
        return r;
    }
}
