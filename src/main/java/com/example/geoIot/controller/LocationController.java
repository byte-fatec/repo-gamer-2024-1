package com.example.geoIot.controller;

import com.example.geoIot.entity.dto.LocationDto;
import com.example.geoIot.entity.dto.PolygonSaveDto;
import com.example.geoIot.exception.OpenPolygonException;
import com.example.geoIot.service.location.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Tag(name = "Local - Controller", description = "Endpoints para criar e consultar poligonos de localizações")
@RestController
@RequestMapping("/location")
@CrossOrigin
public class LocationController {

    @Autowired
    private LocationService service;

    @GetMapping("get-polygon")
    @Operation(summary = "Busca de um polígono de local", description = "Faz uma requisição ao OracleCloud trazendo os dados de um polígono")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Polígono encontrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Requisição mal formulada."),
            @ApiResponse(responseCode = "404", description = "Polígono não existe."),
            @ApiResponse(responseCode = "408", description = "Tempo de resposta excedido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar buscar o local.")
    })
    public ResponseEntity<?> getPolygon(
            @Parameter(description = "ID do local", required = true) @RequestParam long id
    ) {
        try {
            LocationDto locationDto = service.getLocation(id);
            return ResponseEntity.ok().body(locationDto);
        } catch (NoSuchElementException noSuchElementException) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.internalServerError().body("Internal Server Error" + runtimeException.getMessage());
        }
    }

    @GetMapping("get-all-polygons")
    @Operation(summary = "Busca de todos os polígonos de locais", description = "Faz uma requisição ao OracleCloud trazendo todos os dados de polígonos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Todos os polígonos encontrados com sucesso."),
            @ApiResponse(responseCode = "400", description = "Requisição mal formulada."),
            @ApiResponse(responseCode = "404", description = "Nenhum polígono existe."),
            @ApiResponse(responseCode = "408", description = "Tempo de resposta excedido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar buscar os locais.")
    })
    public ResponseEntity<?> getAllPolygons() {
        try {
            List<LocationDto> locationDtoList = service.getAllLocations();
            return ResponseEntity.ok().body(locationDtoList);
        } catch (NoSuchElementException noSuchElementException) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.internalServerError().body("Internal Server Error" + runtimeException.getMessage());
        }
    }

    @Operation(summary = "Inserção de um polígono de local", description = "Faz uma requisição ao OracleCloud salvando os dados de um polígono")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Polígono criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Requisição mal formulada."),
            @ApiResponse(responseCode = "408", description = "Tempo de resposta excedido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar criar o local.")
    })
    @PostMapping("/save-polygon")
    public ResponseEntity<?> savePolygon(
            @Parameter(description = "Nome e Lista de coordenadas que delimitam o poligono",required = true) @RequestParam PolygonSaveDto saveDto
    ) {
        try {
            LocationDto createdLocation = service.saveLocation(saveDto);
            return ResponseEntity.status(201).body(createdLocation);
        } catch (OpenPolygonException e) {
            return ResponseEntity.badRequest().body("Bad Request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Bad Request: " + e.getMessage());
        }
    }
}
