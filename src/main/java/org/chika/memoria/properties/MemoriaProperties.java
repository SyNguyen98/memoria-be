package org.chika.memoria.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "memoria")
@Configuration
@Getter
public class MemoriaProperties {

    private final Microsoft microsoft = new Microsoft();

    @Getter
    @Setter
    public static class Microsoft {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
    }
}
