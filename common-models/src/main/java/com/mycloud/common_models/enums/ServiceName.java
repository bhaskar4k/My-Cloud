package com.mycloud.common_models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServiceName {

    CORE("core"),
    COMMON("common"),
    AUTH("auth"),
    FILE("file"),
    PROCESSING("processing");

    private final String value;
}