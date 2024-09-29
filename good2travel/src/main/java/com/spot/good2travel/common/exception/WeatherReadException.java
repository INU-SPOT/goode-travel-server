package com.spot.good2travel.common.exception;

public class WeatherReadException extends RuntimeException{

    public WeatherReadException(ExceptionMessage m){
        super(m.getMessage());
    }
}
