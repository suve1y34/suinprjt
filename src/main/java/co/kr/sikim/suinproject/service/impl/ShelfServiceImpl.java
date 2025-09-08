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
import co.kr.sikim.suinproject.service.BookInternalService;
import co.kr.sikim.suinproject.service.ShelfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ShelfServiceImpl implements ShelfService {
    private final ShelfMapper sbMapper;
    private final BookMapper bMapper;
    private final BookInternalService biService;

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
            r.setCurrentPage(si.getCurrentPage());
            r.setReadingStatus(si.getReadingStatus());
            r.setAddedDatetime(si.getAddedDatetime() != null ? si.getAddedDatetime().format(DT) : null);
            r.setModifiedDatetime(si.getModifiedDatetime() != null ? si.getModifiedDatetime().format(DT) : null);
            r.setMemo(si.getMemo());
            return r;
        }).toList();
    }

    @Transactional
    @Override
    public ShelfItemResponse createShelfItem(ShelfItemAddRequest req) {
        if (!sbMapper.existBookshelfById(req.getBookshelfId())) {
            throw new IllegalArgumentException("bookshelf not found: " + req.getBookshelfId());
        }
        if (!sbMapper.existBookshelfById(req.getBookshelfId())) {
            throw new IllegalArgumentException("bookshelf not found: " + req.getBookshelfId());
        }

        Long bookId;
        Book ensuredBook;

        if (req.getBookId() != null) {
            ensuredBook = bMapper.selectBookById(req.getBookId());
            if (ensuredBook == null) throw new IllegalArgumentException("book not found: " + req.getBookId());
            bookId = ensuredBook.getBookId();
        } else if(req.getIsbn13Code() != null && !req.getIsbn13Code().isBlank()) {
            ensuredBook = biService.upsertByIsbn13AndFillPagesIfMissing(
                    req.getIsbn13Code(), req.getTitle(), req.getAuthor(), req.getPages(),
                    req.getPublisher(), req.getPubDate()
            );
            bookId = ensuredBook.getBookId();
        } else {
            throw new IllegalArgumentException("bookId or isbn13Code is required");
        }
        if (sbMapper.existsBookshelfById(req.getBookshelfId(), bookId)) {
            throw new IllegalArgumentException("already exists in shelf: bookId=" + req.getBookId());
        }

        Integer pages = ensuredBook.getPages(); // null 가능
        int currentPage = clamp(req.getCurrentPage(), 0, pages);
        String readingStatus = normalizeStatus(req.getReadingStatus(), currentPage, pages);


        ShelfItem si = new ShelfItem();
        si.setBookshelfId(req.getBookshelfId());
        si.setBookId(bookId);
        si.setCurrentPage(currentPage);
        si.setReadingStatus(readingStatus);
        si.setMemo(req.getMemo());
        sbMapper.insertShelfItem(si);

        ShelfItemResponse r = new ShelfItemResponse();
        r.setShelfBookId(si.getShelfBookId());
        r.setBookshelfId(si.getBookshelfId());
        r.setBookId(bookId);
        r.setTitle(ensuredBook.getTitle());
        r.setAuthor(ensuredBook.getAuthor());
        r.setPages(ensuredBook.getPages());
        r.setCurrentPage(currentPage);
        r.setReadingStatus(readingStatus);
        r.setMemo(req.getMemo());
        return r;
    }

    @Transactional
    @Override
    public ShelfItemResponse updateShelfItem(ShelfItemUpdateRequest req) {
        ShelfItemJoinRow current = sbMapper.selectShelfItemById(req.getShelfBookId());
        if (current == null) {
            throw new IllegalArgumentException("shelf item not found: " + req.getShelfBookId());
        }

        Book b = bMapper.selectBookById(current.getBookId());
        Integer pages = b != null ? b.getPages() : null;

        Integer nextPage = req.getCurrentPage() != null ? clamp(req.getCurrentPage(), 0, pages) : current.getCurrentPage();
        String nextStatus = req.getReadingStatus();
        if (nextStatus == null || nextStatus.isBlank()) {
            nextStatus = normalizeStatus(current.getReadingStatus(), nextPage, pages);
        }

        ShelfItem toUpdate = new ShelfItem();
        toUpdate.setShelfBookId(req.getShelfBookId());
        toUpdate.setCurrentPage(nextPage);
        toUpdate.setReadingStatus(nextStatus); // null이면 미변경

        if (Boolean.TRUE.equals(req.getMemoChanged())) {
            toUpdate.setMemo(req.getMemo()); // null이면 비우기
            toUpdate.setMemoChanged(true);
        } else {
            toUpdate.setMemoChanged(false);
        }

        int updated = sbMapper.updateShelfItem(toUpdate);
        if (updated == 0) {
            throw new IllegalStateException("update failed: " + req.getShelfBookId());
        }

        // 갱신 후 응답용 조회 + Book 조인
        ShelfItemJoinRow fresh = sbMapper.selectShelfItemById(req.getShelfBookId());
        ShelfItemResponse r = new ShelfItemResponse();
        r.setShelfBookId(fresh.getShelfBookId());
        r.setBookshelfId(fresh.getBookshelfId());
        r.setBookId(fresh.getBookId());
        r.setTitle(fresh.getTitle());
        r.setAuthor(fresh.getAuthor());
        r.setPages(fresh.getPages());
        r.setCurrentPage(fresh.getCurrentPage());
        r.setReadingStatus(fresh.getReadingStatus());
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

    private int clamp(Integer v, int min, Integer maxNullable) {
        int x = v == null ? 0 : v;
        if (x < min) return min;
        if (maxNullable != null && x > maxNullable) return maxNullable;
        return x;
    }

    private String normalizeStatus(String status, int currentPage, Integer pages) {
        String s = (status == null || status.isBlank()) ? "PLAN" : status.trim().toUpperCase();
        if (Objects.equals(s, "PLAN") || Objects.equals(s, "READING") || Objects.equals(s, "DONE")) {
            // 입력이 유효하면 그대로 쓰되, 미지정/잘못된 경우는 진행도 기반 자동 전이
            return s;
        }
        // 자동 전이
        if (pages != null) {
            if (currentPage >= pages) return "DONE";
            if (currentPage > 0) return "READING";
            return "PLAN";
        } else {
            // 총 페이지를 모르면 0이면 PLAN, >0이면 READING 가정
            return currentPage > 0 ? "READING" : "PLAN";
        }
    }
}
