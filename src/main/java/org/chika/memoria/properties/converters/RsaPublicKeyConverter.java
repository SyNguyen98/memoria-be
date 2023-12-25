package org.chika.memoria.properties.converters;

import com.nimbusds.jose.jwk.JWK;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPublicKey;

@Component
@ConfigurationPropertiesBinding
public class RsaPublicKeyConverter implements Converter<String, RSAPublicKey> {

    @SneakyThrows
    @Override
    public RSAPublicKey convert(String source) {
        return JWK.parseFromPEMEncodedObjects(source).toRSAKey().toRSAPublicKey();
    }
}
