package org.chika.memoria.converters;

import org.chika.memoria.client.models.Item;
import org.chika.memoria.dtos.ItemRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemConverter {

    @Mapping(target = "takenDateTime", source = "entity.photo.takenDateTime")
    @Mapping(target = "mimeType", source = "entity.file.mimeType")
    ItemRecord toRecord(Item entity);

    @Mapping(target = "takenDateTime", source = "entity.photo.takenDateTime")
    @Mapping(target = "mimeType", source = "entity.file.mimeType")
    @Mapping(target = "thumbnailUrl", expression = "java(mapThumbnail(entity.getThumbnails(), thumbnailSize))")
    ItemRecord toRecord(Item entity, String thumbnailSize);

    default String mapThumbnail(List<Item.Thumbnail> thumbnails, String thumbnailSize) {
        if (thumbnails.isEmpty()) {
            return "";
        }
        return switch (thumbnailSize) {
            case "large" -> thumbnails.getFirst().getLarge().getUrl();
            case "medium" -> thumbnails.getFirst().getMedium().getUrl();
            case "small" -> thumbnails.getFirst().getSmall().getUrl();
            default -> "";
        };
    }
}
