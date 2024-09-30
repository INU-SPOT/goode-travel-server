package com.spot.good2travel.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionMessage {
        
        //토큰 관련 메시지
        TOKEN_EXPIRED("토큰이 만료되었습니다.", 0, HttpStatus.UNAUTHORIZED),
        TOKEN_NOT_AUTHORIZED("권한이 없습니다", 0, HttpStatus.FORBIDDEN),
        TOKEN_UNAUTHENTICATED("인증되지 않은 토큰입니다.", 0, HttpStatus.UNAUTHORIZED),
        TOKEN_INVALID_FORMAT("잘못된 형식의 토큰입니다.", 0, HttpStatus.UNAUTHORIZED),
        TOKEN_NOT_FOUND("토큰이 비었거나 null입니다", 0, HttpStatus.BAD_REQUEST),
        TOKEN_TYPE_INVALID("토큰 타입이 틀렸습니다.", 0, HttpStatus.BAD_REQUEST),

        //유저 관련 메시지
        USER_NOT_FOUND("멤버가 존재하지 않습니다.", 0, HttpStatus.NOT_FOUND),
        USER_UNAUTHENTICATED("접근 권한이 없는 페이지입니다.", 0, HttpStatus.UNAUTHORIZED),

        //이미지 관련 메시지
        IMAGE_EMPTY("이미지가 비었습니다.", 0, HttpStatus.BAD_REQUEST),
        IMAGE_SEND_ERROR("이미지 처리 중 예기치 못한 오류가 발생했습니다.", 0, HttpStatus.BAD_REQUEST),
        IMAGE_READ_ERROR("이미지를 읽을 수 없습니다.", 0, HttpStatus.BAD_REQUEST),

        //지역구 관련 메시지
        METROPOLITAN_GOVERNMENT_NOT_FOUND("존재하지 않는 지자체입니다.", 0, HttpStatus.NOT_FOUND),
        LOCAL_GOVERNMENT_NOT_FOUND("존재하지 않는 지역 시/구 입니다.",0,HttpStatus.NOT_FOUND),

        //카테고리 관련 메시지
        CATEGORY_NOT_FOUND("존재하지 않는 굳이 카테고리 입니다.", 0, HttpStatus.NOT_FOUND),

        //dto 유효성 검사 메시지
        FAILED_VALIDATION("유효성 검사 실패", 0, HttpStatus.BAD_REQUEST),

        //게시글 관련 메시지
        POST_NOT_FOUND("존재하지 않는 게시글입니다.", 0, HttpStatus.NOT_FOUND),
        ITEM_POST_NOT_FOUND("존재하지 않는 ITEMPOST 연결 테이블입니다.", 0, HttpStatus.NOT_FOUND),
        ITEM_POST_IMAGE_NOT_FOUND("존재하지 않는 게시글 이미지입니다.", 0, HttpStatus.NOT_FOUND),

        //굳이/계획 관련 메시지
        ITEM_NOT_FOUND("존재하지 않는 굳이/계획 입니다.", 0, HttpStatus.NOT_FOUND),
        ITEM_DETAIL_INFO_NOT_FOUND("공식적인 굳이가 아니라 상세 정보를 찾을 수 없습니다.", 0, HttpStatus.BAD_REQUEST),
        ITEM_DELETE_BAD_REQUEST("잘못된 Item 삭제 요청입니다.", 0, HttpStatus.BAD_REQUEST),
        ITEM_TYPE_NOT_OFFICIAL_GOODE("Item 타입이 굳이가 아니거나 공식이 아닙니다.", 0, HttpStatus.BAD_REQUEST),
        ITEM_TYPE_NOT_PLAN("Item 타입이 계획이 아니거나 공식이 아닙니다.", 0, HttpStatus.BAD_REQUEST),
        RECOMMEND_ITEM_NOT_FOUND("추천 할 아이템이 없습니다.", 0, HttpStatus.NOT_FOUND),
        ITEM_IS_EXIST("이미 존재하는 아이템입니다.", 0, HttpStatus.BAD_REQUEST),

        //폴더 관련 메시지
        ITEM_FOLDER_NOT_FOUND("해당 Item이 포함된 폴더를 찾을 수 없습니다.", 0, HttpStatus.NOT_FOUND),
        FOLDER_NOT_FOUND("Folder가 존재하지 않습니다.",0,HttpStatus.NOT_FOUND),

        //댓글 관련 메시지
        COMMENT_NOT_FOUND("댓글이 존재하지 않습니다.", 0, HttpStatus.NOT_FOUND),
        REPLY_COMMENT_NOT_FOUND("답글이 존재하지 않습니다.", 0, HttpStatus.NOT_FOUND),
        COMMENT_NOT_USERS("댓글 소유자가 아니여서 지울 수 없습니다.", 0, HttpStatus.BAD_REQUEST),
        ALREADY_REPORTED("이미 신고한 댓글입니다.", 0, HttpStatus.BAD_REQUEST),
        ITEM_UPDATE_BAD_REQUEST("잘못된 Item 수정 요청입니다.", 0, HttpStatus.BAD_REQUEST),
        
        //fcm 관련 메시지
        FCM_TOKEN_NOT_FOUND("FCM 토큰을 찾을 수 없습니다.", 0, HttpStatus.NOT_FOUND),

        //알림 관련 메세지
        NOTIFICATION_NOT_FOUND("해당 알림 내용을 찾을 수 없습니다.", 0 ,HttpStatus.NOT_FOUND),

        //코스 관련 메시지
        COURSE_NOT_FOUND("해당 코스가 존재하지 않습니다.", 0, HttpStatus.NOT_FOUND),
        ITEM_ISEXIST_COURSE("이미 코스가 존재하는 Goode입니다.", 0, HttpStatus.BAD_REQUEST),

        //날씨 관련 메시지
        WEATHER_READ_EXCEPTION("날씨 정보를 받아오던 중 예기치 못한 에러가 발생했습니다.", 0, HttpStatus.BAD_REQUEST);

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
