package com.example.geoIot.entity.dto;

import lombok.*;
import org.locationtech.jts.geom.Polygon;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDto {
    private Long idLocation;
    private String name;
    private Polygon polygon;

}
