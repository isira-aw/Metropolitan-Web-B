package com.metropolitan.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> data;
    private long total;
    private int page;
    private int totalPages;

    public static <T> PageResponse<T> of(List<T> data, long total, int page, int limit) {
        int totalPages = (int) Math.ceil((double) total / limit);
        return new PageResponse<>(data, total, page, totalPages);
    }
}
