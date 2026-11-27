package com.lloydsbyte.giftlist.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
// ── Christmas palette (green primary, red secondary, gold tertiary) ──────────

// Light theme
val ChristmasGreen = Color(0xFF1B6B3A)        // primary — Christmas tree green
val ChristmasGreenContainer = Color(0xFFA4F5B4)
val OnChristmasGreenContainer = Color(0xFF002111)

val ChristmasRed = Color(0xFFBE1F2E)         // secondary — Santa / berry red
val ChristmasRedContainer = Color(0xFFFFDAD6)
val OnChristmasRedContainer = Color(0xFF410006)

val ChristmasGold = Color(0xFF8E5B00)        // tertiary — bauble gold
val ChristmasGoldContainer = Color(0xFFFFDF8E)
val OnChristmasGoldContainer = Color(0xFF2A1900)

val SnowSurface = Color(0xFFFCFCF9)           // warm snow-white background/surface
val OnSnowSurface = Color(0xFF1A1C19)
val SnowSurfaceVariant = Color(0xFFE0E4DC)
val OnSnowSurfaceVariant = Color(0xFF424940)
val SnowOutline = Color(0xFF72796F)
val SnowError = Color(0xFFBA1A1A)
val SnowErrorContainer = Color(0xFFFFDAD6)
val OnSnowErrorContainer = Color(0xFF410002)

// Dark theme
val ChristmasGreenDark = Color(0xFF88D89A)
val ChristmasGreenContainerDark = Color(0xFF00522B)
val OnChristmasGreenContainerDark = Color(0xFFA4F5B4)

val ChristmasRedDark = Color(0xFFFFB4AB)
val ChristmasRedContainerDark = Color(0xFF93000A)
val OnChristmasRedContainerDark = Color(0xFFFFDAD6)

val ChristmasGoldDark = Color(0xFFFFC04D)
val ChristmasGoldContainerDark = Color(0xFF5B4300)
val OnChristmasGoldContainerDark = Color(0xFFFFDF8E)

val SnowSurfaceDark = Color(0xFF1A1C19)
val OnSnowSurfaceDark = Color(0xFFE2E3DD)
val SnowSurfaceVariantDark = Color(0xFF424940)
val OnSnowSurfaceVariantDark = Color(0xFFC2C8BE)
val SnowOutlineDark = Color(0xFF8C9388)
val SnowErrorDark = Color(0xFFFFB4AB)
val SnowErrorContainerDark = Color(0xFF93000A)
val OnSnowErrorContainerDark = Color(0xFFFFDAD6)

// Deeper greens for the lock-screen gradient (festive, calm).
val PineDeep = Color(0xFF0E3D22)
val PineDeepDark = Color(0xFF062114)

/** Chip color for the "Wrapped" toggle (green points + wraps it up). */
val WrappedGreen = ChristmasGreen
val WrappedGreenDark = ChristmasGreenDark

/** Vertical pine gradient for the lock screen background. */
fun pineGradient(dark: Boolean): Brush = Brush.verticalGradient(
    colors = listOf(
        if (dark) PineDeepDark else PineDeep,
        if (dark) Color(0xFF11321F) else Color(0xFF1B6B3A),
    )
)
