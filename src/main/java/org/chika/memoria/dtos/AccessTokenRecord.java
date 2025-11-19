package org.chika.memoria.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccessTokenRecord(
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") String expiresIn,
        @JsonProperty("scope") String scope,
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken
) {
}