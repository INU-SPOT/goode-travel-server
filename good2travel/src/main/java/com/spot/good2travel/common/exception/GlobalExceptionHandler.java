package com.spot.good2travel.common.exception;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.spot.good2travel.common.dto.CommonResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = NotFoundElementException.class)
    public ResponseEntity<CommonResponse> handleNotFoundElementException(NotFoundElementException ex) {
        log.error("[handleNotFoundElementException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(value = FailedTokenCreateException.class)
    public ResponseEntity<CommonResponse> handleTokenCreateError(FailedTokenCreateException ex) {
        log.error("[handleTokenCreateError] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(value = JwtEmptyException.class)
    public ResponseEntity<CommonResponse> handleJwtEmptyException(JwtEmptyException ex) {
        log.error("[handleJwtEmptyException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<CommonResponse> handleExpiredJwtException(ExpiredJwtException ex) {
        log.error("[handleExpiredJwtException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CommonResponse<?>> handleIllegalStateException(IllegalStateException ex) {
        log.error("[handleNotEnoughPrivilegeException {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("[handleIllegalArgumentException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(value = ImageEmptyException.class)
    public ResponseEntity<CommonResponse<?>> handleImageEmptyException(ImageEmptyException ex) {
        log.error("[handleImageEmptyException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(value = ImageReadException.class)
    public ResponseEntity<CommonResponse<?>> handleImageReadException(ImageReadException ex){
        log.error("[handleImageReadException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(value = ImageSendException.class)
    public ResponseEntity<CommonResponse<?>> handleImageReadException(ImageSendException ex){
        log.error("[handleImageSendException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(value = ItemAccessException.class)
    public ResponseEntity<CommonResponse<?>> handleItemUpdateException(ItemAccessException ex){
        log.error("[handleItemUpdateException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(value = NotAuthorizedUserException.class)
    public ResponseEntity<CommonResponse<?>> handleNotFoundGoodeInFolderException(NotAuthorizedUserException ex){
        log.error("[handleNotFoundGoodeInFolderException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(value = UserNotAuthorizedException.class)
    public ResponseEntity<CommonResponse<?>> handleUserNotAuthorizedException(UserNotAuthorizedException ex){
        log.error("[handleUserNotAuthorizedException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(value = ItemTypeException.class)
    public ResponseEntity<CommonResponse<?>> handleItemTypeException(ItemTypeException ex){
        log.error("[handleItemTypeException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(value = FirebaseMessagingException.class)
    public ResponseEntity<CommonResponse<?>> handleFirebaseMessagingException(FirebaseMessagingException ex){
        log.error("[handleFirebaseMessagingException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(value = ItemCourseIsExistException.class)
    public ResponseEntity<CommonResponse<?>> handleItemCourseIsExistException(ItemCourseIsExistException ex){
        log.error("[handleItemCourseIsExistException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(value = WeatherReadException.class)
    public ResponseEntity<CommonResponse> handleWeatherReadException(WeatherReadException ex){
        log.error("[handleWeatherReadException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(ex.getMessage(), null));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("유효성 검사 실패: {}", errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(ExceptionMessage.FAILED_VALIDATION.getMessage(), errorMessage));
    }
}
