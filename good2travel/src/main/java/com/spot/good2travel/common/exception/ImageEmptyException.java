package com.spot.good2travel.common.exception;

public class ImageEmptyException extends RuntimeException{
    public ImageEmptyException(ExceptionMessage m){
        super(m.getMessage());
    }
}
