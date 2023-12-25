package org.chika.memoria.properties;

import com.nimbusds.jose.jwk.RSAKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "application")
@Configuration
@Getter
@Setter
public class ApplicationProperties {

    private Security security = new Security();
    private OAuth2 oauth2 = new OAuth2();
    private List<String> corsOrigins;

    @Getter
    @Setter
    public static class Security {
        private RSAKey privateJwtKey;
        private RSAPublicKey publicJwtKey;
        private long tokenExpiration;
    }

    @Getter
    @Setter
    public static class OAuth2 {
        private List<String> authorizedRedirectUris = new ArrayList<>();
    }
}
