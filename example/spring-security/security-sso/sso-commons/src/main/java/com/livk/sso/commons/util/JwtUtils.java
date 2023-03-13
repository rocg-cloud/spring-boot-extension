package com.livk.sso.commons.util;

import com.livk.commons.jackson.JacksonUtils;
import com.livk.sso.commons.entity.Payload;
import com.livk.sso.commons.entity.User;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.UUID;

/**
 * <p>
 * JwtUtils
 * </p>
 *
 * @author livk
 */
@UtilityClass
public class JwtUtils {

    @SneakyThrows
    public String generateToken(User userInfo, RSAKey key) {
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build();
        Payload payload = new Payload(UUID.randomUUID().toString(), userInfo);
        JWSObject jwsObject = new JWSObject(jwsHeader, new com.nimbusds.jose.Payload(JacksonUtils.writeValueAsString(payload)));
        RSASSASigner signer = new RSASSASigner(key);
        jwsObject.sign(signer);
        return jwsObject.serialize();
    }


    @SneakyThrows
    public Payload parse(String token, RSAKey key) {
        JWSObject jwsObject = JWSObject.parse(token);
        RSASSAVerifier verifier = new RSASSAVerifier(key);
        if (jwsObject.verify(verifier)) {
            String json = jwsObject.getPayload().toString();
            return JacksonUtils.readValue(json, Payload.class);
        }
        throw new RuntimeException("500");
    }
}