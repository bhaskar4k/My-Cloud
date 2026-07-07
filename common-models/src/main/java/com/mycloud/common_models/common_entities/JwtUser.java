package com.mycloud.common_models.common_entities;

public record JwtUser(
        Boolean IsAuthenticated,
        Long userId,
        String email
) {}
