package co.kr.sikim.suinproject.service.impl;

import co.kr.sikim.suinproject.domain.Book;
import co.kr.sikim.suinproject.domain.Bookshelf;
import co.kr.sikim.suinproject.domain.ShelfItem;
import co.kr.sikim.suinproject.domain.ShelfItemJoinRow;
import co.kr.sikim.suinproject.dto.shelf.BookshelfResponse;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemAddRequest;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemResponse;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemDeleteRequest;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemUpdateRequest;
import co.kr.sikim.suinproject.mapper.BookMapper;
import co.kr.sikim.suinproject.mapper.ShelfMapper;
import co.kr.sikim.suinproject.service.ShelfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShelfServiceImpl implements ShelfService {
    private final ShelfMapper sbMapper;
    private final BookMapper bMapper;

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public BookshelfResponse getShelf(Long userId) {
        Bookshelf bs = sbMapper.selectBookshelfById(userId);
        int count = sbMapper.countShelfItems(bs.getBookshelfId());

        if (!sbMapper.existBookshelfById(bs.getBookshelfId()))
            throw new IllegalArgumentException("bookshelf not found");

        BookshelfResponse res = new BookshelfResponse();
        res.setBookshelfId(bs.getBookshelfId());
        res.setUserId(bs.getUserId());
        res.setItemCount(count);

        return res;
    }

    @Override
    public List<ShelfItemResponse> listShelfItems(Long bookshelfId) {
        if (!sbMapper.existBookshelfById(bookshelfId)) {
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
        if (!sbMapper.existBookshelfById(req.getBookshelfId())) {
            throw new IllegalArgumentException("bookshelf not found: " + req.getBookshelfId());
        }

        Long bookId = req.getBookId();

        Book b;
        if (bookId != null) {
            b = bMapper.selectBookById(bookId);
            if (b == null) throw new IllegalArgumentException("book not found: " + req.getBookId());
        } else {
            String isbn = req.getIsbn13Code();
            if (isbn == null || isbn.isBlank()) {
                throw new IllegalArgumentException("bookId or isbn13Code required");
            }

            if (bMapper.existsBookByIsbn13Code(isbn)) {
                b = bMapper.selectBookByIsbn13Code(isbn);
            } else {
                b = new Book();
                b.setIsbn13Code(isbn);
                b.setTitle(req.getTitle());
                b.setAuthor(req.getAuthor());
                b.setPages(req.getPages());
                b.setPublisher(req.getPublisher());
//                b.setPubDate(req.getPubDate() != null ? req.getPubDate().format(DT) : null);
                bMapper.insertBook(b); // keyProperty=bookId 세팅
            }
            bookId = b.getBookId();
        }
        if (sbMapper.existsBookshelfById(req.getBookshelfId(), req.getBookId())) {
            throw new IllegalArgumentException("already exists in shelf: bookId=" + req.getBookId());
        }

        ShelfItem si = new ShelfItem();
        si.setBookshelfId(req.getBookshelfId());
        si.setBookId(req.getBookId());
        sbMapper.insertShelfItem(si);

        ShelfItemResponse r = new ShelfItemResponse();
        r.setShelfBookId(si.getShelfBookId());
        r.setBookshelfId(si.getBookshelfId());
        r.setBookId(si.getBookId());
        r.setCurrentPage(si.getCurrentPage());
        r.setReadingStatus(si.getReadingStatus());
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

        // 1) 페이지 클램프
        Integer cp = req.getCurrentPage();
        if (cp != null) {
            int max = current.getPages() != null ? Math.max(0, current.getPages()) : Integer.MAX_VALUE;
            cp = Math.max(0, Math.min(cp, max));
        }

        // 2) 상태 결정
        String status = req.getReadingStatus(); // null 아니면 지정된대로 적용
        if (status == null && cp != null) {
            if (current.getPages() != null) {
                int pages = Math.max(0, current.getPages());
                if (cp <= 0) status = "PLAN"; // 0페이지면 읽을예정상태
                else if (cp >= pages && pages > 0) status = "DONE";
                else status = "READING";
            } else {
                status = (cp <= 0) ? "PLAN" : "READING";
            }
        }
        if (status != null && !status.matches("PLAN|READING|DONE")) {
            throw new IllegalArgumentException(("invalid readingStatus"));
        }

        // 3) 업데이트
        ShelfItem toUpdate = new ShelfItem();
        toUpdate.setShelfBookId(req.getShelfBookId());
        toUpdate.setCurrentPage(cp);
        toUpdate.setReadingStatus(status); // null이면 미변경
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
        r.setCurrentPage(fresh.getCurrentPage());
        r.setReadingStatus(fresh.getReadingStatus());
        if (b != null) {
            r.setTitle(b.getTitle());
            r.setAuthor(b.getAuthor());
            r.setPages(b.getPages());
        }
        r.setAddedDatetime(fresh.getAddedDatetime() != null ? fresh.getAddedDatetime().format(DT) : null);
        r.setModifiedDatetime(fresh.getModifiedDatetime() != null ? fresh.getModifiedDatetime().format(DT) : null);
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
