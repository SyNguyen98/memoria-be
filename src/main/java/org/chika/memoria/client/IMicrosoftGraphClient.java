package org.chika.memoria.client;

import feign.Headers;
import org.chika.memoria.client.models.Item;
import org.chika.memoria.client.models.ListItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "MicrosoftGraphClient", url = "https://graph.microsoft.com/v1.0")
public interface IMicrosoftGraphClient {

    @GetMapping("/drive/items/{itemId}/children?$expand=thumbnails")
    ResponseEntity<ListItem> getAllChildrenByItemId(@PathVariable String itemId);

    @GetMapping("/drive/items/{itemId}")
    ResponseEntity<Item> getDriveItemById(@PathVariable String itemId);

    @PostMapping("/drive/items/{itemId}/children")
    ResponseEntity<Item> createFolderInDriveItem(@PathVariable String itemId, @RequestBody Map<String, Object> body);

    @PutMapping("/drive/items/{parentId}:/{fileName}:/content")
    @Headers("Content-Type: image/jpeg")
    ResponseEntity<Item> uploadFile(@PathVariable String parentId, @PathVariable String fileName,
                                    @RequestBody byte[] file);

    @DeleteMapping("/drive/items/{itemId}")
    ResponseEntity<Void> deleteDriveItem(@PathVariable String itemId);
}
