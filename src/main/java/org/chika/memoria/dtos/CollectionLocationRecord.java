package org.chika.memoria.dtos;

import org.chika.memoria.models.Coordinate;

public record CollectionLocationRecord(
        String name,
        String isoLevel,
        Coordinate coordinate
) {
}
