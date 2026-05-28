package com.mycloud.common_models.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseDto<T> {
    private int StatusCode;

    private boolean Success;

    private String Message;

    private T Data;

    private Object ExtraData;

    // SUCCESS
    public static <T> ApiResponseDto<T> Success(
            String message,
            T data
    ) {
        return ApiResponseDto.<T>builder()
                .StatusCode(200)
                .Success(true)
                .Message(message)
                .Data(data)
                .ExtraData(null)
                .build();
    }

    // SUCCESS WITH EXTRA DATA
    public static <T> ApiResponseDto<T> Success(
            String message,
            T data,
            Object extraData
    ) {
        return ApiResponseDto.<T>builder()
                .StatusCode(200)
                .Success(true)
                .Message(message)
                .Data(data)
                .ExtraData(extraData)
                .build();
    }

    // ERROR
    public static <T> ApiResponseDto<T> Error(
            int statusCode,
            String message
    ) {
        return ApiResponseDto.<T>builder()
                .StatusCode(statusCode)
                .Success(false)
                .Message(message)
                .Data(null)
                .ExtraData(null)
                .build();
    }

    // ERROR WITH DATA
    public static <T> ApiResponseDto<T> Error(
            int statusCode,
            String message,
            T data
    ) {
        return ApiResponseDto.<T>builder()
                .StatusCode(statusCode)
                .Success(false)
                .Message(message)
                .Data(data)
                .ExtraData(null)
                .build();
    }
}