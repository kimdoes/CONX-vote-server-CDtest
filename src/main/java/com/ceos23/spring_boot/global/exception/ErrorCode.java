package com.ceos23.spring_boot.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),

    INVALID_LOGIN_INFO(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),

    POLL_NOT_FOUND(HttpStatus.NOT_FOUND, "투표를 찾을 수 없습니다."),
    CANDIDATE_NOT_FOUND(HttpStatus.NOT_FOUND, "후보를 찾을 수 없습니다."),
    CANDIDATE_NOT_IN_POLL(HttpStatus.BAD_REQUEST, "해당 투표에 속한 후보가 아닙니다."),

    INVALID_PART_LEADER_VOTE(HttpStatus.BAD_REQUEST, "본인의 파트에 해당하는 파트장 후보에게만 투표할 수 있습니다."),
    INVALID_DEMO_DAY_VOTE(HttpStatus.BAD_REQUEST, "본인이 속한 팀에는 데모데이 투표를 할 수 없습니다."),
    ALREADY_VOTED(HttpStatus.CONFLICT, "이미 투표를 완료했습니다."),

    NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED, "아직 구현되지 않은 기능입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}