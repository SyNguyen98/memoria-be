package org.chika.memoria.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chika.memoria.converters.CollectionConverter;
import org.chika.memoria.dtos.CollectionRecord;
import org.chika.memoria.models.Collection;
import org.chika.memoria.security.CurrentUser;
import org.chika.memoria.security.UserPrincipal;
import org.chika.memoria.services.CollectionLocationService;
import org.chika.memoria.services.CollectionService;
import org.chika.memoria.utils.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    private final CollectionLocationService collectionLocationService;
    private final CollectionConverter collectionConverter;

    @Operation(summary = "Get all collections that user have access to", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    @GetMapping
    public ResponseEntity<List<CollectionRecord>> getAllCollectionsThatHaveAccess(@CurrentUser UserPrincipal userPrincipal,
                                                                                  @RequestParam(required = false) String tags,
                                                                                  @RequestParam(required = false, defaultValue = "false") boolean unpaged,
                                                                                  Pageable pageable) {
        log.debug("GET - get all collections that user have access to");
        final String email = userPrincipal.getEmail();
        if (unpaged) {
            final List<CollectionRecord> collectionRecords = collectionService.findAllHaveAccessByParams(email)
                    .stream().map(collectionConverter::toRecord).toList();
            return ResponseEntity.ok(collectionRecords);
        }
        final Page<Collection> page = collectionService.findAllHaveAccessByParams(email, tags, pageable);
        final HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.stream().map(collectionConverter::toRecord).toList());
    }

    @Operation(summary = "Get all collections that current user owned", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    @GetMapping("/owned")
    public ResponseEntity<List<CollectionRecord>> getAllCollectionsThatOwned(@CurrentUser UserPrincipal userPrincipal, Pageable pageable) {
        log.debug("GET - get all collections that current user owned");
        final String email = userPrincipal.getEmail();
        final Page<Collection> page = collectionService.findAllByOwnerEmail(email, pageable);
        final HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.stream().map(collectionConverter::toRecord).toList());
    }

    @Operation(summary = "Get all distinct years of collections that user have access to", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    @GetMapping("/years")
    public ResponseEntity<List<Integer>> getAllYearOfCollection(@CurrentUser UserPrincipal userPrincipal) {
        log.debug("GET - get all distinct years of collections that user have access to");
        return ResponseEntity.ok(collectionService.getAllDistinctTakenYearsOfCollectionsHaveAccess(userPrincipal.getEmail()));
    }

    @Operation(summary = "Get all distinct user emails of collections that user have access to", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    @GetMapping("/user-emails")
    public ResponseEntity<List<String>> getAllUserEmailsOfCollectionByOwnerEmail(@CurrentUser UserPrincipal userPrincipal) {
        log.debug("GET - get all distinct user emails of collections that user have access to");
        return ResponseEntity.ok(collectionService.getAllDistinctUserEmailsByOwnerEmail(userPrincipal.getEmail()));
    }

    @Operation(summary = "Get a collection by ID", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    @GetMapping("/{id}")
    public ResponseEntity<CollectionRecord> getCollectionById(@CurrentUser UserPrincipal userPrincipal, @PathVariable final String id) {
        log.debug("GET - get a collection by id");
        return ResponseEntity.ok(collectionConverter.toRecord(collectionService.findById(userPrincipal.getEmail(), id)));
    }

    @Operation(summary = "Get a collection by location's ID", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    @GetMapping("/locations/{locationId}")
    public ResponseEntity<CollectionRecord> getCollectionByLocationId(@CurrentUser UserPrincipal userPrincipal, @PathVariable final String locationId) {
        log.debug("GET - get a collection by location id");
        return ResponseEntity.ok(collectionConverter.toRecord(collectionService.findByLocationId(userPrincipal.getEmail(), locationId)));
    }

    @Operation(summary = "Create a collection", responses = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CollectionRecord> createCollection(@CurrentUser UserPrincipal userPrincipal,
                                                             @RequestBody @Valid CollectionRecord collectionRecord) throws URISyntaxException {
        log.debug("POST - create a collection");
        final var collection = collectionService.create(userPrincipal.getEmail(), collectionRecord);
        return ResponseEntity.created(new URI("/api/collections/" + collection.getId())).body(collectionConverter.toRecord(collection));
    }

    @Operation(summary = "Update a collection", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    @PutMapping("/{id}")
    public ResponseEntity<CollectionRecord> updateCollection(@CurrentUser UserPrincipal userPrincipal,
                                                             @PathVariable final String id,
                                                             @RequestBody @Valid final CollectionRecord collectionRecord) {
        log.debug("PUT - update a collection");
        final var collection = collectionService.update(userPrincipal.getEmail(), id, collectionRecord);
        return ResponseEntity.ok(collectionConverter.toRecord(collection));
    }

    @Operation(summary = "Update collection's locations", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    @PutMapping("/locations")
    public ResponseEntity<Void> updateCollectionLocations() {
        log.debug("PUT - update collection's locations");
        collectionLocationService.updateCollectionLocations();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete a collection by ID", responses = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
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
