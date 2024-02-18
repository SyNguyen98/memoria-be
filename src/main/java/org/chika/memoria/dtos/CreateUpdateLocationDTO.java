package org.chika.memoria.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.chika.memoria.models.Coordinate;
import org.chika.memoria.models.Location;

@Data
public class CreateUpdateLocationDTO {

    private String id;
    @NotNull
    private Integer takenYear;
    private Integer takenMonth;
    private Integer takenDay;
    private String takenTime;
    private Coordinate coordinate;
    @NotBlank
    private String place;
    private String description;
    @NotBlank
    private String collectionId;

    public Location convert() {
        return Location.builder().takenYear(this.takenYear).takenMonth(this.takenMonth)
                .takenDay(this.takenDay).takenTime(this.takenTime)
                .coordinate(this.coordinate)
                .place(this.place)
                .description(this.description)
                .collectionId(this.collectionId)
                .build();
    }

    public Location update(final Location location) {
        location.setTakenYear(this.takenYear);
        location.setTakenMonth(this.takenMonth);
        location.setTakenDay(this.takenDay);
        location.setTakenTime(this.takenTime);
        location.setCoordinate(this.coordinate);
        location.setPlace(this.place);
        location.setDescription(this.description);
        return location;
    }
}
