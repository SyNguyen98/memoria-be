package org.chika.memoria.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chika.memoria.dtos.ItemDTO;
import org.chika.memoria.security.CurrentUser;
import org.chika.memoria.security.UserPrincipal;
import org.chika.memoria.services.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@AllArgsConstructor
@Slf4j
@Tag(name = "Item APIs", description = "/api/items")
public class ItemController {

    private final ItemService itemService;

    @Operation(summary = "Get all items by locationId", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Bad Request", content = @Content())
    })
    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAllItemsByLocationId(@CurrentUser UserPrincipal userPrincipal,
                                                                 @RequestParam final String locationId,
                                                                 @Parameter(description = "value is \"large\", \"medium\" or \"small\"") @RequestParam(required = false) final String thumbnailSize) {
        log.debug("GET - get all items by locationId {}", locationId);
        return ResponseEntity.ok(itemService.getAllItemsByLocationId(userPrincipal.getEmail(), locationId, thumbnailSize));
    }

    @Operation(summary = "Get an item by ID", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Bad Request", content = @Content())
    })
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable final String itemId) {
        log.debug("GET - get an item by ID {}", itemId);
        return ResponseEntity.ok(itemService.getImageByItemId(itemId));
    }
}
