package org.chika.memoria.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.chika.memoria.models.Collection;
import org.chika.memoria.constants.Tag;
import org.chika.memoria.models.CollectionLocation;
import org.chika.memoria.models.Coordinate;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CollectionDTO {

    private String id;
    private String name;
    private String description;
    private List<Tag> tags;
    private String ownerEmail;
    private Set<String> userEmails;
    private Instant lastModifiedDate;
    private List<CollectionLocationDTO> locations;

    public CollectionDTO(final Collection collection) {
        this.id = collection.getId();
        this.name = collection.getName();
        this.description = collection.getDescription();
        this.tags = collection.getTags();
        this.ownerEmail = collection.getOwnerEmail();
        this.userEmails = collection.getUserEmails();
        this.lastModifiedDate = collection.getLastModifiedDate();
        this.locations = collection.getLocations().stream()
                .map(CollectionLocationDTO::new)
                .toList();
    }

    @Data
    public static class CollectionLocationDTO {
        private String name;
        private String isoLevel;
        private Coordinate coordinate;

        public CollectionLocationDTO(final CollectionLocation location) {
            this.name = location.getName();
            this.isoLevel = location.getIsoLevel();
            this.coordinate = Coordinate.builder()
                    .longitude(location.getCoordinate().getLongitude())
                    .latitude(location.getCoordinate().getLatitude())
                    .build();
        }
    }
}
