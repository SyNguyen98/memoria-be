package org.chika.memoria.security.oauth2;

import java.util.Map;

public record GoogleOAuth2UserInfo(Map<String, Object> attributes) {

    public String getId() {
        return (String) attributes.get("sub");
    }

    public String getName() {
        return (String) attributes.get("name");
    }

    public String getEmail() {
        return (String) attributes.get("email");
    }

    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
}
