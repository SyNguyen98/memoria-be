package org.chika.memoria.security.oauth2;

import lombok.RequiredArgsConstructor;
import org.chika.memoria.constants.AuthProvider;
import org.chika.memoria.exceptions.OAuth2AuthenticationProcessingException;
import org.chika.memoria.models.User;
import org.chika.memoria.repositories.UserRepository;
import org.chika.memoria.security.UserPrincipal;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
            GoogleOAuth2UserInfo oAuth2UserInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
            String email = oAuth2UserInfo.getEmail();

            if (email.isEmpty()) {
                throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
            }

            User user = userRepository.findByEmail(email)
                    .map(existingUser -> validateAndUpdateUser(existingUser, oAuth2UserRequest, oAuth2UserInfo))
                    .orElseGet(() -> registerNewUser(oAuth2UserRequest, oAuth2UserInfo));

            return UserPrincipal.create(user, oAuth2User.getAttributes());
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private User validateAndUpdateUser(User user, OAuth2UserRequest oAuth2UserRequest, GoogleOAuth2UserInfo userInfo) {
        String provider = oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase();
        if (!user.getProvider().equals(AuthProvider.valueOf(provider))) {
            throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " + user.getProvider() + " account. "
                    + "Please use your " + user.getProvider() + " account to login.");
        }
        return updateExistingUser(user, userInfo);
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, GoogleOAuth2UserInfo userInfo) {
        return userRepository.save(User.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .avatarUrl(userInfo.getImageUrl())
                .providerId(userInfo.getId())
                .provider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase()))
                .build());
    }

    private User updateExistingUser(User user, GoogleOAuth2UserInfo userInfo) {
        user.setName(userInfo.getName());
        user.setAvatarUrl(userInfo.getImageUrl());
        return userRepository.save(user);
    }
}