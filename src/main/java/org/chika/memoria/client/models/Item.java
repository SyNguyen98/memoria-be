package org.chika.memoria.client.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class Item {

    private String id;

    private Instant createdDateTime;

    private Instant lastModifiedDateTime;

    private String name;

    private File file;

    private Photo photo;

    @JsonProperty("@microsoft.graph.downloadUrl")
    private String downloadUrl;

    @Data
    public static class File {

        private String mimeType;
    }

    @Data
    public static class Photo {

        private String cameraMake;

        private String cameraModel;

        private Instant takenDateTime;
    }
}