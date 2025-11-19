package org.chika.memoria.dtos;

import java.time.Instant;

public record ItemRecord(
        String id,
        Instant createdDateTime,
        Instant lastModifiedDateTime,
        Instant takenDateTime,
        String name,
        String mimeType,
        String downloadUrl,
        String thumbnailUrl
) {
}