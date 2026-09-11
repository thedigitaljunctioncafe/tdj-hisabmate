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
}
