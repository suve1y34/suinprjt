package co.kr.sikim.suinproject.service;

import co.kr.sikim.suinproject.mapper.BookMapper;
import co.kr.sikim.suinproject.mapper.ShelfMapper;
import co.kr.sikim.suinproject.service.impl.ShelfServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShelfServiceImplTest {

    @Mock private ShelfMapper shelfMapper;
    @Mock private BookMapper bookMapper;
    @Mock private BookInternalService biService;

    private ShelfServiceImpl sut;

    @BeforeEach
    void setUp() {
        sut = new ShelfServiceImpl(shelfMapper, bookMapper, biService);
    }
}
