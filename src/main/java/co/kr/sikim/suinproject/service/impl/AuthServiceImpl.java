package co.kr.sikim.suinproject.service.impl;

import co.kr.sikim.suinproject.common.NicknameGenerator;
import co.kr.sikim.suinproject.domain.Bookshelf;
import co.kr.sikim.suinproject.domain.User;
import co.kr.sikim.suinproject.mapper.ShelfMapper;
import co.kr.sikim.suinproject.mapper.UserMapper;
import co.kr.sikim.suinproject.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper uMapper;
    private final ShelfMapper sMapper;
    private final PasswordEncoder pwEncoder;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AuthServiceImpl(UserMapper userMapper,
                           ShelfMapper shelfMapper,
                           PasswordEncoder passwordEncoder) {
        this.uMapper = userMapper;
        this.sMapper = shelfMapper;
        this.pwEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public User upsertOAuthUser(String email, String name) {
        User existing = uMapper.selectUserByEmail(email);

        if (existing != null) {
            if (existing.getNickname() == null || existing.getNickname().isBlank()) {
                existing.setNickname(allocNickname(email));
                uMapper.updateUser(existing); // 닉/이름 등 기본 정보만 갱신하는 쿼리 사용
            }

            if ((existing.getUserName() == null || existing.getUserName().isBlank()) && name != null && !name.isBlank()) {
                existing.setUserName(name.trim());
                uMapper.updateUser(existing);
            }
            return existing;
        }
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

    private String allocNickname(String providerIdOrEmail) {
        // DB 중복 검사 람다
        java.util.function.Predicate<String> exists = name -> uMapper.existsUserByNickname(name);

        return NicknameGenerator.generateUnique(providerIdOrEmail, NicknameGenerator.Strategy.ALLITERATION, exists);
    }
}