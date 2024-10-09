package com.example.geoIot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.locationtech.jts.geom.Polygon;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id_location")
    private Long idLocation;
    @Column(name = "name")
    private String name;
    @Column(name = "poly", columnDefinition = "SDO_GEOMETRY")
    private Polygon polygon;

}
