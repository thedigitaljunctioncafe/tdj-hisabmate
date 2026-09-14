package com.thedigitaljunction.tdjhisabmate.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thedigitaljunction.tdjhisabmate.R
import com.thedigitaljunction.tdjhisabmate.ui.theme.EmeraldHero
import com.thedigitaljunction.tdjhisabmate.ui.theme.EmeraldPrimary

/**
 * Official TDJ HisabMate brand logo component.
 * Features the signature financial growth bars, ascending chevron wings,
 * and wealth diamond node reflecting the official brand identity.
 */
@Composable
fun TDJHisabMateLogo(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    showBadgeBackground: Boolean = true,
    badgeShape: RoundedCornerShape = RoundedCornerShape(12.dp),
    contentDescription: String = "TDJ HisabMate Logo"
) {
    if (showBadgeBackground) {
        Box(
            modifier = modifier
                .size(size)
                .clip(badgeShape)
                .background(
                    Brush.linearGradient(
                        listOf(EmeraldHero, EmeraldPrimary)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_tdj_logo),
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(size * 0.75f)
                    .padding(2.dp)
            )
        }
    } else {
        Image(
            painter = painterResource(id = R.drawable.ic_tdj_logo),
            contentDescription = contentDescription,
            modifier = modifier.size(size)
        )
    }
}

/**
 * Reusable full brand header for TDJ HisabMate with logo mark and typography hierarchy.
 */
@Composable
fun TDJBrandHeader(
    modifier: Modifier = Modifier,
    logoSize: Dp = 44.dp,
    showSubtitle: Boolean = true,
    titleColor: Color = MaterialTheme.colorScheme.onBackground,
    subtitleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TDJHisabMateLogo(
            size = logoSize,
            showBadgeBackground = true
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "TDJ HisabMate",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = titleColor
            )
            if (showSubtitle) {
                Text(
                    text = "Your Smart Personal Finance Mate",
                    style = MaterialTheme.typography.bodySmall,
                    color = subtitleColor
                )
            }
        }
    }
}
