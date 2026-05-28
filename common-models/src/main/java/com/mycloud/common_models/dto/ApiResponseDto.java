package com.mycloud.common_models.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseDto<T> {
    private int statusCode;

    private boolean success;

    private String message;

    private T data;

    private Object extraData;

    // SUCCESS
    public static <T> ApiResponseDto<T> Success(
            String message,
            T data
    ) {
        return ApiResponseDto.<T>builder()
                .statusCode(200)
                .success(true)
                .message(message)
                .data(data)
                .extraData(null)
                .build();
    }

    // SUCCESS WITH EXTRA DATA
    public static <T> ApiResponseDto<T> Success(
            String message,
            T data,
            Object extraData
    ) {
        return ApiResponseDto.<T>builder()
                .statusCode(200)
                .success(true)
                .message(message)
                .data(data)
                .extraData(extraData)
                .build();
    }

    // ERROR
    public static <T> ApiResponseDto<T> Error(
            int statusCode,
            String message
    ) {
        return ApiResponseDto.<T>builder()
                .statusCode(statusCode)
                .success(false)
                .message(message)
                .data(null)
                .extraData(null)
                .build();
    }

    // ERROR WITH DATA
    public static <T> ApiResponseDto<T> Error(
            int statusCode,
            String message,
            T data
    ) {
        return ApiResponseDto.<T>builder()
                .statusCode(statusCode)
                .success(false)
                .message(message)
                .data(data)
                .extraData(null)
                .build();
    }
}