package com.example.mobiledisco.ui.theme

import androidx.compose.ui.graphics.Color

object HiFiColors {

    // Fundo principal (madeira escura)
    val Walnut900 = Color(0xFF1E1410)
    val Walnut800 = Color(0xFF2B1D16)
    val Walnut700 = Color(0xFF3A281E)

    // Painéis
    val Espresso = Color(0xFF241814)
    val DarkPanel = Color(0xFF2D1F19)

    // Metal
    val BrushedMetal = Color(0xFF544A43)

    // Destaques
    val Copper = Color(0xFFD07A2D)
    val CopperDark = Color(0xFF9A541E)
    val CopperLight = Color(0xFFE7A15C)

    // Textos
    val Ivory = Color(0xFFE8D7C3)
    val Sand = Color(0xFFC5A989)
    val SoftBrown = Color(0xFF8E6E56)

    // Separadores
    val Divider = Color(0xFF4A352B)

    // Estados
    val LedOn = Color(0xFF48D05F)
    val LedOff = Color(0xFF2B332B)

    // --- Aliases Legados para manter compilação durante a transição ---
    // Estes serão removidos nas próximas etapas da Sprint
    val WoodBrown = Walnut700
    val PanelGray = Espresso
    val WarmBackground = Walnut900
    val LedGreen = LedOn
    val LedAmber = Copper
}
