package com.spot.good2travel.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionMessage {
        TOKEN_EXPIRED("토큰이 만료되었습니다.", 0, HttpStatus.UNAUTHORIZED),
        TOKEN_NOT_AUTHORIZED("권한이 없습니다", 0, HttpStatus.FORBIDDEN),
        TOKEN_UNAUTHENTICATED("인증되지 않은 토큰입니다.", 0, HttpStatus.UNAUTHORIZED),
        TOKEN_INVALID_FORMAT("잘못된 형식의 토큰입니다.", 0, HttpStatus.UNAUTHORIZED),
        MEMBER_NOT_FOUND("멤버가 존재하지 않습니다.", 0, HttpStatus.NOT_FOUND),
        TOKEN_NOT_FOUND("토큰이 비었거나 null입니다", 0, HttpStatus.BAD_REQUEST),
        TOKEN_TYPE_INVALID("토큰 타입이 틀렸습니다.", 0, HttpStatus.BAD_REQUEST),
        MEMBER_UNAUTHENTICATED("접근 권한이 없는 페이지입니다.", 0, HttpStatus.UNAUTHORIZED),
        IMAGE_EMPTY("이미지가 비었습니다.", 0, HttpStatus.BAD_REQUEST),
        IMAGE_SEND_ERROR("이미지 처리 중 예기치 못한 오류가 발생했습니다.", 0, HttpStatus.BAD_REQUEST),
        IMAGE_READ_ERROR("이미지를 읽을 수 없습니다.", 0, HttpStatus.BAD_REQUEST),
        METROPOLITANGOVERNMENT_NOT_FOUND("존재하지 않는 지자체입니다.", 0, HttpStatus.NOT_FOUND);


private final String message;

private final Integer errorCode;

private final HttpStatus httpStatus;


        ExceptionMessage(String message, Integer errorCode, HttpStatus httpStatus) {
        this.message = message;
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        }

public String getMessage() {
        return message;
        }

public Integer getErrorCode() {
        return errorCode;
        }

public HttpStatus getHttpStatus() {
        return httpStatus;
        }
}
