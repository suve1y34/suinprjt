package co.kr.sikim.suinproject.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AladinCache {
    private String isbn13Code;
    private String rawJson;
    private LocalDateTime lastSyncedDatetime;
}
