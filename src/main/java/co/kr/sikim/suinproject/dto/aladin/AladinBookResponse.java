package co.kr.sikim.suinproject.dto.aladin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AladinBookResponse {
    private String isbn13Code;
    private String title;
    private String author;
    private Integer pages;
    private String publisher;
    private String pubDate;
    private String coverImageUrl;
}
