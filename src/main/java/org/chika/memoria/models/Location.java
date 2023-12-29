package org.chika.memoria.models;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("locations")
@Data
@Builder
public class Location {

    @Id
    private String id;

    @Field("takenDay")
    @Size(min = 1, max = 31)
    private Integer takenDay;

    @Field("takenMonth")
    @Size(min = 1, max = 12)
    private Integer takenMonth;

    @Field("takenYear")
    @Size(min = 1990, max = 2100)
    private Integer takenYear;

    @Field("takenTime")
    private String takenTime;

    @Field("coordinate")
    private Coordinate coordinate;

    @Field("place")
    private String place;

    @Field("description")
    private String description;

    @Field("collectionId")
    private String collectionId;

    @Field("driveItemId")
    @Indexed(unique = true)
    private String driveItemId;
}
