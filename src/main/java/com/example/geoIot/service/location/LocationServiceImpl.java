package com.example.geoIot.service.location;

import com.example.geoIot.entity.Location;
import com.example.geoIot.entity.dto.CoordinateDto;

import com.example.geoIot.entity.dto.LocationDto;
import com.example.geoIot.entity.dto.PolygonSaveDto;
import com.example.geoIot.exception.OpenPolygonException;
import com.example.geoIot.repository.LocationRepository;
import com.example.geoIot.util.CoordinateValidator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationRepository locationRepository;

    private CoordinateValidator coordinateValidator;

    @Transactional
    @Override
    public LocationDto getLocation(Long id) {
        Optional<Location> location = locationRepository.findById(id);
        if (location.isEmpty()) {
            throw new NoSuchElementException("No such location");
        }
        return LocationDto.builder()
                .idLocation(location.get().getIdLocation())
                .name(location.get().getName())
                .polygon(location.get().getPolygon())
                .build();
    }

    @Override
    public List<LocationDto> getAllLocations() {
        List<Location> locationList = locationRepository.findAll();
        if (locationList.isEmpty()) {
            throw new NoSuchElementException("No locations exist yet");
        }
        return locationList.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional
    @Override
    public LocationDto saveLocation(PolygonSaveDto polygonSaveDto) {
        if (isPolygonOpen(polygonSaveDto)) {
            throw new OpenPolygonException();
        }
        Location location = new Location();
        location.setName(polygonSaveDto.getName());

        List<Coordinate> coords = new ArrayList<>();
        for (CoordinateDto dotCoords : polygonSaveDto.getCoordinates()) {
            coordinateValidator.validateCoordinate(
                    dotCoords.getLongitude(),
                    dotCoords.getLatitude()
            );

            coords.add(new Coordinate(dotCoords.getLongitude(), dotCoords.getLatitude()));
        }
        GeometryFactory geometry = new GeometryFactory();

        location.setPolygon(geometry.createPolygon((coords.toArray(new Coordinate[0]))));

        Location savedLocation = locationRepository.save(location);
        return LocationDto.builder()
                .idLocation(savedLocation.getIdLocation())
                .name(savedLocation.getName())
                .polygon(savedLocation.getPolygon())
                .build();

    }

    private LocationDto convertToDTO(Location location) {
        return LocationDto.builder()
                .idLocation(location.getIdLocation())
                .name(location.getName())
                .polygon(location.getPolygon())
                .build();
    }

    private boolean isPolygonOpen(PolygonSaveDto saveDto) {
        int lastIndex = saveDto.getCoordinates().size()-1;
        if (saveDto.getCoordinates().get(0).getLatitude() != saveDto.getCoordinates().get(lastIndex).getLatitude()
        || saveDto.getCoordinates().get(0).getLongitude() != saveDto.getCoordinates().get(lastIndex).getLongitude()
        ) {
            return true;
        } else {
            return false;
        }
    }
}
