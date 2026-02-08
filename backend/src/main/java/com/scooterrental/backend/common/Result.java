package com.scooterrental.backend.common;

import lombok.Data;

/**
 * Generic response wrapper for standardizing API output.
 * Format: { code, message, data }
 */
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    // Success response with data
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "Success";
        result.data = data;
        return result;
    }

    // Success response without data
    public static <T> Result<T> success() {
        return success(null);
    }

    // Error response with custom code and message
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        return result;
    }
}
