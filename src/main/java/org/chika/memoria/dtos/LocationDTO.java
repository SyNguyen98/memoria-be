package org.chika.memoria.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.chika.memoria.models.Coordinate;
import org.chika.memoria.models.Location;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationDTO {
    private String id;
    private Integer takenYear;
    private Integer takenMonth;
    private Integer takenDay;
    private String takenTime;
    private Coordinate coordinate;
    private String place;
    private String description;
    private String driveItemId;

    public LocationDTO(final Location location) {
        this.id = location.getId();
        this.takenYear = location.getTakenYear();
        this.takenMonth = location.getTakenMonth();
        this.takenDay = location.getTakenDay();
        this.takenTime = location.getTakenTime();
        this.coordinate = location.getCoordinate();
        this.place = location.getPlace();
        this.description = location.getDescription();
        this.driveItemId = location.getDriveItemId();
    }
}
