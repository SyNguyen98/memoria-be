package org.chika.memoria.converters;

import org.chika.memoria.dtos.CollectionRecord;
import org.chika.memoria.models.Collection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CollectionConverter {

    Collection toEntity(CollectionRecord record);

    @Mapping(target = "ownerEmail", source = "ownerEmail")
    Collection toEntity(CollectionRecord record, String ownerEmail);

    CollectionRecord toRecord(Collection entity);
}
