package com.spot.good2travel.common.exception;

import com.spot.good2travel.service.ImageService;

public class ImageSendException extends RuntimeException{
    public ImageSendException(ExceptionMessage m){
        super(m.getMessage());
    }
}
