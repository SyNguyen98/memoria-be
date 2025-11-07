package org.chika.memoria.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import lombok.extern.slf4j.Slf4j;
import org.chika.memoria.properties.ApplicationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@Slf4j
public class TokenProvider {

    private static final JwsHeader JWS_HEADER = JwsHeader.with(SignatureAlgorithm.RS256).type("JWT").build();

    private final ApplicationProperties applicationProperties;

    private final NimbusJwtEncoder encoder;

    public TokenProvider(final ApplicationProperties applicationProperties) {
        final RSAKey privateJwtKey = applicationProperties.getSecurity().getPrivateJwtKey();
        final JWKSet jwkSet = new JWKSet(privateJwtKey);
        this.encoder = new NimbusJwtEncoder(new ImmutableJWKSet<>(jwkSet));
        this.applicationProperties = applicationProperties;
    }

    public String createToken(final Authentication authentication) {
        final UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        final Instant expiration = new Date(new Date().getTime() + applicationProperties.getSecurity().getTokenExpiration()).toInstant();

        JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
                .subject(userPrincipal.getId())
                .issuedAt(Instant.now())
                .expiresAt(expiration);

        final JwtClaimsSet claimsSet = builder.build();
        final JwtEncoderParameters params = JwtEncoderParameters.from(JWS_HEADER, claimsSet);

        return encoder.encode(params).getTokenValue();
    }
}