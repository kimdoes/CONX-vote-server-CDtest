package com.ceos23.spring_boot.global.security.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JWTType {
    ACCESS (3600),
    REFRESH (3600 * 24 * 3),
    TEMPORARY (60 * 5),
    TEST (1);

    private final int validTime;
}