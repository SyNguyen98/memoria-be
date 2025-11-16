package org.chika.memoria.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chika.memoria.properties.ApplicationProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final ApplicationProperties applicationProperties;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Processes the incoming HTTP request to authenticate the user based on a JWT token.
     * <br>
     * This method extracts the JWT token from the request, decodes it to retrieve the user ID,
     * loads the user details, and sets the authentication in the security context. If an error
     * occurs during this process, it logs the error and continues the filter chain.
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        try {
            // Extract the JWT token from the request
            String jwt = getJwtFromRequest(request);

            // If the token is present and valid, authenticate the user
            if (StringUtils.hasText(jwt)) {
                // Decode the JWT to retrieve the user ID
                String userId = jwtDecoder().decode(jwt).getSubject();

                // Load user details using the user ID
                UserDetails userDetails = customUserDetailsService.loadUserById(userId);

                // Create an authentication object with the user details
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Set additional details for the authentication object
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication in the security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Log any errors that occur during authentication
            log.error("Could not set user authentication in security context", ex);
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

    private JwtDecoder jwtDecoder() {
        final RSAPublicKey publicJwtKey = applicationProperties.getSecurity().getPublicJwtKey();
        return NimbusJwtDecoder.withPublicKey(publicJwtKey).build();
    }

    private String getJwtFromRequest(final HttpServletRequest request) {
        final String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}