package org.chika.memoria.properties.converters;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class GrantedAuthorityConverter implements Converter<String, GrantedAuthority> {

    @Override
    public GrantedAuthority convert(String source) {
        return new SimpleGrantedAuthority(source);
    }
}
