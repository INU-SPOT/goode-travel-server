package com.spot.good2travel.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionMessage {
        //토큰
        TOKEN_EXPIRED("토큰이 만료되었습니다.", 0, HttpStatus.UNAUTHORIZED),
        TOKEN_NOT_AUTHORIZED("권한이 없습니다", 0, HttpStatus.FORBIDDEN),
        TOKEN_UNAUTHENTICATED("인증되지 않은 토큰입니다.", 0, HttpStatus.UNAUTHORIZED),
        TOKEN_INVALID_FORMAT("잘못된 형식의 토큰입니다.", 0, HttpStatus.UNAUTHORIZED),
        TOKEN_NOT_FOUND("토큰이 비었거나 null입니다", 0, HttpStatus.BAD_REQUEST),
        TOKEN_TYPE_INVALID("토큰 타입이 틀렸습니다.", 0, HttpStatus.BAD_REQUEST),

        //유저
        MEMBER_NOT_FOUND("멤버가 존재하지 않습니다.", 0, HttpStatus.NOT_FOUND),
        MEMBER_PROJECT_NOT_FOUND("멤버-프로젝트가 존재하지 않습니다.", 0, HttpStatus.NOT_FOUND),
        MEMBER_UNAUTHENTICATED("접근 권한이 없는 페이지입니다.", 0, HttpStatus.UNAUTHORIZED),

        //Item
        ITEM_NOT_FOUND("Item을 찾을 수 없습니다.", 0, HttpStatus.NOT_FOUND),
        ITEM_FOLDER_NOT_FOUND("해당 Item이 포함된 폴더를 찾을 수 없습니다.", 0, HttpStatus.NOT_FOUND),

        //Folder
        FOLDER_NOT_FOUND("Folder가 존재하지 않습니다.",0,HttpStatus.NOT_FOUND),
        FOLDER_BAD_REQUEST("잘못된 Type을 입력했습니다", 0, HttpStatus.BAD_REQUEST);



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
