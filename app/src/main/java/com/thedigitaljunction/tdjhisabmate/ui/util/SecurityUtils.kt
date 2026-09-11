package com.thedigitaljunction.tdjhisabmate.ui.util

import java.security.MessageDigest

/**
 * Utility for securing 4-digit PIN lock using SHA-256 hashing.
 * Storing only hashes guarantees privacy and prevents plain-text PIN exposure.
 */
object SecurityUtils {

    /**
     * Hashes the given PIN with SHA-256.
     */
    fun hashPin(pin: String): String {
        if (pin.isEmpty()) return ""
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(pin.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    /**
     * Verifies if the entered PIN matches the stored hash (or legacy plain text).
     */
    fun verifyPin(enteredPin: String, storedPinOrHash: String): Boolean {
        if (storedPinOrHash.isEmpty()) return true
        val hashed = hashPin(enteredPin)
        return hashed.equals(storedPinOrHash, ignoreCase = true) || enteredPin == storedPinOrHash
    }
}
