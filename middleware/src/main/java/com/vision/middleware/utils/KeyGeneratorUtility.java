package com.vision.middleware.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * Utility class for generating RSA keys for encoding/decoding JWT tokens.
 */
public class KeyGeneratorUtility {

    /**
     * Generates a new RSA key pair.
     *
     * @return a newly generated RSA key pair
     * @throws RuntimeException if an error occurs during key pair generation
     */
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
