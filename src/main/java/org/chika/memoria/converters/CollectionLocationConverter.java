package org.chika.memoria.converters;

import org.chika.memoria.dtos.CollectionLocationRecord;
import org.chika.memoria.models.CollectionLocation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CollectionLocationConverter {

    CollectionLocationRecord toRecord(CollectionLocation entity);

    CollectionLocation toEntity(CollectionLocationRecord record);
}
