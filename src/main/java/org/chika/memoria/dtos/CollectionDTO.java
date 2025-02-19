package org.chika.memoria.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.chika.memoria.models.Collection;
import org.chika.memoria.constants.Tag;

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

    public CollectionDTO(final Collection collection) {
        this.id = collection.getId();
        this.name = collection.getName();
        this.description = collection.getDescription();
        this.tags = collection.getTags();
        this.ownerEmail = collection.getOwnerEmail();
        this.userEmails = collection.getUserEmails();
        this.lastModifiedDate = collection.getLastModifiedDate();
    }
}
