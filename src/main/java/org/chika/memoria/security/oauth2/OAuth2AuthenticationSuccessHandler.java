package org.chika.memoria.security.oauth2;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chika.memoria.exceptions.BadRequestException;
import org.chika.memoria.properties.ApplicationProperties;
import org.chika.memoria.security.TokenProvider;
import org.chika.memoria.utils.CookieUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static org.chika.memoria.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_COOKIE;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final ApplicationProperties applicationProperties;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    /**
     * Handles the successful authentication process for OAuth2.
     *
     * @param request        the HTTP request object
     * @param response       the HTTP response object
     * @param authentication the authentication object containing user details
     * @throws IOException if an input or output exception occurs during redirection
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // Determine the target URL to redirect the user after successful authentication
        String targetUrl = determineTargetUrl(request, response, authentication);

        // Check if the response has already been committed to avoid further processing
        if (response.isCommitted()) {
            log.debug("Response already committed. Unable to redirect to {}", targetUrl);
            return;
        }

        // Clear any authentication-related attributes from the request and response
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        // Redirect the user to the determined target URL
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * Determines the target URL to redirect the user after successful authentication.
     * <p>
     * This method retrieves the redirect URI from a cookie, validates it against the list of
     * authorized redirect URIs, and appends a generated authentication token as a query parameter
     * to the target URL.
     */
    @Override
    public String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                     Authentication authentication) {
        // Retrieve the redirect URI from the cookie, if present
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_COOKIE).map(Cookie::getValue);

        // Validate the redirect URI against the list of authorized URIs
        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException("Unauthorized Redirect URI. Cannot proceed with authentication.");
        }

        // Use the redirect URI if present, otherwise use the default target URL
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        // Generate an authentication token for the user
        String token = tokenProvider.createToken(authentication);

        // Append the token as a query parameter to the target URL and return it
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return applicationProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedUri -> {
                    URI authorizedURI = URI.create(authorizedUri);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}
