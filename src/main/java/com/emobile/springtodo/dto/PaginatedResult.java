package com.emobile.springtodo.dto;

import java.util.List;

public record PaginatedResult<T>(
        List<T> items,
        long totalCount
) {}
