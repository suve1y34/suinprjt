package co.kr.sikim.suinproject.dto;

import lombok.Data;

@Data
public class BookResponse {
    private Long bookId;
    private String isbn13Code;
    private String title;
    private String author;
    private Integer pages;
    private String publisher;
    private String pubDate;
}
