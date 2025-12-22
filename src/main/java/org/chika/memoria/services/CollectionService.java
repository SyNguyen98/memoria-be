package org.chika.memoria.services;

import lombok.AllArgsConstructor;
import org.chika.memoria.client.MicrosoftGraphClient;
import org.chika.memoria.constants.Tag;
import org.chika.memoria.converters.CollectionConverter;
import org.chika.memoria.dtos.CollectionRecord;
import org.chika.memoria.exceptions.BadRequestException;
import org.chika.memoria.exceptions.ForbiddenException;
import org.chika.memoria.exceptions.ResourceNotFoundException;
import org.chika.memoria.models.Collection;
import org.chika.memoria.models.Location;
import org.chika.memoria.repositories.CollectionRepository;
import org.chika.memoria.repositories.LocationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class CollectionService {

    // Root drive item ID in Microsoft OneDrive
    private static final String ROOT_DRIVE_ITEM_ID = "6713014C02E57D90!113706";

    private final CollectionRepository collectionRepository;
    private final LocationRepository locationRepository;
    private final MicrosoftGraphClient microsoftGraphClient;
    private final CollectionConverter collectionConverter;

    public Collection findById(final String userEmail, final String id) {
        final var collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Collection.class.getName(), "id", id));
        if (collection.getOwnerEmail().equals(userEmail)) {
            return collection;
        }
        throw new ForbiddenException("You are not owner of this collection");
    }

    public Collection findByLocationId(final String userEmail, final String locationId) {
        final var location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException(Location.class.getName(), "id", locationId));
        return findById(userEmail, location.getCollectionId());
    }

    public Page<Collection> findAllByOwnerEmail(final String ownerEmail, Pageable pageable) {
        return collectionRepository.findAllByOwnerEmail(ownerEmail, pageable);
    }

    public List<Collection> findAllHaveAccessByParams(final String userEmail) {
        return collectionRepository.findAllByOwnerEmailOrUserEmailsContains(userEmail, userEmail);
    }

    public Page<Collection> findAllHaveAccessByParams(final String userEmail, final String tag, Pageable pageable) {
        if (tag == null || tag.isBlank()) {
            return collectionRepository.findAllByOwnerEmailOrUserEmailsContains(userEmail, userEmail, pageable);
        }
        List<Tag> tags = Arrays.stream(tag.split(",")).map(Tag::valueOf).toList();
        return collectionRepository.findAllByTagsInAndOwnerEmailOrUserEmailsContains(tags, userEmail, userEmail, pageable);
    }

    @Transactional
    public Collection create(final String ownerEmail, final CollectionRecord collectionRecord) {
        final Collection collection = collectionConverter.toEntity(collectionRecord, ownerEmail);
        collection.setLastModifiedDate(Instant.now());

        final String driveItemId = microsoftGraphClient.createFolderInDriveItem(ROOT_DRIVE_ITEM_ID, collection.getName()).getId();
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
    public Collection update(final String ownerEmail, final String id, final CollectionRecord collectionRecord) {
        if (!Objects.equals(id, collectionRecord.id())) {
            throw new BadRequestException("Collection ID in path and request body do not match");
        }
        final Collection collection = collectionRepository.findById(collectionRecord.id())
                .orElseThrow(() -> new ResourceNotFoundException(Collection.class.getName(), "id", collectionRecord.id()));
        if (collection.getOwnerEmail().equals(ownerEmail)) {
            microsoftGraphClient.updateDriveItem(collection.getDriveItemId(), collectionRecord.name());

            collection.setName(collectionRecord.name());
            collection.setDescription(collectionRecord.description());
            collection.setTags(collectionRecord.tags());
            collection.setUserEmails(collectionRecord.userEmails());
            collection.setLastModifiedDate(Instant.now());
            return collectionRepository.save(collection);
        }
        throw new ForbiddenException("You don't have permission to update this collection");
    }

    @Transactional
    public void deleteById(final String userEmail, final String id) {
        if (collectionRepository.existsByIdAndOwnerEmail(id, userEmail)) {
            locationRepository.deleteAllByCollectionId(id);
            collectionRepository.deleteById(id);
            return;
        }
        throw new ForbiddenException("You don't have permission to delete this collection");
    }

    @Transactional
    public List<Integer> getAllDistinctTakenYearsOfCollectionsHaveAccess(final String ownerEmail) {
        final List<String> collectionIds = collectionRepository.findAllByOwnerEmailOrUserEmailsContains(ownerEmail, ownerEmail)
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
