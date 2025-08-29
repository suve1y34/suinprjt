package co.kr.sikim.suinproject.external;

import co.kr.sikim.suinproject.dto.aladin.AladinBookResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class AladinParser {
    private final ObjectMapper om = new ObjectMapper();
    private final DateTimeFormatter[] dateFormats = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyyMMdd")
    };

    public List<AladinBookResponse> parseSearch(String rawJson) {
        try {
            JsonNode root = om.readTree(rawJson);

            // 에러 우선 처리
            if (root.hasNonNull("errorCode")) {
                String code = root.path("errorCode").asText();
                String msg = root.path("errorMessage").asText("Unknown error");
                throw new IllegalStateException("Aladin error " + code + ": " + msg);
            }

            JsonNode items = root.get("item");
            if (items == null || !items.isArray() || items.size() == 0) {
                return Collections.emptyList();
            }

            List<AladinBookResponse> result = new ArrayList<>();
            for (JsonNode it : items) {
                String isbn13 = getText(it, "isbn13");
                String title = getText(it, "title");
                String author = getText(it, "author");
                String publisher = getText(it, "publisher");

                // 날짜: pubDate 또는 pubDateStr
                String pubDateRaw = firstNonEmpty(getText(it, "pubDate"), getText(it, "pubDateStr"));
                String pubDate = normalizeDate(pubDateRaw);

                Integer pages = readInt(it.path("itemPage"));
                if (pages == null) {
                    JsonNode sub = it.path("subInfo");
                    if (sub.isObject()) pages = readInt(sub.path("itemPage"));
                }
                if (pages == null) pages = readInt(it.path("page"));

                result.add(new AladinBookResponse(
                        isbn13, title, author, pages, publisher, pubDate
                ));
            }
            return result;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private String getText(JsonNode it, String field) {
        JsonNode n = it.get(field);
        return n == null || n.isNull() ? null : n.asText();
    }

    private String firstNonEmpty(String... v) {
        for (String s : v) if (s != null && !s.isBlank()) return s;
        return null;
    }

    private String normalizeDate(String raw) {
        if (raw == null || raw.isBlank()) return null;
        for (DateTimeFormatter f : dateFormats) {
            try {
                LocalDate d = LocalDate.parse(raw, f);
                return d.toString();
            } catch (Exception ignored) {}
        }
        return null;
    }

    private Integer readInt(JsonNode n) {
        if (n == null || n.isNull()) return null;
        if (n.isInt()) return n.asInt();
        if (n.isTextual()) {
            try { return Integer.parseInt(n.asText().trim()); } catch (Exception ignore) {}
        }
        return null;
    }
}