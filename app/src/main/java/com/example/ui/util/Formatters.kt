package com.example.ui.util

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
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Formatters {

    private val numberFormat = DecimalFormat("#,##0.00")
    private val compactFormat = DecimalFormat("#,##0")
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    fun formatMoney(amount: Double, currency: String = "₹", compact: Boolean = false): String {
        val formattedNumber = if (compact && amount % 1.0 == 0.0) {
            compactFormat.format(amount)
        } else {
            numberFormat.format(amount)
        }
        return "$currency $formattedNumber"
    }

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    fun formatRelativeDate(timestamp: Long): String {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = timestamp }

        val isSameYear = now.get(Calendar.YEAR) == target.get(Calendar.YEAR)
        val dayDiff = now.get(Calendar.DAY_OF_YEAR) - target.get(Calendar.DAY_OF_YEAR)

        return when {
            isSameYear && dayDiff == 0 -> "Today"
            isSameYear && dayDiff == 1 -> "Yesterday"
            else -> dateFormat.format(Date(timestamp))
        }
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
