
package com.artfolio.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    public void testPasswordSecurity() {

        String password = "ArtfolioTest2026!";

        String hash = PasswordUtil.hashPassword(password);

        assertNotNull(hash);

        assertNotEquals(password, hash);

        assertTrue(
            PasswordUtil.verifyPassword(password, hash)
        );

        assertFalse(
            PasswordUtil.verifyPassword("Incorrecta123!", hash)
        );

        System.out.println(
            "ARTFOLIO: Prueba de seguridad exitosa."
        );
    }
}
