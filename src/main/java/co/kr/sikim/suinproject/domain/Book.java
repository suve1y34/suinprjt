package co.kr.sikim.suinproject.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Book {
    private Long bookId;
    private String isbn13Code;
    private String title;
    private String author;
    private Integer pages;
    private String publisher;
    private LocalDate pubDate;
    private LocalDateTime createdDatetime;
    private LocalDateTime modifiedDatetime;
}
