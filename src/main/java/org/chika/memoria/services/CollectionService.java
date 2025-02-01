package org.chika.memoria.services;

import lombok.AllArgsConstructor;
import org.chika.memoria.client.MicrosoftGraphClient;
import org.chika.memoria.dtos.CreateUpdateCollectionDTO;
import org.chika.memoria.exceptions.BadRequestException;
import org.chika.memoria.exceptions.ResourceNotFoundException;
import org.chika.memoria.models.Collection;
import org.chika.memoria.repositories.CollectionRepository;
import org.chika.memoria.repositories.LocationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CollectionService {

    private static final String ROOT_DRIVE_ITEM_ID = "6713014C02E57D90!113706";

    private final CollectionRepository collectionRepository;
    private final LocationRepository locationRepository;
    private final MicrosoftGraphClient microsoftGraphClient;

    public Collection findById(final String userEmail, final String id) {
        final var collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Collection.class.getName(), "id", id));
        if (collection.getOwnerEmail().equals(userEmail)) {
            return collection;
        }
        throw new BadRequestException("You are not owner of this collection");
    }

    public Collection findByLocationId(final String userEmail, final String locationId) {
        final var location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException(Collection.class.getName(), "id", locationId));
        return findById(userEmail, location.getCollectionId());
    }

    public List<Collection> findAllByOwnerEmail(final String ownerEmail) {
        return collectionRepository.findAllByOwnerEmailOrderByLastModifiedDateDesc(ownerEmail);
    }

    public Page<Collection> findAllByOwnerEmail(final String ownerEmail, Pageable pageable) {
        return collectionRepository.findAllByOwnerEmail(ownerEmail, pageable);
    }

    public Page<Collection> findAllByOwnerEmailOrUserEmail(final String userEmail, Pageable pageable) {
        return collectionRepository.findAllByOwnerEmailOrUserEmailsContains(userEmail, userEmail, pageable);
    }

    @Transactional
    public Collection create(final String ownerEmail, final CreateUpdateCollectionDTO collectionDTO) {
        final Collection collection = collectionDTO.createNew(ownerEmail);

        final String driveItemId = microsoftGraphClient.createFolderInDriveItem(ROOT_DRIVE_ITEM_ID, collectionDTO.getName()).getId();
        collection.setDriveItemId(driveItemId);

//        final File file = new File(String.format("%s/%s", System.getProperty("java.io.tmpdir"), image.getOriginalFilename()));
//        image.transferTo(file);
//        final var item = microsoftGraphClient.uploadFile(COLLECTION_COVER_ITEM_ID, file.getName(), Files.readAllBytes(Path.of(file.getPath())));
//        final var coverImage = microsoftGraphClient.getDriveItemById(item.getId());
//        collection.setCoverImageId(coverImage.getId());
//        collection.setCoverImageUrl(coverImage.getDownloadUrl());

        return collectionRepository.save(collection);
    }

    @Transactional
    public Collection update(final String ownerEmail, final CreateUpdateCollectionDTO collectionDTO) {
        final Collection collection = collectionRepository.findById(collectionDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(Collection.class.getName(), "id", collectionDTO.getId()));
        if (collection.getOwnerEmail().equals(ownerEmail)) {
            microsoftGraphClient.updateDriveItem(collection.getDriveItemId(), collectionDTO.getName());
            return collectionRepository.save(collectionDTO.update(collection));
        }
        throw new BadRequestException("You don't have permission to update this collection");
    }

    @Transactional
    public void deleteById(final String userEmail, final String id) {
        if (collectionRepository.existsByIdAndOwnerEmail(id, userEmail)) {
            locationRepository.deleteAllByCollectionId(id);
            collectionRepository.deleteById(id);
        } else {
            throw new BadRequestException("You don't have permission to delete this collection");
        }
    }

    @Transactional
    public List<Integer> getAllDistinctTakenYearsOfCollectionsByOwnerEmail(final String ownerEmail) {
        final List<String> collectionIds = collectionRepository.findAllByOwnerEmail(ownerEmail, Pageable.unpaged())
                .stream()
                .map(Collection::getId)
                .toList();
        return locationRepository.findDistinctTakenYearByCollectionIdIn(collectionIds);
    }

    @Transactional
    public List<String> getAllDistinctUserEmailsByOwnerEmail(final String ownerEmail) {
        return collectionRepository.findDistinctUserEmailsByOwnerEmail(ownerEmail);
    }
}
