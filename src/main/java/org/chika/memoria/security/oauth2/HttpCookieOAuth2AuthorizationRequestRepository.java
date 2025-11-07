package org.chika.memoria.security.oauth2;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.chika.memoria.utils.CookieUtils;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Component
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String AUTH_REQUEST_COOKIE = "oauth2_auth_request";
    public static final String REDIRECT_URI_COOKIE = "redirect_uri";
    private static final int COOKIE_EXPIRATION = 180;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CookieUtils.getCookie(request, AUTH_REQUEST_COOKIE)
                .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            deleteCookies(request, response);
            return;
        }

        CookieUtils.addCookie(response, AUTH_REQUEST_COOKIE, CookieUtils.serialize(authorizationRequest), COOKIE_EXPIRATION);
        String redirectUri = request.getParameter(REDIRECT_URI_COOKIE);
        if (StringUtils.isNotBlank(redirectUri)) {
            CookieUtils.addCookie(response, REDIRECT_URI_COOKIE, redirectUri, COOKIE_EXPIRATION);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return loadAuthorizationRequest(request);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        deleteCookies(request, response);
    }

    private void deleteCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, AUTH_REQUEST_COOKIE);
        CookieUtils.deleteCookie(request, response, REDIRECT_URI_COOKIE);
    }
}
