package org.chika.memoria.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import org.chika.memoria.constants.Tag;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CollectionRecord(
        String id,
        @NotBlank
        String name,
        String description,
        List<Tag> tags,
        String ownerEmail,
        Set<String> userEmails,
        Instant lastModifiedDate,
        List<CollectionLocationRecord> locations
) {
}