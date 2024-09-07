package com.spot.good2travel.common.exception;

public class FolderException extends RuntimeException {

    public FolderException(ExceptionMessage m) {
        super(m.getMessage());
    }
}
