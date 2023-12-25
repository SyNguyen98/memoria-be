package org.chika.memoria.services;

import lombok.AllArgsConstructor;
import org.chika.memoria.client.MicrosoftGraphClient;
import org.chika.memoria.client.models.Item;
import org.chika.memoria.dtos.ItemDTO;
import org.chika.memoria.exceptions.BadRequestException;
import org.chika.memoria.exceptions.ResourceNotFoundException;
import org.chika.memoria.models.Location;
import org.chika.memoria.repositories.CollectionRepository;
import org.chika.memoria.repositories.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemService {

    private final CollectionRepository collectionRepository;
    private final LocationRepository locationRepository;
    private final MicrosoftGraphClient microsoftGraphClient;

    @Transactional(readOnly = true)
    public List<ItemDTO> getAllImagesByDriveItemId(final String userEmail, final String driveItemId) {
        final Location location = locationRepository.findByDriveItemId(driveItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Drive Item", "id", driveItemId));
        if (collectionRepository.existsByIdAndOwnerEmailOrUserEmailsContains(location.getCollectionId(), userEmail, userEmail)) {
            return microsoftGraphClient.getAllChildrenByItemId(driveItemId).stream()
                    .map(item -> ItemDTO.builder()
                            .id(item.getId())
                            .createdDateTime(item.getCreatedDateTime())
                            .lastModifiedDateTime(item.getLastModifiedDateTime())
                            .takenDateTime(item.getPhoto().getTakenDateTime())
                            .name(item.getName())
                            .mimeType(item.getFile().getMimeType())
                            .downloadUrl(item.getDownloadUrl())
                            .build())
                    .toList();
        }
        throw new BadRequestException("You don't have permission to get images in this Drive Item");
    }

    public ItemDTO getImageByItemId(final String itemId) {
        final Item item = microsoftGraphClient.getDriveItemById(itemId);
        return ItemDTO.builder().id(item.getId())
                .createdDateTime(item.getCreatedDateTime())
                .lastModifiedDateTime(item.getLastModifiedDateTime())
                .takenDateTime(item.getPhoto().getTakenDateTime())
                .name(item.getName())
                .mimeType(item.getFile().getMimeType())
                .downloadUrl(item.getDownloadUrl())
                .build();
    }
}
