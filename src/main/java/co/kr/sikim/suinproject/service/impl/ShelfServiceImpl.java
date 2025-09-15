package co.kr.sikim.suinproject.service.impl;

import static co.kr.sikim.suinproject.common.SecurityUtils.currentUserIdOrThrow;
import co.kr.sikim.suinproject.domain.Book;
import co.kr.sikim.suinproject.domain.Bookshelf;
import co.kr.sikim.suinproject.domain.ShelfItem;
import co.kr.sikim.suinproject.domain.ShelfItemJoinRow;
import co.kr.sikim.suinproject.dto.shelf.BookshelfResponse;
import co.kr.sikim.suinproject.dto.shelf.ShelfItemSearchCond;
import co.kr.sikim.suinproject.dto.shelf.StatsResponse;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemAddRequest;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemResponse;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemDeleteRequest;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemUpdateRequest;
import co.kr.sikim.suinproject.mapper.BookMapper;
import co.kr.sikim.suinproject.mapper.ShelfMapper;
import co.kr.sikim.suinproject.service.BookInternalService;
import co.kr.sikim.suinproject.service.ShelfService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ShelfServiceImpl implements ShelfService {
    private final ShelfMapper sbMapper;
    private final BookMapper bMapper;
    private final BookInternalService biService;

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 책장에 대한 권한 체크
    private void assertMyBookShelf(Long bookshelfId) {
        Long uid = currentUserIdOrThrow();
        if (!sbMapper.existsBookshelfOwnedByUser(bookshelfId, uid)) {
            throw new AccessDeniedException("forbidden: not your shelf");
        }
    }

    // 책장 책에 대한 권한 체크
    private void assertMyShelfItem(Long shelfBookid) {
        Long uid = currentUserIdOrThrow();
        if (!sbMapper.existsShelfItemOwnedByUser(shelfBookid, uid)) {
            throw new AccessDeniedException("forbidden: not your shelf item");
        }
    }

    @Transactional
    @Override
    public ShelfItemResponse createShelfItem(ShelfItemAddRequest req) {
        // 책장 존재 & 권한 체크
        if (!sbMapper.existBookshelfById(req.getBookshelfId())) {
            throw new IllegalArgumentException("bookshelf not found: " + req.getBookshelfId());
        }
        assertMyBookShelf(req.getBookshelfId());

        // 입력 해석 및 정규화
        Book book = resolveBookOrThrow(req.getBookId(), req.getIsbn13Code(),
                req.getTitle(), req.getAuthor(), req.getPages(), req.getPublisher(), req.getPubDate());
        assertNotDuplicate(req.getBookshelfId(), book.getBookId());

        Integer pages = book.getPages(); // null 가능
        int currentPage = clamp(req.getCurrentPage(), 0, pages);
        String readingStatus = normalizeStatus(req.getReadingStatus(), currentPage, pages);

        if ("DONE".equals(readingStatus)) {
            currentPage = pages;
        }

        ShelfItem si = new ShelfItem();
        si.setBookshelfId(req.getBookshelfId());
        si.setBookId(book.getBookId());
        si.setCurrentPage(currentPage);
        si.setReadingStatus(readingStatus);
        si.setMemo(sanitizeRichText(req.getMemo()));
        si.setStartDate(parseDateOrNull(req.getStartDate())); // FE에서 세팅한 값
        si.setEndDate(parseDateOrNull(req.getEndDate()));

        si.setReview(sanitizeRichText(req.getReview()));
        si.setReviewVisibility(normalizeReviewVisibility(req.getReviewVisibility()));
        sbMapper.insertShelfItem(si);

        // response 데이터 구성
        ShelfItemJoinRow fresh = sbMapper.selectShelfItemById(si.getShelfBookId());
        return toResponse(fresh);
    }

    @Override
    public BookshelfResponse getShelf(Long userId) {
        Bookshelf bs = sbMapper.selectBookshelfById(userId);
        int count = sbMapper.countShelfItems(bs.getBookshelfId());

        if (!sbMapper.existBookshelfById(bs.getBookshelfId()))
            throw new IllegalArgumentException("bookshelf not found");

        assertMyBookShelf(bs.getBookshelfId());
        BookshelfResponse res = new BookshelfResponse();
        res.setBookshelfId(bs.getBookshelfId());
        res.setUserId(bs.getUserId());
        res.setItemCount(count);

        return res;
    }

    @Override
    public List<ShelfItemResponse> listShelfItems(ShelfItemSearchCond cond) {
        if (!sbMapper.existBookshelfById(cond.getBookshelfId())) {
            throw new IllegalArgumentException("bookshelf not found");
        }

        assertMyBookShelf(cond.getBookshelfId());

        return sbMapper.selectShelfItemsByShelfId(cond).stream().map(si -> {
            ShelfItemResponse r = new ShelfItemResponse();
            r.setShelfBookId(si.getShelfBookId());
            r.setBookshelfId(si.getBookshelfId());
            r.setBookId(si.getBookId());
            r.setIsbn13Code(si.getIsbn13Code());
            r.setTitle(si.getTitle());
            r.setAuthor(si.getAuthor());
            r.setPages(si.getPages());
            r.setCurrentPage(si.getCurrentPage());
            r.setReadingStatus(si.getReadingStatus());
            r.setMemo(si.getMemo());
            r.setMemoVisibility(si.getMemoVisibility());
            r.setReview(si.getReview());
            r.setReviewVisibility(si.getReviewVisibility());
            r.setStartDate(formatDateOrNull(si.getStartDate()));
            r.setEndDate(formatDateOrNull(si.getEndDate()));
            r.setCoverImageUrl(si.getCoverImageUrl());

            r.setAddedDatetime(si.getAddedDatetime() != null ? si.getAddedDatetime().format(DT) : null);
            r.setModifiedDatetime(si.getModifiedDatetime() != null ? si.getModifiedDatetime().format(DT) : null);
            return r;
        }).toList();
    }



    @Transactional
    @Override
    public ShelfItemResponse updateShelfItem(ShelfItemUpdateRequest req) {
        // 현재 상태 로드 & 권한 체크
        ShelfItemJoinRow current = sbMapper.selectShelfItemById(req.getShelfBookId());
        if (current == null) {
            throw new IllegalArgumentException("shelf item not found: " + req.getShelfBookId());
        }
        assertMyShelfItem(req.getShelfBookId());

        Book b = bMapper.selectBookById(current.getBookId());
        Integer pages = b != null ? b.getPages() : null;

        // 정규화
        Integer nextPage = req.getCurrentPage() != null ? clamp(req.getCurrentPage(), 0, pages) : current.getCurrentPage();
        String nextStatus = req.getReadingStatus();
        if (nextStatus == null || nextStatus.isBlank()) {
            nextStatus = normalizeStatus(current.getReadingStatus(), nextPage, pages);
        }

        if ("DONE".equals(nextStatus)) {
            nextPage = pages;
        }

        ShelfItem toUpdate = new ShelfItem();
        toUpdate.setShelfBookId(req.getShelfBookId());
        toUpdate.setCurrentPage(nextPage);
        toUpdate.setReadingStatus(nextStatus); // null이면 미변경
        toUpdate.setStartDate(parseDateOrNull(req.getStartDate()));
        toUpdate.setEndDate(parseDateOrNull(req.getEndDate()));

        if (Boolean.TRUE.equals(req.getMemoChanged())) {
            toUpdate.setMemo(sanitizeRichText(req.getMemo())); // null이면 비우기
            toUpdate.setMemoChanged(true);
        } else {
            toUpdate.setMemoChanged(false);
        }

        // 리뷰
        if (Boolean.TRUE.equals(req.getReviewChanged())) {
            toUpdate.setReviewChanged(true);
            toUpdate.setReview(sanitizeRichText(req.getReview()));             // null → 비우기
            if (req.getReviewVisibility() != null && !req.getReviewVisibility().isBlank()) {
                toUpdate.setReviewVisibility(normalizeReviewVisibility(req.getReviewVisibility()));
            }
        }

        int updated = sbMapper.updateShelfItem(toUpdate);
        if (updated == 0) {
            throw new IllegalStateException("update failed: " + req.getShelfBookId());
        }

        // 갱신 후 응답용 조회 + response 데이터 구성
        ShelfItemJoinRow fresh = sbMapper.selectShelfItemById(req.getShelfBookId());
        return toResponse(fresh);
    }

    @Transactional
    @Override
    public void deleteShelfItem(ShelfItemDeleteRequest req) {
        assertMyShelfItem(req.getShelfBookId());
        int deleted = sbMapper.deleteShelfItemById(req.getShelfBookId());
        if (deleted == 0) {
            throw new IllegalArgumentException("shelf item not found: " + req.getShelfBookId());
        }
    }

    @Override
    public int countShelfBooksByStatus(Long userId, String status) {
        return sbMapper.countShelfBooksByStatus(userId, status);
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

    @Override
    public StatsResponse getShelfStats(Long userId, Integer year) {
        // 연도 기본값: 올해
        Integer targetYear = (year != null && year > 0) ? year : Year.now().getValue();

        // 1) 상태별 개수
        List<Map<String, Object>> rows = sbMapper.selectStatusCountsByUserId(userId);
        // 기본 0으로 초기화
        Map<String, Integer> statusMap = new HashMap<>();
        statusMap.put("PLAN", 0);
        statusMap.put("READING", 0);
        statusMap.put("DONE", 0);

        for (Map<String, Object> r : rows) {
            String s = String.valueOf(r.get("status"));
            int cnt = ((Number) r.get("cnt")).intValue();
            if (statusMap.containsKey(s)) statusMap.put(s, cnt);
        }

        List<StatsResponse.StatusRatio> statusRatio = new ArrayList<>();
        statusRatio.add(new StatsResponse.StatusRatio("읽기전", statusMap.get("PLAN"), "PLAN"));
        statusRatio.add(new StatsResponse.StatusRatio("읽는중", statusMap.get("READING"), "READING"));
        statusRatio.add(new StatsResponse.StatusRatio("다읽음", statusMap.get("DONE"), "DONE"));

        // 2) 월별(완독) 개수
        List<Map<String, Object>> monthlyRows = sbMapper.selectMonthlyDoneCountsByUserId(userId, targetYear);
        Map<Integer, Integer> byMonth = new HashMap<>();
        for (Map<String, Object> r : monthlyRows) {
            Integer m = ((Number) r.get("m")).intValue(); // 1..12
            Integer cnt = ((Number) r.get("cnt")).intValue();
            byMonth.put(m, cnt);
        }

        List<StatsResponse.MonthlyCount> monthly = IntStream.rangeClosed(1, 12)
                .mapToObj(m -> new StatsResponse.MonthlyCount(m + "월", byMonth.getOrDefault(m, 0), m))
                .collect(Collectors.toList());

        return new StatsResponse(statusRatio, monthly);
    }

    private String sanitizeRichText(String s) {
        if (s == null) return null;
        // XSS 리스크 최소화를 위해 HTML제거
        return Jsoup.clean(s, Safelist.none());
    }

    private Book resolveBookOrThrow(Long bookId,
                                    String isbn13,
                                    String title,
                                    String author,
                                    Integer pages,
                                    String publisher,
                                    String pubDate) {
        if (bookId != null) {
            Book ensured = bMapper.selectBookById(bookId);
            if (ensured == null) throw new IllegalArgumentException("book not found: " + bookId);
            return ensured;
        }
        if (isbn13 != null && !isbn13.isBlank()) {
            return biService.upsertByIsbn13AndFillPagesIfMissing(isbn13, title, author, pages, publisher, pubDate);
        }
        throw new IllegalArgumentException("bookId or isbn13Code is required");
    }

    private void assertNotDuplicate(Long bookshelfId, Long bookId) {
        if (sbMapper.existsBookshelfById(bookshelfId, bookId)) {
            throw new IllegalArgumentException("already exists in shelf: bookId=" + bookId);
        }
    }

    private String normalizeReviewVisibility(String v) {
        if (v == null || v.isBlank()) return "PRIVATE";
        String s = v.trim().toUpperCase();
        return "PUBLIC".equals(s) ? "PUBLIC" : "PRIVATE";
    }

    private LocalDate parseDateOrNull(String yyyyMmDd) {
        if (yyyyMmDd == null) return null;
        String s = yyyyMmDd.trim();
        if (s.isEmpty()) return null;
        return LocalDate.parse(s); // ISO-8601 yyyy-MM-dd
    }

    private String formatDateOrNull(LocalDate date) {
        return (date == null) ? null : date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private ShelfItemResponse toResponse(ShelfItemJoinRow si) {
        ShelfItemResponse r = new ShelfItemResponse();
        r.setShelfBookId(si.getShelfBookId());
        r.setBookshelfId(si.getBookshelfId());
        r.setBookId(si.getBookId());
        r.setTitle(si.getTitle());
        r.setAuthor(si.getAuthor());
        r.setPages(si.getPages());
        r.setCurrentPage(si.getCurrentPage());
        r.setStartDate(formatDateOrNull(si.getStartDate()));
        r.setEndDate(formatDateOrNull(si.getEndDate()));
        r.setReadingStatus(si.getReadingStatus());
        r.setMemo(si.getMemo());
        r.setMemoVisibility(si.getMemoVisibility());
        r.setReview(si.getReview());
        r.setReviewVisibility(si.getReviewVisibility());
        r.setAddedDatetime(si.getAddedDatetime() != null ? si.getAddedDatetime().format(DT) : null);
        r.setModifiedDatetime(si.getModifiedDatetime() != null ? si.getModifiedDatetime().format(DT) : null);
        return r;
    }
}