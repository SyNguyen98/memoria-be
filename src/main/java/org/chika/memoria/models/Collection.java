package org.chika.memoria.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Document("collections")
@Data
@Builder
public class Collection {

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("tags")
    private List<Tag> tags;

    @Field("lastModifiedDate")
    private Instant lastModifiedDate;

    @Field("ownerEmail")
    private String ownerEmail;

    @Field("userEmails")
    private Set<String> userEmails;

    @Field("driveItemId")
//    @Indexed(unique = true)
    private String driveItemId;
}
