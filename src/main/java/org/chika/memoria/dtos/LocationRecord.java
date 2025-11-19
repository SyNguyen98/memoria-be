package org.chika.memoria.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.chika.memoria.models.Coordinate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LocationRecord(
        String id,
        @NotNull
        Integer takenYear,
        Integer takenMonth,
        Integer takenDay,
        String takenTime,
        Coordinate coordinate,
        @NotBlank
        String place,
        String description,
        String collectionId
) {
}