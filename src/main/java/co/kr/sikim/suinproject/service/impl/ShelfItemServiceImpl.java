package co.kr.sikim.suinproject.service.impl;

import co.kr.sikim.suinproject.domain.Book;
import co.kr.sikim.suinproject.domain.ShelfItem;
import co.kr.sikim.suinproject.domain.ShelfItemJoinRow;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemAddRequest;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemResponse;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemDeleteRequest;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemUpdateRequest;
import co.kr.sikim.suinproject.mapper.BookMapper;
import co.kr.sikim.suinproject.mapper.BookshelfMapper;
import co.kr.sikim.suinproject.mapper.ShelfItemMapper;
import co.kr.sikim.suinproject.service.ShelfItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShelfItemServiceImpl implements ShelfItemService {
    private final ShelfItemMapper sbMapper;
    private final BookshelfMapper bsMapper;
    private final BookMapper bMapper;

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<ShelfItemResponse> listShelfItems(Long bookshelfId) {
        if (!bsMapper.existBookshelfById(bookshelfId)) {
            throw new IllegalArgumentException("bookshelf not found");
        }

        return sbMapper.selectShelfItemsByShelfId(bookshelfId).stream().map(si -> {
            ShelfItemResponse r = new ShelfItemResponse();
            r.setShelfBookId(si.getShelfBookId());
            r.setBookshelfId(si.getBookshelfId());
            r.setBookId(si.getBookId());
            r.setTitle(si.getTitle());
            r.setAuthor(si.getAuthor());
            r.setPages(si.getPages());
            r.setAddedDatetime(si.getAddedDatetime() != null ? si.getAddedDatetime().format(DT) : null);
            return r;
        }).toList();
    }

    @Transactional
    @Override
    public ShelfItemResponse createShelfItem(ShelfItemAddRequest req) {
        if (!bsMapper.existBookshelfById(req.getBookshelfId())) {
            throw new IllegalArgumentException("bookshelf not found: " + req.getBookshelfId());
        }
        Book b = bMapper.selectBookById(req.getBookId());
        if (b == null) {
            throw new IllegalArgumentException("book not found: " + req.getBookId());
        }
        if (sbMapper.existsBookshelfById(req.getBookshelfId(), req.getBookId())) {
            throw new IllegalArgumentException("already exists in shelf: bookId=" + req.getBookId());
        }

        ShelfItem si = new ShelfItem();
        si.setBookshelfId(req.getBookshelfId());
        si.setBookId(req.getBookId());
        si.setSpineWidth(req.getSpineWidth());
        sbMapper.insertShelfItem(si);

        ShelfItemResponse r = new ShelfItemResponse();
        r.setShelfBookId(si.getShelfBookId());
        r.setBookshelfId(si.getBookshelfId());
        r.setBookId(si.getBookId());
        r.setTitle(b.getTitle());
        r.setAuthor(b.getAuthor());
        r.setPages(b.getPages());
        return r;
    }

    @Transactional
    @Override
    public ShelfItemResponse updateShelfItem(ShelfItemUpdateRequest req) {
        ShelfItemJoinRow current = sbMapper.selectShelfItemById(req.getShelfBookId());
        if (current == null) {
            throw new IllegalArgumentException("shelf item not found: " + req.getShelfBookId());
        }
        ShelfItem toUpdate = new ShelfItem();
        toUpdate.setShelfBookId(req.getShelfBookId());
        int updated = sbMapper.updateShelfItem(toUpdate);
        if (updated == 0) {
            throw new IllegalStateException("update failed: " + req.getShelfBookId());
        }

        // 갱신 후 응답용 조회 + Book 조인
        ShelfItemJoinRow fresh = sbMapper.selectShelfItemById(req.getShelfBookId());
        Book b = bMapper.selectBookById(fresh.getBookId());
        ShelfItemResponse r = new ShelfItemResponse();
        r.setShelfBookId(fresh.getShelfBookId());
        r.setBookshelfId(fresh.getBookshelfId());
        r.setBookId(fresh.getBookId());
        if (b != null) {
            r.setTitle(b.getTitle());
            r.setAuthor(b.getAuthor());
            r.setPages(b.getPages());
        }
        r.setAddedDatetime(fresh.getAddedDatetime() != null ? fresh.getAddedDatetime().format(DT) : null);
        return r;
    }

    @Transactional
    @Override
    public void deleteShelfItem(ShelfItemDeleteRequest req) {
        int deleted = sbMapper.deleteShelfItemById(req.getShelfBookId());
        if (deleted == 0) {
            throw new IllegalArgumentException("shelf item not found: " + req.getShelfBookId());
        }
    }
}
