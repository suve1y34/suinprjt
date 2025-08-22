package co.kr.sikim.suinproject.service;

import co.kr.sikim.suinproject.dto.aladin.AladinBookResponse;
import co.kr.sikim.suinproject.dto.aladin.AladinSearchRequest;

import java.util.List;

public interface AladinService {
    List<AladinBookResponse> searchBooks(AladinSearchRequest req);
}
