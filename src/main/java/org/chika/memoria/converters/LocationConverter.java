package org.chika.memoria.converters;

import org.chika.memoria.dtos.LocationRecord;
import org.chika.memoria.models.Location;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationConverter {

    Location toEntity(LocationRecord record);

    LocationRecord toRecord(Location entity);
}
