package com.vision.middleware.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

// utility class for generating RSA keys for encoding / decoding JWT tokens.

public class KeyGeneratorUtility {

    public static KeyPair generateRsaKey() {

        KeyPair keyPair;

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return keyPair;
    }

}
