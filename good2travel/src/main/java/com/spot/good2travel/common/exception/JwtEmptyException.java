package com.spot.good2travel.common.exception;

public class JwtEmptyException extends RuntimeException {

    public JwtEmptyException(ExceptionMessage m) {
        super(m.getMessage());
    }
}
