package com.spot.good2travel.common.exception;

public class NotFoundElementException extends RuntimeException {
    public NotFoundElementException(ExceptionMessage m) {
        super(m.getMessage());
    }
}
