package org.chika.memoria.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chika.memoria.client.OpenStreetMapClient;
import org.chika.memoria.exceptions.ResourceNotFoundException;
import org.chika.memoria.models.Collection;
import org.chika.memoria.models.CollectionLocation;
import org.chika.memoria.models.Coordinate;
import org.chika.memoria.models.Location;
import org.chika.memoria.repositories.CollectionRepository;
import org.chika.memoria.repositories.LocationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class CollectionLocationService {

    private final CollectionRepository collectionRepository;
    private final LocationRepository locationRepository;
    private final OpenStreetMapClient openStreetMapClient;

    public void updateCollectionLocations() {
        locationRepository.findAll().forEach(location -> {
            var collection = collectionRepository.findById(location.getCollectionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Collection", "ID", location.getCollectionId()));

            log.info("Updating collection location {}, collection {}", location.getPlace(), collection.getName());

            collection = updateCollectionLocation(collection, location);

            collectionRepository.save(collection);

            try {
                Thread.sleep(1000); // 1-second delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread was interrupted", e);
            }
        });
    }

    public Collection updateCollectionLocation(Collection collection, Location location) {
        // Reverse Geocode
        var openStreetMap = openStreetMapClient.reverseGeocode(location.getCoordinate().getLatitude(), location.getCoordinate().getLongitude());

        // Update Collection Locations
        var collectionLocations = Optional.ofNullable(collection.getLocations()).orElse(new ArrayList<>());

        if (collectionLocations.stream().noneMatch(lo -> Objects.equals(lo.getIsoLevel(), openStreetMap.getAddress().getIsoLevel()))) {
            collectionLocations.add(CollectionLocation.builder()
                    .name(Optional.ofNullable(openStreetMap.getAddress().getState()).orElse(openStreetMap.getAddress().getCity()))
                    .isoLevel(openStreetMap.getAddress().getIsoLevel())
                    .coordinate(Coordinate.builder()
                            .latitude(Double.valueOf(openStreetMap.getLat()))
                            .longitude(Double.valueOf(openStreetMap.getLon()))
                            .build())
                    .build());
        }

        // Update Collection Metadata
        collection.setLocations(collectionLocations);
        collection.setLastModifiedDate(Instant.now());

        return collection;
    }
}
