package com.example.geoIot.exception;

public class OpenPolygonException extends RuntimeException{
    public OpenPolygonException() {
        super("The first and last coordinates are different. The Polygon must be closed.");
    }
}
