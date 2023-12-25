package org.chika.memoria.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ItemDTO {

    private String id;

    private Instant createdDateTime;

    private Instant lastModifiedDateTime;

    private Instant takenDateTime;

    private String name;

    private String mimeType;

    private String downloadUrl;
}
