package com.example.geoIot.util;

import com.example.geoIot.exception.LatitudeValueException;
import com.example.geoIot.exception.LongitudeValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class CoordinateValidatorTest {
    private double illegalLongitude = 204.51;
    private double illegalLatitude = -95.11;
    private double legalLongitude = -175.1617;
    private double legalLatitude = 89.999;

    @Test
    @DisplayName("should return a LatitudeValueException")
    void shouldReturnALatitudeValueException() {
        CoordinateValidator validator = new CoordinateValidator();
        assertThrowsExactly(LatitudeValueException.class, () -> validator.validateCoordinate(legalLatitude, legalLongitude));
    }

    @Test
    @DisplayName("should return a LongitudeValueException")
    void shouldReturnALongitudeValueException() {
        CoordinateValidator validator = new CoordinateValidator();
        assertThrowsExactly(LongitudeValueException.class, () -> validator.validateCoordinate(illegalLongitude, legalLatitude));
    }

    @Test
    @DisplayName("should pass the test as coordinates are legal")
    void shouldPassTheTestAsCoordinatesAreLegal() {
        CoordinateValidator validator = new CoordinateValidator();
        validator.validateCoordinate(legalLongitude, legalLatitude);
    }
}
