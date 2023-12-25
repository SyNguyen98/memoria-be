package org.chika.memoria.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("msToken")
@Data
@Builder
public class MsToken {

    @Id
    private String id;

    @Field("accessToken")
    private String accessToken;

    @Field("refreshToken")
    private String refreshToken;
}
