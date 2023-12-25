package org.chika.memoria.properties.converters;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class RsaPrivateKeyConverter implements Converter<String, RSAKey> {

    @SneakyThrows
    @Override
    public RSAKey convert(String source) {
        return JWK.parseFromPEMEncodedObjects(source).toRSAKey();
    }
}
