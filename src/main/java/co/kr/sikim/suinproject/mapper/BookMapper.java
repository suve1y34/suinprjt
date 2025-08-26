package co.kr.sikim.suinproject.mapper;

import co.kr.sikim.suinproject.domain.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookMapper {
    // 책 목록 조회
    List<Book> selectBooks(@Param("keyword") String keyword);
    // 책 상세 조회
    Book selectBookById(@Param("bookId") Long bookId);
    // 책 존재여부
    boolean existsBookByIsbn13Code(@Param("isbn13Code") String isbn13Code);
    // isbncode로 책 조회
    Book selectBookByIsbn13Code(@Param("isbn13Code") String isbn13Code);
    int insertBook(Book book);
    int updateBook(Book book);
}
