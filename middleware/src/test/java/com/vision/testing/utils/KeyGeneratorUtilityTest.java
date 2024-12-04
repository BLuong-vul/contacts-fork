package com.vision.testing.utils;


import com.vision.middleware.utils.KeyGeneratorUtility;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class KeyGeneratorUtilityTest {
    @Test
    void testGenerateRSAKey() {
        KeyPair keyPair = KeyGeneratorUtility.generateRsaKey();

        assertThat(keyPair).isNotNull();
        assertThat(keyPair.getPrivate()).isNotNull();
        assertThat(keyPair.getPublic()).isNotNull();

        assertThat(keyPair.getPrivate().getAlgorithm()).isEqualTo("RSA");
        assertThat(keyPair.getPublic().getAlgorithm()).isEqualTo("RSA");
    }

    @Test
    public void testGenerateRsaKey_Success() {
        // Arrange
        KeyPairGenerator keyPairGenerator = mock(KeyPairGenerator.class);
        KeyPair keyPair = mock(KeyPair.class);

        try (MockedStatic<KeyPairGenerator> mocked = mockStatic(KeyPairGenerator.class)) {
            mocked.when(() -> KeyPairGenerator.getInstance("RSA")).thenReturn(keyPairGenerator);
            when(keyPairGenerator.generateKeyPair()).thenReturn(keyPair);

            // Act
            KeyPair result = KeyGeneratorUtility.generateRsaKey();

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(keyPair);
        }
    }

    @Test
    public void testGenerateRsaKey_ThrowsRuntimeExceptionOnException() {
        // Arrange
        KeyPairGenerator keyPairGenerator = mock(KeyPairGenerator.class);

        try (MockedStatic<KeyPairGenerator> mocked = mockStatic(KeyPairGenerator.class)) {
            mocked.when(() -> KeyPairGenerator.getInstance("RSA")).thenThrow(new NoSuchAlgorithmException("Mock exception"));

            // Act & Assert
            assertThatThrownBy(KeyGeneratorUtility::generateRsaKey)
                    .isInstanceOf(RuntimeException.class)
                    .hasCauseInstanceOf(NoSuchAlgorithmException.class)
                    .hasMessageContaining("Mock exception");
        }
    }

//    @Test
//    void testGenerateRsaKey_Exception() {
//        KeyPairGenerator mockKPG = mock(KeyPairGenerator.class);
//        when(mockKPG.generateKeyPair()).thenThrow(new RuntimeException("mocked exception"));
//
//        assertThatThrownBy(() -> {
//            KeyPair keyPair = KeyGeneratorUtility.generateRsaKey();
//        }).isInstanceOf(RuntimeException.class).hasMessage("mocked exception");
//    }
}
