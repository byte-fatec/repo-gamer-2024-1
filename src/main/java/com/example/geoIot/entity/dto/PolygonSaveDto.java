package com.example.geoIot.entity.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PolygonSaveDto {
    private List<CoordinateDto> coordinates;
    private String name;
}
