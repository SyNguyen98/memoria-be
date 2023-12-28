package org.chika.memoria.controllers;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Get all items by driveItemId", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Bad Request", content = @Content())
    })
    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAllImagesByDriveItemId(@CurrentUser UserPrincipal userPrincipal,
                                                                   @RequestParam final String driveItemId) {
        log.debug("GET - get all items by driveItemId {}", driveItemId);
        return ResponseEntity.ok(itemService.getAllImagesByDriveItemId(userPrincipal.getEmail(), driveItemId));
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
