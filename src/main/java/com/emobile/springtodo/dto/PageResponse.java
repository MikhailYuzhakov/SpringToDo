package com.emobile.springtodo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageResponse<T> {
    private List<T> content;         // Сам список элементов на текущей странице
    private int currentPage;         // Номер текущей страницы (обычно с 0)
    private int pageSize;            // Количество элементов на странице
    private long totalElements;      // Общее количество элементов во всей коллекции
    private int totalPages;          // Общее количество страниц

    public PageResponse(List<T> content, int currentPage, int pageSize, long totalElements) {
        this.content = content;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        // Рассчитываем общее количество страниц
        if (pageSize > 0) {
            this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
        } else {
            this.totalPages = 0;
        }
    }
}
