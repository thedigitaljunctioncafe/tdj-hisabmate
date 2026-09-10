package com.thedigitaljunction.tdjhisabmate.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

object Formatters {

    fun formatMoney(amount: Double, currency: String = "₹", compact: Boolean = false): String {
        val paise = MoneyUtils.rupeesToPaise(amount)
        return MoneyUtils.formatPaise(paise, currencySymbol = currency, showDecimalsIfZero = !compact)
    }

    fun formatMoney(paise: Long, currency: String = "₹", compact: Boolean = false): String {
        return MoneyUtils.formatPaise(paise, currencySymbol = currency, showDecimalsIfZero = !compact)
    }

    fun formatDate(timestamp: Long): String {
        return DateUtils.formatDate(timestamp)
    }

    fun formatTime(timestamp: Long): String {
        return DateUtils.formatTime(timestamp)
    }

    fun formatRelativeDate(timestamp: Long): String {
        return DateUtils.formatRelativeDay(timestamp)
    }

    fun getCategoryIcon(iconName: String): ImageVector {
        return when (iconName.lowercase()) {
            "restaurant", "food" -> Icons.Default.Restaurant
            "shopping_cart", "groceries" -> Icons.Default.ShoppingCart
            "directions_car", "transport", "fuel" -> Icons.Default.DirectionsCar
            "shopping_bag", "shopping" -> Icons.Default.ShoppingBag
            "receipt_long", "bills", "utilities" -> Icons.Default.ReceiptLong
            "home", "rent" -> Icons.Default.Home
            "account_balance", "emi", "bank" -> Icons.Default.AccountBalance
            "local_hospital", "health", "medical" -> Icons.Default.LocalHospital
            "school", "education" -> Icons.Default.School
            "movie", "entertainment" -> Icons.Default.Movie
            "flight", "travel" -> Icons.Default.Flight
            "person", "personal" -> Icons.Default.Person
            "group", "family" -> Icons.Default.Group
            "work", "business" -> Icons.Default.Work
            "payments", "salary" -> Icons.Default.Payments
            "trending_up", "investment" -> Icons.Default.TrendingUp
            "card_giftcard", "gift" -> Icons.Default.CardGiftcard
            "credit_card" -> Icons.Default.CreditCard
            "attach_money" -> Icons.Default.AttachMoney
            else -> Icons.Default.Category
        }
    }
}

