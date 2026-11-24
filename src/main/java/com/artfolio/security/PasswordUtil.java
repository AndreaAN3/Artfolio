
package com.artfolio.security;

import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.GeneralSecurityException;

import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {

    private static final String ALGORITHM =
            "PBKDF2WithHmacSHA256";

    private static final int ITERATIONS = 600000;

    private static final int SALT_LENGTH = 16;

    private static final int KEY_LENGTH = 256;

    private static final SecureRandom RANDOM =
            new SecureRandom();

    private PasswordUtil() {
    }

    public static String hashPassword(String password) {

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "La contraseña no puede estar vacía"
            );
        }

        byte[] salt = new byte[SALT_LENGTH];

        RANDOM.nextBytes(salt);

        byte[] hash = deriveKey(
                password,
                salt,
                ITERATIONS
        );

        return "pbkdf2_sha256"
                + "$" + ITERATIONS
                + "$" + Base64.getEncoder()
                        .encodeToString(salt)
                + "$" + Base64.getEncoder()
                        .encodeToString(hash);
    }

    public static boolean verifyPassword(
            String password,
            String storedHash) {

        if (password == null || storedHash == null) {
            return false;
        }

        try {

            String[] parts = storedHash.split("\\$");

            if (parts.length != 4) {
                return false;
            }

            if (!"pbkdf2_sha256".equals(parts[0])) {
                return false;
            }

            int iterations =
                    Integer.parseInt(parts[1]);

            if (iterations < 100000
                    || iterations > 1000000) {
                return false;
            }

            byte[] salt =
                    Base64.getDecoder().decode(parts[2]);

            byte[] expectedHash =
                    Base64.getDecoder().decode(parts[3]);

            if (salt.length != SALT_LENGTH
                    || expectedHash.length != KEY_LENGTH / 8) {
                return false;
            }

            byte[] actualHash = deriveKey(
                    password,
                    salt,
                    iterations
            );

            return MessageDigest.isEqual(
                    expectedHash,
                    actualHash
            );

        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static byte[] deriveKey(
            String password,
            byte[] salt,
            int iterations) {

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                iterations,
                KEY_LENGTH
        );

        try {

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(ALGORITHM);

            return factory.generateSecret(spec).getEncoded();

        } catch (GeneralSecurityException ex) {

            throw new IllegalStateException(
                    "No se pudo procesar la contraseña",
                    ex
            );

        } finally {
            spec.clearPassword();
        }
    }
}

