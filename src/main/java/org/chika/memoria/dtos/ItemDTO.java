package org.chika.memoria.dtos;

import lombok.Builder;
import lombok.Data;
import org.chika.memoria.client.models.Item;

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

    private String thumbnailUrl;

    public static ItemDTO convert(final Item item) {
        return ItemDTO.builder()
                .id(item.getId())
                .createdDateTime(item.getCreatedDateTime())
                .lastModifiedDateTime(item.getLastModifiedDateTime())
                .takenDateTime(item.getPhoto().getTakenDateTime())
                .name(item.getName())
                .mimeType(item.getFile().getMimeType())
                .downloadUrl(item.getDownloadUrl())
                .build();
    }

    public static ItemDTO convert(final Item item, final String thumbnailSize) {
        ItemDTOBuilder builder = ItemDTO.builder()
                .id(item.getId())
                .createdDateTime(item.getCreatedDateTime())
                .lastModifiedDateTime(item.getLastModifiedDateTime())
                .takenDateTime(item.getPhoto().getTakenDateTime())
                .name(item.getName())
                .mimeType(item.getFile().getMimeType())
                .downloadUrl(item.getDownloadUrl());
        if (!item.getThumbnails().isEmpty()) {
            switch (thumbnailSize) {
                case "large" -> builder.thumbnailUrl(item.getThumbnails().getFirst().getLarge().getUrl());
                case "medium" -> builder.thumbnailUrl(item.getThumbnails().getFirst().getMedium().getUrl());
                case "small" -> builder.thumbnailUrl(item.getThumbnails().getFirst().getSmall().getUrl());
                default -> builder.thumbnailUrl("");
            }
        }
        return builder.build();
    }
}
