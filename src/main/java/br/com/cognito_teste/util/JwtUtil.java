package br.com.cognito_teste.util;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@Component
public class JwtUtil {
    @Value("${aws.cognito.jwk}")
    private String jwk;

    public Map<String, Object> getValueFromJwt(String accessToken) throws Exception {
        JWKSet jwkSet = fetchJWKSet();
        JWSObject jwsObject = JWSObject.parse(accessToken);
        JWSHeader header = jwsObject.getHeader();
        JWK jwk = jwkSet.getKeyByKeyId(header.getKeyID());
        if (jwk != null) {
            JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) jwk.toRSAKey().toPublicKey());
            if (jwsObject.verify(verifier)) {
                Payload payload = jwsObject.getPayload();
                return payload.toJSONObject();
            }
        }
        return null;
    }

    private JWKSet fetchJWKSet() throws Exception {
        URL jwkSetURL = new URL(jwk);
        InputStream inputStream = jwkSetURL.openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder jwkSetString = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jwkSetString.append(line);
        }
        return JWKSet.parse(jwkSetString.toString());
    }
}
