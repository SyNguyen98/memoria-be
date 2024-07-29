package org.chika.memoria.services;

import lombok.RequiredArgsConstructor;
import org.chika.memoria.client.MicrosoftGraphClient;
import org.chika.memoria.dtos.CreateUpdateLocationDTO;
import org.chika.memoria.exceptions.BadRequestException;
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

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final CollectionRepository collectionRepository;
    private final MicrosoftGraphClient microsoftGraphClient;

    @Transactional(readOnly = true)
    public Page<Location> findAllByCollectionId(final String ownerEmail, final String collectionId, Pageable pageable) {
        if (collectionRepository.existsByIdAndOwnerEmailOrUserEmailsContains(collectionId, ownerEmail, ownerEmail)) {
            return locationRepository.findAllByCollectionIdOrderByTakenYearDescTakenMonthDescTakenDayDescTakenTimeDesc(collectionId, pageable);
        }
        throw new BadRequestException("You are not owner of this collection");
    }

    @Transactional(readOnly = true)
    public Location findById(final String ownerEmail, String id) {
        final Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Location.class.getSimpleName(), "id", id));
        if (collectionRepository.existsByIdAndOwnerEmailOrUserEmailsContains(location.getCollectionId(), ownerEmail, ownerEmail)) {
            return location;
        }
        throw new BadRequestException("You don't have permission to get this location");
    }

    @Transactional
    public Location create(final String ownerEmail, final CreateUpdateLocationDTO locationDTO) {
        final Collection collection = getCollectionById(locationDTO.getCollectionId());
        if (collection.getOwnerEmail().equals(ownerEmail)) {
            final Location location = locationDTO.convert();

            final String driveItemId = microsoftGraphClient.createFolderInDriveItem(collection.getDriveItemId(), locationDTO.getPlace()).getId();
            location.setDriveItemId(driveItemId);

            collection.setLastModifiedDate(Instant.now());
            collectionRepository.save(collection);

            return locationRepository.save(location);
        }
        throw new BadRequestException("You are not owner of this collection to create a location");
    }

    @Transactional
    public Location update(final String ownerEmail, final CreateUpdateLocationDTO locationDTO) {
        Location location = locationRepository.findById(locationDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(Location.class.getSimpleName(), "id", locationDTO.getId()));
        final Collection collection = getCollectionById(location.getCollectionId());
        if (collection.getOwnerEmail().equals(ownerEmail)) {
            location = locationDTO.update(location);

            collection.setLastModifiedDate(Instant.now());
            collectionRepository.save(collection);

            return locationRepository.save(location);
        }
        throw new BadRequestException("You don't have permission update this location");
    }

    @Transactional
    public void deleteAllByCollectionId(final String ownerEmail, final String collectionId) {
        if (collectionRepository.existsByIdAndOwnerEmail(collectionId, ownerEmail)) {
            locationRepository.deleteAllByCollectionId(collectionId);
        } else {
            throw new BadRequestException("You are not owner of this collection to delete locations");
        }
    }

    @Transactional
    public void deleteById(final String ownerEmail, String id) {
        final Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Location.class.getName(), "id", id));
        final Collection collection = getCollectionById(location.getCollectionId());
        if (collection.getOwnerEmail().equals(ownerEmail)) {

            collection.setLastModifiedDate(Instant.now());
            collectionRepository.save(collection);

            locationRepository.delete(location);
        } else {
            throw new BadRequestException("You don't have permission update this location");
        }
    }

    private Collection getCollectionById(String id) {
        return collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Collection.class.getName(), "id", id));
    }
}
