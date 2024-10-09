package com.example.geoIot.util;

import com.example.geoIot.exception.LatitudeValueException;
import com.example.geoIot.exception.LongitudeValueException;

public class CoordinateValidator {

    public void validateCoordinate(double longitude, double latitude) {
        validateLatitude(latitude);
        validateLongitude(longitude);
    }

    private void validateLongitude(double longitude) {
        if (longitude >= 180 || longitude <= -180) {
            throw new LongitudeValueException();
        }
    }
    private void validateLatitude(double latitude) {
        if (latitude >= 90 || latitude <= -90) {
            throw new LatitudeValueException();
        }
    }

}
