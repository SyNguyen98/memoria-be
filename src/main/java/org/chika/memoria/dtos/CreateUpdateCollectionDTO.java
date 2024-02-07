package org.chika.memoria.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.chika.memoria.models.Collection;

import java.util.Set;

@Data
public class CreateUpdateCollectionDTO {

    private String id;
    @NotBlank
    private String name;
    private String description;
    private Set<String> userEmails;

    public Collection convert(final String ownerEmail) {
        return Collection.builder().name(name)
                .description(description)
                .ownerEmail(ownerEmail)
                .userEmails(userEmails).build();
    }
}
