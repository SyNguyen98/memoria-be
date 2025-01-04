package org.chika.memoria.client;

import feign.FeignException;
import lombok.AllArgsConstructor;
import org.chika.memoria.client.models.Item;
import org.chika.memoria.client.models.ListItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@AllArgsConstructor
public class MicrosoftGraphClient {

    private final IMicrosoftGraphClient iMicrosoftGraphClient;
    private final MicrosoftToken microsoftToken;

    public List<Item> getAllChildrenByItemId(final String itemId) {
        ResponseEntity<ListItem> response;
        try {
            response = iMicrosoftGraphClient.getAllChildrenByItemId(itemId);
        } catch (FeignException exception) {
            if (exception.status() == HttpStatus.UNAUTHORIZED.value()) {
                microsoftToken.refreshAccessToken();
                response = iMicrosoftGraphClient.getAllChildrenByItemId(itemId);
            } else {
                throw exception;
            }
        }
        return Objects.requireNonNull(response.getBody()).getValue();
    }

    public Item getDriveItemById(final String itemId) {
        ResponseEntity<Item> response;
        try {
            response = iMicrosoftGraphClient.getDriveItemById(itemId);
        } catch (FeignException exception) {
            if (exception.status() == HttpStatus.UNAUTHORIZED.value()) {
                microsoftToken.refreshAccessToken();
                response = iMicrosoftGraphClient.getDriveItemById(itemId);
            } else {
                throw exception;
            }
        }
        return Objects.requireNonNull(response.getBody());
    }

    public Item createFolderInDriveItem(final String itemId, final String folderName) {
        ResponseEntity<Item> response;
        Map<String, Object> body = Map.of("name", folderName, "folder", new HashMap<>());
        try {
            response = iMicrosoftGraphClient.createFolderInDriveItem(itemId, body);
        } catch (FeignException exception) {
            if (exception.status() == HttpStatus.UNAUTHORIZED.value()) {
                microsoftToken.refreshAccessToken();
                response = iMicrosoftGraphClient.createFolderInDriveItem(itemId, body);
            } else {
                throw exception;
            }
        }
        return Objects.requireNonNull(response.getBody());
    }

    public void updateDriveItem(final String itemId, final String newName) {
        Map<String, String> body = Map.of("name", newName);
        try {
            iMicrosoftGraphClient.updateDriveItem(itemId, body);
        } catch (FeignException exception) {
            if (exception.status() == HttpStatus.UNAUTHORIZED.value()) {
                microsoftToken.refreshAccessToken();
                iMicrosoftGraphClient.updateDriveItem(itemId, body);
            } else {
                throw exception;
            }
        }
    }

    public Item uploadFile(final String parentId, final String fileName, final byte[] file) {
        ResponseEntity<Item> response;
        try {
            response = iMicrosoftGraphClient.uploadFile(parentId, fileName, file);
        } catch (FeignException exception) {
            if (exception.status() == HttpStatus.UNAUTHORIZED.value()) {
                microsoftToken.refreshAccessToken();
                response = iMicrosoftGraphClient.uploadFile(parentId, fileName, file);
            } else {
                throw exception;
            }
        }
        return Objects.requireNonNull(response.getBody());
    }

    public void deleteDriveItem(final String itemId) {
        try {
            iMicrosoftGraphClient.deleteDriveItem(itemId);
        } catch (FeignException exception) {
            if (exception.status() == HttpStatus.UNAUTHORIZED.value()) {
                microsoftToken.refreshAccessToken();
                iMicrosoftGraphClient.deleteDriveItem(itemId);
            } else {
                throw exception;
            }
        }
    }
}
