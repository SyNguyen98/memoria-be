package org.chika.memoria.services;

import lombok.AllArgsConstructor;
import org.chika.memoria.client.MicrosoftGraphClient;
import org.chika.memoria.client.models.Item;
import org.chika.memoria.dtos.ItemDTO;
import org.chika.memoria.exceptions.ForbiddenException;
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
    public List<ItemDTO> getAllItemsByLocationId(final String userEmail, final String locationId, final String thumbnailSize) {
        final Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", locationId));
        if (collectionRepository.existsByIdAndOwnerEmailOrUserEmailsContains(location.getCollectionId(), userEmail, userEmail)) {
            return microsoftGraphClient.getAllChildrenByItemId(location.getDriveItemId()).stream()
                    .map(item -> thumbnailSize == null ? ItemDTO.convert(item) : ItemDTO.convert(item, thumbnailSize))
                    .toList();
        }
        throw new ForbiddenException("You don't have permission to get images in this Drive Item");
    }

    public ItemDTO getImageByItemId(final String itemId) {
        final Item item = microsoftGraphClient.getDriveItemById(itemId);
        return ItemDTO.convert(item);
    }
}
