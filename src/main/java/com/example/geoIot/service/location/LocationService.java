package com.example.geoIot.service.location;

import com.example.geoIot.entity.dto.LocationDto;
import com.example.geoIot.entity.dto.PolygonSaveDto;

import java.util.List;

public interface LocationService {
    LocationDto getLocation(Long id);
    List<LocationDto> getAllLocations();
    LocationDto saveLocation(PolygonSaveDto polygonSaveDto);
}
