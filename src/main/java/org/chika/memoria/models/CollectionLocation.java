package org.chika.memoria.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("locations")
@Data
@Builder
public class CollectionLocation {

    @Field("name")
    private String name;

    @Field("isoLevel")
    private String isoLevel;

    @Field("coordinate")
    private Coordinate coordinate;
}
