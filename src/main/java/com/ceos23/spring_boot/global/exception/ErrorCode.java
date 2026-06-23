package com.ceos23.spring_boot.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    DUPLICATE_USERID(HttpStatus.CONFLICT, "E001", "이미 사용 중인 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "E002", "이미 사용 중인 이메일입니다."),

    INVALID_LOGIN_INFO(HttpStatus.UNAUTHORIZED, "E003","아이디 또는 비밀번호가 올바르지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E004", "인증이 필요합니다."),

    POLL_NOT_FOUND(HttpStatus.NOT_FOUND, "E005", "투표를 찾을 수 없습니다."),
    CANDIDATE_NOT_FOUND(HttpStatus.NOT_FOUND, "E006", "후보를 찾을 수 없습니다."),
    CANDIDATE_NOT_IN_POLL(HttpStatus.BAD_REQUEST, "E007", "해당 투표에 속한 후보가 아닙니다."),

    INVALID_PART_LEADER_VOTE(HttpStatus.BAD_REQUEST, "E008", "본인의 파트에 해당하는 파트장 후보에게만 투표할 수 있습니다."),
    INVALID_DEMO_DAY_VOTE(HttpStatus.BAD_REQUEST, "E009","본인이 속한 팀에는 데모데이 투표를 할 수 없습니다."),
    ALREADY_VOTED(HttpStatus.CONFLICT, "E010", "이미 투표를 완료했습니다."),

    NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED, "E011", "아직 구현되지 않은 기능입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E012","서버 내부 오류가 발생했습니다."),

    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "E013", "토큰이 만료되었습니다."),
    INVALID_TOKEN_FORM(HttpStatus.BAD_REQUEST, "E014", "잘못된 형식의 토큰입니다."),
    INVALID_SIGNATURE(HttpStatus.BAD_REQUEST, "E015", "서명이 잘못되었습니다."),
    INTERNAL_TOKEN_ERROR(HttpStatus.BAD_REQUEST, "E016", "토큰에 오류가 있습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "E017", "리프레시 토큰을 찾을 수 없습니다"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E018", "사용자를 찾을 수 없습니다."),

    FORBIDDEN(HttpStatus.FORBIDDEN, "E018", "해당 페이지에 대한 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String errorCode, String message) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }
}