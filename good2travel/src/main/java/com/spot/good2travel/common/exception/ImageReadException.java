package com.spot.good2travel.common.exception;

public class ImageReadException extends RuntimeException{
    public ImageReadException(ExceptionMessage m){
        super(m.getMessage());
    }
}
