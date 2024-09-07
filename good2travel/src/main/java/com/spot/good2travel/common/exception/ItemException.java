package com.spot.good2travel.common.exception;

public class ItemException extends RuntimeException {

    public ItemException(ExceptionMessage m) {
        super(m.getMessage());
    }
}
