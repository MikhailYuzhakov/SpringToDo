package com.emobile.springtodo.dto;

import java.time.Instant;
import java.util.Map;

public record ApiResponse<T>(
        boolean success,
        T data,
        ErrorResponse error,
        Map<String, Object> meta,
        Instant timestamp
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, null, Instant.now());
    }
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, data, null, null, Instant.now());
    }
    public static ApiResponse<Void> noContent() {
        return new ApiResponse<>(true, null, null, null, Instant.now());
    }
    public static ApiResponse<ErrorResponse> error(ErrorResponse error) {
        return new ApiResponse<>(false, null, error, null, Instant.now());
    }
    public ApiResponse<T> withMeta(Map<String, Object> meta) {
        return new ApiResponse<>(this.success, this.data, this.error, meta, this.timestamp);
    }
}
