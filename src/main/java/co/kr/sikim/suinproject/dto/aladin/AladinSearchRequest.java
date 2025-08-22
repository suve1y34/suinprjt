package co.kr.sikim.suinproject.dto.aladin;

import lombok.Data;

@Data
public class AladinSearchRequest {
    private String keyword;
    private Integer start = 1;
    private Integer maxResults = 20;
}
