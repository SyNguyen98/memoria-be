package org.chika.memoria.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("coordinate")
@Data
@Builder
@AllArgsConstructor
public class Coordinate {

    private Double latitude;
    private Double longitude;
}
