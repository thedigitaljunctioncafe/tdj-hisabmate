package com.thedigitaljunction.tdjhisabmate

import com.thedigitaljunction.tdjhisabmate.update.SemanticVersion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SemanticVersionTest {

    @Test
    fun `equal versions evaluate as equal`() {
        val v1 = SemanticVersion.parseOrNull("1.0.0")
        val v2 = SemanticVersion.parseOrNull("1.0.0")
        assertNotNull(v1)
        assertNotNull(v2)
        assertEquals(0, v1!!.compareTo(v2!!))
        assertEquals(v1, v2)
    }

    @Test
    fun `patch version increments are newer`() {
        val v1 = SemanticVersion.parseOrNull("1.0.0")!!
        val v2 = SemanticVersion.parseOrNull("1.0.1")!!
        assertTrue(v2.isNewerThan(v1))
        assertTrue(v1.isOlderThan(v2))
        assertEquals(-1, v1.compareTo(v2))
    }

    @Test
    fun `minor version increments are newer`() {
        val v1 = SemanticVersion.parseOrNull("1.0.9")!!
        val v2 = SemanticVersion.parseOrNull("1.1.0")!!
        assertTrue(v2.isNewerThan(v1))
        assertEquals(1, v2.compareTo(v1))
    }

    @Test
    fun `numeric comparison correctly handles two digit numbers`() {
        // 1.0.10 must be recognized as newer than 1.0.9 (NOT lexicographical string comparison)
        val v9 = SemanticVersion.parseOrNull("1.0.9")!!
        val v10 = SemanticVersion.parseOrNull("1.0.10")!!
        assertTrue(v10.isNewerThan(v9))
        assertEquals(1, v10.compareTo(v9))
    }

    @Test
    fun `major version increments are newer`() {
        val v1 = SemanticVersion.parseOrNull("1.9.9")!!
        val v2 = SemanticVersion.parseOrNull("2.0.0")!!
        assertTrue(v2.isNewerThan(v1))
        assertEquals(1, v2.compareTo(v1))
    }

    @Test
    fun `handles leading v and V prefix and whitespace gracefully`() {
        val vLower = SemanticVersion.parseOrNull("  v1.0.0  ")
        val vUpper = SemanticVersion.parseOrNull("V1.0.0")
        val plain = SemanticVersion.parseOrNull("1.0.0")

        assertNotNull(vLower)
        assertNotNull(vUpper)
        assertNotNull(plain)
        assertEquals(0, vLower!!.compareTo(plain!!))
        assertEquals(0, vUpper!!.compareTo(plain))
    }

    @Test
    fun `handles two part versions such as 1 0 gracefully`() {
        val vShort = SemanticVersion.parseOrNull("1.0")
        assertNotNull(vShort)
        assertEquals(1, vShort!!.major)
        assertEquals(0, vShort.minor)
        assertEquals(0, vShort.patch)
    }

    @Test
    fun `pre-release versions evaluate as older than stable release`() {
        val stable = SemanticVersion.parseOrNull("1.0.0")!!
        val beta = SemanticVersion.parseOrNull("1.0.0-beta1")!!
        assertTrue(stable.isNewerThan(beta))
    }

    @Test
    fun `malformed versions return null safely without throwing exceptions`() {
        assertNull(SemanticVersion.parseOrNull(null))
        assertNull(SemanticVersion.parseOrNull(""))
        assertNull(SemanticVersion.parseOrNull("   "))
        assertNull(SemanticVersion.parseOrNull("abc"))
        assertNull(SemanticVersion.parseOrNull("release-1"))
    }
}
