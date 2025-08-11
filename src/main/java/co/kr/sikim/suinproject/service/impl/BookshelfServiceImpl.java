package co.kr.sikim.suinproject.service.impl;

import co.kr.sikim.suinproject.domain.Bookshelf;
import co.kr.sikim.suinproject.dto.shelf.BookshelfResponse;
import co.kr.sikim.suinproject.mapper.BookshelfMapper;
import co.kr.sikim.suinproject.service.BookshelfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class BookshelfServiceImpl implements BookshelfService {
    private final BookshelfMapper bsMapper;
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public BookshelfResponse getShelf(Long userId) {
        if (!bsMapper.existBookshelfById(userId))
            throw new IllegalArgumentException("bookshelf not found");

        Bookshelf bs = bsMapper.selectBookshelfById(userId);
        int count = bsMapper.countShelfItems(userId);

        BookshelfResponse res = new BookshelfResponse();
        res.setBookshelfId(bs.getBookshelfId());
        res.setUserId(bs.getUserId());
        res.setItemCount(count);

        return res;
    }
}
