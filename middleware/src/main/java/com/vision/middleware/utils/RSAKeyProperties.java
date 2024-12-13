package com.vision.middleware.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * This component is responsible for generating and storing RSA key pairs.
 */
@Getter
@Component
public class RSAKeyProperties {

    /**
     * The RSA public key.
     */
    private final RSAPublicKey publicKey;

    /**
     * The RSA private key.
     */
    private final RSAPrivateKey privateKey;

    /**
     * Constructor that generates a new RSA key pair and initializes the public and private keys.
     */
    public RSAKeyProperties() {
        KeyPair pair = KeyGeneratorUtility.generateRsaKey();
        this.publicKey = (RSAPublicKey) pair.getPublic();
        this.privateKey = (RSAPrivateKey) pair.getPrivate();
    }

}
