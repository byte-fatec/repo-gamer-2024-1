package com.example.geoIot.service.location;

import com.example.geoIot.entity.Location;
import com.example.geoIot.entity.dto.CoordinateDto;
import com.example.geoIot.entity.dto.LocationDto;
import com.example.geoIot.entity.dto.PolygonSaveDto;
import com.example.geoIot.exception.OpenPolygonException;
import com.example.geoIot.repository.LocationRepository;
import com.example.geoIot.util.CoordinateValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private CoordinateValidator coordinateValidator;

    @InjectMocks
    private LocationServiceImpl locationService;

    @Test
    @DisplayName("should return a LocationDto with a given Long id")
    public void getLocation() {
        Long id = 1L;
        Location location = new Location();
        location.setIdLocation(id);
        location.setName("GetLocation Test");
        when(locationRepository.findById(id)).thenReturn(Optional.of(location));

        LocationDto locationDto = locationService.getLocation(id);
        assertNotNull(locationDto);
        assertEquals(id, locationDto.getIdLocation());
        assertEquals("GetLocation Test", locationDto.getName());

        verify(locationRepository).findById(id);
    }

    @Test
    @DisplayName("should return a list of LocationDtos")
    public void getAllLocations() {
        Coordinate[] coordinates1 = new Coordinate[]{
                new Coordinate(10.0, 20.0),
                new Coordinate(15.0, 25.0),
                new Coordinate(20.0, 30.0),
                new Coordinate(10.0, 20.0)
        };

        Coordinate[] coordinates2 = new Coordinate[]{
                new Coordinate(30.0, 40.0),
                new Coordinate(35.0, 45.0),
                new Coordinate(40.0, 50.0),
                new Coordinate(30.0, 40.0)
        };

        Polygon polygon1 = mock(Polygon.class);
        Polygon polygon2 = mock(Polygon.class);

        when(polygon1.getCoordinates()).thenReturn(coordinates1);
        when(polygon2.getCoordinates()).thenReturn(coordinates2);

        Location location1 = new Location();
        location1.setIdLocation(1L);
        location1.setName("Location 1");
        location1.setPolygon(polygon1);

        Location location2 = new Location();
        location2.setIdLocation(2L);
        location2.setName("Location 2");
        location2.setPolygon(polygon2);

        List<Location> mockLocationList = List.of(location1, location2);
        when(locationRepository.findAll()).thenReturn(mockLocationList);
        List<LocationDto> locationDtos = locationService.getAllLocations();

        assertNotNull(locationDtos);
        assertEquals(2, locationDtos.size());

        LocationDto locationDto1 = locationDtos.get(0);
        assertEquals("Location 1", locationDto1.getName());
        assertEquals(1L, locationDto1.getIdLocation());
        assertArrayEquals(coordinates1, locationDto1.getPolygon().getCoordinates()); // Check coordinates

        LocationDto locationDto2 = locationDtos.get(1);
        assertEquals("Location 2", locationDto2.getName());
        assertEquals(2L, locationDto2.getIdLocation());
        assertArrayEquals(coordinates2, locationDto2.getPolygon().getCoordinates()); // Check coordinates

        verify(locationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("should throw NoSuchElementException when no location is found")
    public void getLocationNotFound() {
        Long id = 1L;
        when(locationRepository.findById(id)).thenReturn(Optional.empty());
        assertThrowsExactly(NoSuchElementException.class, () -> locationService.getLocation(id));
    }

    @Test
    @DisplayName("should return a LocationDto of the newly saved location")
    public void saveLocation() {
        PolygonSaveDto saveDto = new PolygonSaveDto();
        saveDto.setName("SaveLocation Test");
        saveDto.setCoordinates(Arrays.asList(
                new CoordinateDto(10.0, 20.0),
                new CoordinateDto(15.0, 25.0),
                new CoordinateDto(20.0, 30.0),
                new CoordinateDto(10.0, 20.0))
        );

        Location savedLocation = new Location();
        savedLocation.setIdLocation(1L);
        savedLocation.setName("SaveLocation Test");

        Polygon polygon = mock((Polygon.class));
        savedLocation.setPolygon(polygon);

        when(locationRepository.save(any(Location.class))).thenAnswer((InvocationOnMock invocation) -> {
            Location location = invocation.getArgument(0); // Retrieve the argument passed to save()
            location.setIdLocation(1L); // Simulate setting the ID after save
            location.setPolygon(polygon); // Set the mock Polygon
            return location; // Return the location
        });

        LocationDto locationDto = locationService.saveLocation(saveDto);

        assertNotNull(locationDto);
        assertEquals(1L, locationDto.getIdLocation());
        assertEquals("SaveLocation Test", locationDto.getName());
        assertEquals(polygon, locationDto.getPolygon());
        verify(coordinateValidator, times(4)) // Once per coordinate
                .validateCoordinate(anyDouble(), anyDouble());
        ArgumentCaptor<Location> locationCaptor = ArgumentCaptor.forClass(Location.class);
        verify(locationRepository).save(locationCaptor.capture());
        Location location = locationCaptor.getValue();
        assertEquals(1L, location.getIdLocation());
        assertDoesNotThrow(() -> locationService.saveLocation(saveDto));

    }

    @Test
    @DisplayName("should throw OpenPolygonException when the first and last coordinates aren't the same")
    public void getOpenPolygonException() {
        PolygonSaveDto invalidPolygonSaveDto = PolygonSaveDto.builder()
                .name("Test Location")
                .coordinates(Arrays.asList(
                        new CoordinateDto(10.0, 20.0),
                        new CoordinateDto(15.0, 25.0),
                        new CoordinateDto(20.0, 30.0),
                        new CoordinateDto(25.0, 35.0)
                ))
                .build();

        assertThrowsExactly(OpenPolygonException.class, () -> locationService.saveLocation(invalidPolygonSaveDto));
    }
}
