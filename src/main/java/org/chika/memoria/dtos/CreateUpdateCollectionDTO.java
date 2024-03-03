package org.chika.memoria.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.chika.memoria.models.Collection;
import org.chika.memoria.models.Tag;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Data
public class CreateUpdateCollectionDTO {

    private String id;
    @NotBlank
    private String name;
    private String description;
    private List<Tag> tags;
    private Set<String> userEmails;

    public Collection createNew(final String ownerEmail) {
        return Collection.builder()
                .name(this.name)
                .description(this.description)
                .ownerEmail(ownerEmail)
                .tags(this.tags)
                .userEmails(this.userEmails)
                .lastModifiedDate(Instant.now())
                .build();
    }

    public Collection update(final Collection collection) {
        collection.setName(this.name);
        collection.setDescription(this.description);
        collection.setTags(this.tags);
        collection.setLastModifiedDate(Instant.now());
        return collection;
    }
}
