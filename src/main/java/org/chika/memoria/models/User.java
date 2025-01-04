package org.chika.memoria.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.chika.memoria.constants.AuthProvider;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("users")
@Data
@Builder
public class User {

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("email")
    @Indexed(unique = true)
    @Email
    private String email;

    @Field("avatarUrl")
    private String avatarUrl;

    @Field("provider")
    @NotNull
    private AuthProvider provider;

    @Field("providerId")
    private String providerId;
}
