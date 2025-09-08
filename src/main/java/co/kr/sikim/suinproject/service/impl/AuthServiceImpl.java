package co.kr.sikim.suinproject.service.impl;

import co.kr.sikim.suinproject.config.JwtTokenProvider;
import co.kr.sikim.suinproject.domain.Bookshelf;
import co.kr.sikim.suinproject.domain.User;
import co.kr.sikim.suinproject.dto.auth.AuthLoginResponse;
import co.kr.sikim.suinproject.dto.auth.RegisterRequest;
import co.kr.sikim.suinproject.dto.auth.UserResponse;
import co.kr.sikim.suinproject.exception.ConflictException;
import co.kr.sikim.suinproject.exception.UnauthorizedException;
import co.kr.sikim.suinproject.mapper.ShelfMapper;
import co.kr.sikim.suinproject.mapper.UserMapper;
import co.kr.sikim.suinproject.service.AuthService;
import co.kr.sikim.suinproject.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userSer;
    private final UserMapper uMapper;
    private final ShelfMapper sMapper;
    private final PasswordEncoder pwEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AuthServiceImpl(UserService userService,
                           UserMapper userMapper,
                           ShelfMapper shelfMapper,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.userSer = userService;
        this.uMapper = userMapper;
        this.sMapper = shelfMapper;
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

    @Transactional
    @Override
    public UserResponse createUser(RegisterRequest req) {
        // 중복 검사
        if (uMapper.existsUserByUserEmail(req.getEmail())) {
            throw new ConflictException("email already in use");
        }
        if (req.getNickname() != null && !req.getNickname().isBlank()) {
            if (uMapper.existsUserByNickname(req.getNickname())) {
                throw new ConflictException("nickname already in use");
            }
        }

        User u = new User();
        u.setUserEmail(req.getEmail());
        u.setUserPassword(pwEncoder.encode(req.getPassword()));
        u.setUserName(req.getUserName());
        u.setNickname(req.getNickname());

        int inserted = uMapper.insertUser(u);
        if (inserted != 1 || u.getUserId() == null) {
            throw new IllegalStateException("failed to insert user");
        }

        Bookshelf shelf = new Bookshelf();
        shelf.setUserId(u.getUserId());
        int s = sMapper.insertBookshelf(shelf);
        if (s != 1 || shelf.getBookshelfId() == null) {
            throw new IllegalStateException("failed to create default bookshelf");
        }

        // 최신 사용자 조회 후 DTO 매핑
        User saved = uMapper.selectUserById(u.getUserId());
        UserResponse dto = new UserResponse();
        dto.setUserId(saved.getUserId());
        dto.setEmail(saved.getUserEmail());
        dto.setUserName(saved.getUserName());
        dto.setNickname(saved.getNickname());
        dto.setCreatedDatetime(saved.getCreatedDatetime() != null ? DF.format(saved.getCreatedDatetime()) : null);
        dto.setModifiedDatetime(saved.getModifiedDatetime() != null ? DF.format(saved.getModifiedDatetime()) : null);
        return dto;
    }

    @Transactional
    @Override
    public User upsertOAuthUser(String email, String name) {
        User existing = uMapper.selectUserByEmail(email);
        if (existing != null) return existing;

        User u = new User();
        u.setUserEmail(email);
        u.setUserPassword(pwEncoder.encode("OAUTH2-" + UUID.randomUUID()));
        u.setUserName((name == null || name.isBlank()) ? email : name);
        u.setNickname(null);
        u.setCreatedDatetime(LocalDateTime.now());
        u.setModifiedDatetime(LocalDateTime.now());
        uMapper.insertUser(u);

        Bookshelf shelf = new Bookshelf();
        shelf.setUserId(u.getUserId());
        int s = sMapper.insertBookshelf(shelf);
        if (s != 1 || shelf.getBookshelfId() == null) {
            throw new IllegalStateException("failed to create default bookshelf");
        }

        return u;
    }
}