package co.kr.sikim.suinproject.mapper;

import co.kr.sikim.suinproject.domain.Book;
import co.kr.sikim.suinproject.domain.PublicReviewRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookMapper {
    int insertBook(Book book);
    int updateBook(Book book);

    // 책 상세 조회
    Book selectBookById(@Param("bookId") Long bookId);
    // isbncode로 책 조회
    Book selectBookByIsbn13Code(@Param("isbn13Code") String isbn13Code);

    // 책 목록 조회
    List<Book> selectBooks(@Param("keyword") String keyword);

    // 책 리뷰 리스트 조회
    List<PublicReviewRow> selectPublicReviewsByIsbn13(
            @Param("isbn13") String isbn13,
            @Param("cursorId") Long cursorId,
            @Param("limit") int limit
    );
    // 책 존재여부
    boolean existsBookByIsbn13Code(@Param("isbn13Code") String isbn13Code);
}
