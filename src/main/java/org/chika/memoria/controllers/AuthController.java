package org.chika.memoria.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chika.memoria.client.MicrosoftToken;
import org.chika.memoria.properties.MemoriaProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/memoria")
@AllArgsConstructor
@Slf4j
@Hidden
public class AuthController {

    private final MemoriaProperties memoriaProperties;
    private final MicrosoftToken microsoftToken;

    @GetMapping("/microsoft/oauth2")
    public void getAuthorizeCode(@RequestParam final String code) {
        log.debug("GET request to get Microsoft Graph access token");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", memoriaProperties.getMicrosoft().getClientId());
        map.add("client_secret", memoriaProperties.getMicrosoft().getClientSecret());
        map.add("redirect_uri", memoriaProperties.getMicrosoft().getRedirectUri());
        map.add("code", code);
        map.add("grant_type", "authorization_code");

        microsoftToken.getAccessAndRefreshToken(map);
    }

    @GetMapping("/microsoft/token")
    public ResponseEntity<?> getMicrosoftTokenAvailable() {
        log.debug("GET request to know if Microsoft Graph access token is available");
        if (microsoftToken.getAccessToken().isEmpty() || microsoftToken.getAccessToken().isBlank()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Token is available");
    }
}
