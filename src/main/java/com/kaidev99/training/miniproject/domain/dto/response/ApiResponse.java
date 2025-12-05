package com.kaidev99.training.miniproject.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        int code,
        String status,
        String message,
        T data,
        String error
) {
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), message, data, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Request was successful");
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message, String errorDetails) {
        return new ApiResponse<>(status.value(), status.getReasonPhrase(), message, null, errorDetails);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return error(status, message, null);
    }
}
