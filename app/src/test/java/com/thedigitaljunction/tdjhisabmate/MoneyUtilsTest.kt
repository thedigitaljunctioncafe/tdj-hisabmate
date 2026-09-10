package com.thedigitaljunction.tdjhisabmate

import com.thedigitaljunction.tdjhisabmate.ui.util.MoneyUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MoneyUtilsTest {

    @Test
    fun `rupeesToPaise accurately converts decimal rupees to integer paise`() {
        assertEquals(10050L, MoneyUtils.rupeesToPaise(100.50))
        assertEquals(0L, MoneyUtils.rupeesToPaise(0.0))
        assertEquals(1L, MoneyUtils.rupeesToPaise(0.01))
        assertEquals(99L, MoneyUtils.rupeesToPaise(0.99))
        assertEquals(123456789L, MoneyUtils.rupeesToPaise(1234567.89))
    }

    @Test
    fun `paiseToRupees accurately converts integer paise to double rupees`() {
        assertEquals(100.50, MoneyUtils.paiseToRupees(10050L), 0.001)
        assertEquals(0.0, MoneyUtils.paiseToRupees(0L), 0.001)
        assertEquals(0.01, MoneyUtils.paiseToRupees(1L), 0.001)
        assertEquals(1500.0, MoneyUtils.paiseToRupees(150000L), 0.001)
    }

    @Test
    fun `parseMoneyInput parses valid currency strings into paise`() {
        assertEquals(15000L, MoneyUtils.parseMoneyInput("150"))
        assertEquals(15050L, MoneyUtils.parseMoneyInput("150.50"))
        assertEquals(15005L, MoneyUtils.parseMoneyInput("150.05"))
        assertEquals(250000L, MoneyUtils.parseMoneyInput("₹2,500.00"))
        assertEquals(10000000L, MoneyUtils.parseMoneyInput("1,00,000"))
        assertEquals(-5000L, MoneyUtils.parseMoneyInput("-50"))
    }

    @Test
    fun `parseMoneyInput returns null on invalid strings`() {
        assertNull(MoneyUtils.parseMoneyInput(""))
        assertNull(MoneyUtils.parseMoneyInput("   "))
        assertNull(MoneyUtils.parseMoneyInput("abc"))
        assertNull(MoneyUtils.parseMoneyInput("12.34.56"))
    }

    @Test
    fun `formatPaise outputs expected currency formats`() {
        assertEquals("₹150.00", MoneyUtils.formatPaise(15000L, "₹", includeSymbol = true, showDecimalsIfZero = true))
        assertEquals("₹150", MoneyUtils.formatPaise(15000L, "₹", includeSymbol = true, showDecimalsIfZero = false))
        assertEquals("$1,250.75", MoneyUtils.formatPaise(125075L, "$", includeSymbol = true, showDecimalsIfZero = true))
        assertEquals("-₹50.00", MoneyUtils.formatPaise(-5000L, "₹", includeSymbol = true, showDecimalsIfZero = true))
    }

    @Test
    fun `calculatePercentage prevents division by zero`() {
        assertEquals(0.5f, MoneyUtils.calculatePercentage(5000L, 10000L), 0.001f)
        assertEquals(1.0f, MoneyUtils.calculatePercentage(10000L, 10000L), 0.001f)
        assertEquals(1.5f, MoneyUtils.calculatePercentage(15000L, 10000L), 0.001f)
        assertEquals(0.0f, MoneyUtils.calculatePercentage(0L, 0L), 0.001f)
        assertEquals(1.0f, MoneyUtils.calculatePercentage(5000L, 0L), 0.001f)
    }

    @Test
    fun `safeRemaining accurately calculates remaining budget`() {
        assertEquals(5000L, MoneyUtils.safeRemaining(10000L, 5000L))
        assertEquals(0L, MoneyUtils.safeRemaining(10000L, 12000L))
    }
}
