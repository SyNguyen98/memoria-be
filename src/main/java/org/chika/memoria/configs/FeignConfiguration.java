package org.chika.memoria.configs;

import feign.Logger;
import feign.RequestInterceptor;
import org.chika.memoria.client.MicrosoftToken;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;

@Configuration
@EnableFeignClients(basePackages = "org.chika.memoria")
@Import(FeignClientsConfiguration.class)
public class FeignConfiguration {

    private final MicrosoftToken microsoftToken;

    public FeignConfiguration(MicrosoftToken microsoftToken) {
        this.microsoftToken = microsoftToken;
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> requestTemplate.header(HttpHeaders.AUTHORIZATION, microsoftToken.getAccessToken());
    }
}
