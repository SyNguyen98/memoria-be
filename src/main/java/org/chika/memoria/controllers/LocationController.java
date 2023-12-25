package org.chika.memoria.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chika.memoria.dtos.CreateLocationDTO;
import org.chika.memoria.dtos.LocationDTO;
import org.chika.memoria.models.Location;
import org.chika.memoria.security.CurrentUser;
import org.chika.memoria.security.UserPrincipal;
import org.chika.memoria.services.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/locations")
@AllArgsConstructor
@Slf4j
@Tag(name = "Location APIs", description = "/api/locations")
public class LocationController {

    private final LocationService locationService;

    @Operation(summary = "Get all locations by collection's ID", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocationsByCollectionId(@RequestParam final String collectionId,
                                                                           @CurrentUser UserPrincipal userPrincipal) {
        log.debug("GET - get all locations by collection's ID");
        final var email = userPrincipal.getEmail();
        return ResponseEntity.ok(locationService.findAllByCollectionId(email, collectionId).stream().map(LocationDTO::new).toList());
    }

    @Operation(summary = "Create a location", responses = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LocationDTO> createLocation(@RequestBody final CreateLocationDTO locationDTO,
                                                      @CurrentUser UserPrincipal userPrincipal) throws URISyntaxException {
        log.debug("POST - create a location");
        final var location = locationService.create(userPrincipal.getEmail(), locationDTO);
        return ResponseEntity.created(new URI("/api/locations/" + location.getId())).body(new LocationDTO(location));
    }

    @Operation(summary = "Update a location", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @PutMapping
    public ResponseEntity<LocationDTO> updateLocation(@RequestBody final Location location,
                                                      @CurrentUser UserPrincipal userPrincipal) {
        log.debug("PUT - update a location");
        return ResponseEntity.ok(new LocationDTO(locationService.update(userPrincipal.getEmail(), location)));
    }

    @Operation(summary = "Delete all locations by collection's ID", responses = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Bad Request", content = @Content())
    })
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteAllLocationsByCollectionId(@CurrentUser UserPrincipal userPrincipal,
                                                                 @RequestParam final String collectionId) {
        log.debug("DELETE - delete all locations by collection's ID");
        locationService.deleteAllByCollectionId(userPrincipal.getEmail(), collectionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a location by ID", responses = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Bad Request", content = @Content())
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteLocationById(@CurrentUser UserPrincipal userPrincipal,
                                                     @PathVariable final String id) {
        log.debug("DELETE - delete a location by id");
        locationService.deleteById(userPrincipal.getEmail(), id);
        return ResponseEntity.noContent().build();
    }
}
