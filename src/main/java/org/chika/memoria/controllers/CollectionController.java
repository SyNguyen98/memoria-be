package org.chika.memoria.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chika.memoria.dtos.CollectionDTO;
import org.chika.memoria.dtos.CreateCollectionDTO;
import org.chika.memoria.models.Collection;
import org.chika.memoria.security.CurrentUser;
import org.chika.memoria.security.UserPrincipal;
import org.chika.memoria.services.CollectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/collections")
@AllArgsConstructor
@Slf4j
@Tag(name = "Collection APIs", description = "/api/collections")
public class CollectionController {

    private final CollectionService collectionService;

    @Operation(summary = "Get all collections that current user owned", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping
    public ResponseEntity<List<CollectionDTO>> getAllCollections(@CurrentUser UserPrincipal userPrincipal) {
        log.debug("GET - get all collections that current user owned");
        final var email = userPrincipal.getEmail();
        return ResponseEntity.ok(collectionService.findAllByOwnerEmail(email).stream().map(CollectionDTO::new).toList());
    }

    @Operation(summary = "Get all collections that user have access to", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @GetMapping("/all")
    public ResponseEntity<List<CollectionDTO>> getAllCollectionsThatHaveAccess(@CurrentUser UserPrincipal userPrincipal) {
        log.debug("GET - get all collections that user have access to");
        final String email = userPrincipal.getEmail();
        final List<Collection> collections = collectionService.findAllByOwnerEmailOrUserEmail(email);
        return ResponseEntity.ok(collections.stream().map(CollectionDTO::new).toList());
    }

    @Operation(summary = "Get a collection by ID", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Bad Request", content = @Content())
    })
    @GetMapping("/{id}")
    public ResponseEntity<CollectionDTO> getCollectionById(@CurrentUser UserPrincipal userPrincipal, @PathVariable final String id) {
        log.debug("GET - get a collection by id");
        return ResponseEntity.ok(new CollectionDTO(collectionService.findById(userPrincipal.getEmail(), id)));
    }

    @Operation(summary = "Create a collection", responses = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CollectionDTO> createCollection(@CurrentUser UserPrincipal userPrincipal,
                                                          @RequestBody final CreateCollectionDTO collectionDTO) throws URISyntaxException {
        log.debug("POST - create a collection");
        final var collection = collectionService.create(userPrincipal.getEmail(), collectionDTO);
        return ResponseEntity.created(new URI("/api/collections/" + collection.getId())).body(new CollectionDTO(collection));
    }

    @Operation(summary = "Update a collection", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Bad Request", content = @Content())
    })
    @PutMapping
    public ResponseEntity<CollectionDTO> updateCollection(@CurrentUser UserPrincipal userPrincipal,
                                                          @RequestBody @Valid final Collection collection) {
        log.debug("PUT - update a collection");
        return ResponseEntity.ok(new CollectionDTO(collectionService.update(userPrincipal.getEmail(), collection)));
    }

    @Operation(summary = "Delete a collection by ID", responses = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Bad Request", content = @Content())
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteCollectionById(@CurrentUser UserPrincipal userPrincipal,
                                                     @PathVariable final String id) {
        log.debug("DELETE - delete a collection by id");
        collectionService.deleteById(userPrincipal.getEmail(), id);
        return ResponseEntity.noContent().build();
    }
}
