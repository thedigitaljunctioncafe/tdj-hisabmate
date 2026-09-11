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
     * Checks if the stored PIN representation is an unhashed legacy plain text PIN.
     * Valid SHA-256 hashes are 64 hexadecimal characters.
     */
    fun isLegacyPlainTextPin(storedPinOrHash: String): Boolean {
        if (storedPinOrHash.isEmpty()) return false
        return storedPinOrHash.length != 64 || !storedPinOrHash.all { it in "0123456789abcdefABCDEF" }
    }

    /**
     * Verifies if the entered PIN matches the stored hash (or legacy plain text).
     */
    fun verifyPin(enteredPin: String, storedPinOrHash: String): Boolean {
        if (storedPinOrHash.isEmpty()) return true
        val hashed = hashPin(enteredPin)
        return hashed.equals(storedPinOrHash, ignoreCase = true) || enteredPin == storedPinOrHash
    }

    /**
     * Verifies the entered PIN and, if it matches a legacy unhashed PIN, triggers the migration callback
     * with the newly generated SHA-256 hash.
     */
    fun verifyAndMigratePin(enteredPin: String, storedPinOrHash: String, onMigrate: (String) -> Unit): Boolean {
        val isValid = verifyPin(enteredPin, storedPinOrHash)
        if (isValid && isLegacyPlainTextPin(storedPinOrHash)) {
            onMigrate(hashPin(enteredPin))
        }
        return isValid
    }
}
