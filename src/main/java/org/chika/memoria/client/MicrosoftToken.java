package org.chika.memoria.client;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.chika.memoria.dtos.AccessTokenDTO;
import org.chika.memoria.models.MsToken;
import org.chika.memoria.properties.MemoriaProperties;
import org.chika.memoria.repositories.MsTokenRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Getter
@Slf4j
public class MicrosoftToken {

    public static final String MICROSOFT_TOKEN_URL = "https://login.microsoftonline.com/consumers/oauth2/v2.0/token";
    private final MemoriaProperties memoriaProperties;
    private final MsTokenRepository msTokenRepository;
    private String accessToken;
    private String refreshToken;

    public MicrosoftToken(MemoriaProperties memoriaProperties, MsTokenRepository msTokenRepository) {
        this.memoriaProperties = memoriaProperties;
        this.msTokenRepository = msTokenRepository;
        this.accessToken = "";
        this.refreshToken = "";
    }

    @PostConstruct
    public void getToken() {
        final List<MsToken> list = msTokenRepository.findAll();
        if (list.isEmpty()) {
            this.accessToken = "";
            this.refreshToken = "";
        } else {
            final MsToken msToken = list.getFirst();
            this.accessToken = msToken.getAccessToken();
            this.refreshToken = msToken.getRefreshToken();
        }
    }

    public void getAccessAndRefreshToken(final MultiValueMap<String, String> bodyMap) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(bodyMap, headers);

        log.debug("[MicrosoftToken#getAccessAndRefreshToken] ---> POST {}", MICROSOFT_TOKEN_URL);
        ResponseEntity<AccessTokenDTO> response = new RestTemplate().exchange(MICROSOFT_TOKEN_URL, HttpMethod.POST, entity, AccessTokenDTO.class);
        log.debug("[MicrosoftToken#getAccessAndRefreshToken] <--- HTTP/1.1 {}", response.getStatusCode());

        if (response.getBody() != null) {
            this.accessToken = "Bearer " + response.getBody().getAccessToken();
            this.refreshToken = response.getBody().getRefreshToken();

            msTokenRepository.deleteAll();
            msTokenRepository.save(MsToken.builder()
                    .accessToken(this.accessToken)
                    .refreshToken(this.refreshToken)
                    .build());
        }
    }

    public void refreshAccessToken() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", memoriaProperties.getMicrosoft().getClientId());
        map.add("client_secret", memoriaProperties.getMicrosoft().getClientSecret());
        map.add("redirect_uri", memoriaProperties.getMicrosoft().getRedirectUri());
        map.add("refresh_token", this.refreshToken);
        map.add("grant_type", "refresh_token");

        getAccessAndRefreshToken(map);
    }
}
