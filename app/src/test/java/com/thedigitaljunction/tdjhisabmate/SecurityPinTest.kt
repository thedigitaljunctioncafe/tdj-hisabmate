package com.thedigitaljunction.tdjhisabmate

import com.thedigitaljunction.tdjhisabmate.ui.util.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityPinTest {

    @Test
    fun `hashPin generates consistent SHA-256 hex string`() {
        val pin = "1234"
        val hash1 = SecurityUtils.hashPin(pin)
        val hash2 = SecurityUtils.hashPin(pin)

        assertEquals(hash1, hash2)
        assertEquals(64, hash1.length) // SHA-256 produces 64 hex characters
        assertNotEquals(pin, hash1)
    }

    @Test
    fun `verifyPin correctly checks entered PIN against hashed stored PIN`() {
        val pin = "5678"
        val storedHash = SecurityUtils.hashPin(pin)

        assertTrue(SecurityUtils.verifyPin("5678", storedHash))
        assertFalse(SecurityUtils.verifyPin("0000", storedHash))
        assertFalse(SecurityUtils.verifyPin("5679", storedHash))
    }

    @Test
    fun `verifyPin supports legacy plain-text fallback`() {
        val legacyPlainPin = "9876"
        assertTrue(SecurityUtils.verifyPin("9876", legacyPlainPin))
        assertFalse(SecurityUtils.verifyPin("1111", legacyPlainPin))
    }

    @Test
    fun `Legacy plain-text PIN migrates to SHA-256 hash after successful verification`() {
        val legacyStoredPin = "4321"
        assertTrue(SecurityUtils.isLegacyPlainTextPin(legacyStoredPin))

        var migratedHash: String? = null
        val verified = SecurityUtils.verifyAndMigratePin("4321", legacyStoredPin) { hash ->
            migratedHash = hash
        }

        assertTrue(verified)
        assertEquals(SecurityUtils.hashPin("4321"), migratedHash)
        assertFalse(SecurityUtils.isLegacyPlainTextPin(migratedHash!!))
    }

    @Test
    fun `New PIN is never stored as plain text`() {
        val userRawPin = "2468"
        val hashedToStore = SecurityUtils.hashPin(userRawPin)

        assertNotEquals(userRawPin, hashedToStore)
        assertEquals(64, hashedToStore.length)
        assertFalse(SecurityUtils.isLegacyPlainTextPin(hashedToStore))
    }

    @Test
    fun `Changing PIN requires correct current PIN verification`() {
        val originalPin = "1122"
        val storedHash = SecurityUtils.hashPin(originalPin)

        // User attempts change with wrong current PIN
        val wrongCurrentPin = "9999"
        val isCurrentPinValidWrong = SecurityUtils.verifyPin(wrongCurrentPin, storedHash)
        assertFalse(isCurrentPinValidWrong)

        // User attempts change with correct current PIN
        val isCurrentPinValidCorrect = SecurityUtils.verifyPin(originalPin, storedHash)
        assertTrue(isCurrentPinValidCorrect)

        // New PIN is hashed and replaces old PIN
        val newPin = "3344"
        val newHashed = SecurityUtils.hashPin(newPin)
        assertEquals(64, newHashed.length)
        assertNotEquals(storedHash, newHashed)
        assertTrue(SecurityUtils.verifyPin(newPin, newHashed))
        assertFalse(SecurityUtils.verifyPin(originalPin, newHashed))
    }

    @Test
    fun `Disabling PIN requires correct current PIN verification`() {
        val originalPin = "7788"
        val storedHash = SecurityUtils.hashPin(originalPin)

        // Disabling requires current PIN check
        assertTrue(SecurityUtils.verifyPin("7788", storedHash))
        assertFalse(SecurityUtils.verifyPin("1234", storedHash))
    }
}
