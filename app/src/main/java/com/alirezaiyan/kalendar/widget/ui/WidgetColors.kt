package com.alirezaiyan.kalendar.widget.ui

import androidx.compose.ui.graphics.Color
import androidx.glance.color.ColorProvider

/**
 * Premium modern color palette for the calendar widget.
 * Features sophisticated gradients, proper contrast, and premium aesthetics.
 */
object WidgetColors {
    // Base colors
    val transparent = ColorProvider(
        day = Color(0x00000000),
        night = Color(0x00000000)
    )
    
    // Premium surface colors with subtle gradients
    val surface = ColorProvider(
        day = Color(0xFFFEFEFE), // Pure white with slight warmth
        night = Color(0xFF0F0F0F) // Deep black with slight blue tint
    )
    
    val surfaceElevated = ColorProvider(
        day = Color(0xFFF8F9FA), // Subtle elevated surface
        night = Color(0xFF1A1A1A) // Elevated dark surface
    )
    
    val surfaceContainer = ColorProvider(
        day = Color(0xFFF1F3F4), // Container background
        night = Color(0xFF2A2A2A) // Dark container
    )
    
    // Premium text colors with proper hierarchy
    val onSurface = ColorProvider(
        day = Color(0xFF1A1A1A), // Rich black
        night = Color(0xFFF5F5F5) // Soft white
    )
    
    val onSurfaceMuted = ColorProvider(
        day = Color(0xFF5F6368), // Muted gray
        night = Color(0xFFB0B3B8) // Light muted
    )
    
    val onSurfaceSubtle = ColorProvider(
        day = Color(0xFF9AA0A6), // Subtle gray
        night = Color(0xFF8A8D93) // Dark subtle
    )
    
    // Premium accent colors
    val primary = ColorProvider(
        day = Color(0xFF1976D2), // Material Blue 700
        night = Color(0xFF42A5F5) // Material Blue 400
    )
    
    val primaryContainer = ColorProvider(
        day = Color(0xFFE3F2FD), // Light blue container
        night = Color(0xFF1A237E) // Dark blue container
    )
    
    val onPrimaryContainer = ColorProvider(
        day = Color(0xFF0D47A1), // Dark blue text
        night = Color(0xFFE3F2FD) // Light blue text
    )
    
    // Premium today highlighting
    val todayContainer = ColorProvider(
        day = Color(0xFF1976D2), // Material Blue 700
        night = Color(0xFF42A5F5) // Material Blue 400
    )
    
    val onTodayContainer = ColorProvider(
        day = Color(0xFFFFFFFF), // White text
        night = Color(0xFF000000) // Black text
    )
    
    // Premium selected day
    val selectedContainer = ColorProvider(
        day = Color(0xFFE8F5E8), // Light green
        night = Color(0xFF2E7D32) // Dark green
    )
    
    val onSelectedContainer = ColorProvider(
        day = Color(0xFF1B5E20), // Dark green text
        night = Color(0xFFE8F5E8) // Light green text
    )
    
    // Premium borders and dividers
    val border = ColorProvider(
        day = Color(0xFFE0E0E0), // Light border
        night = Color(0xFF424242) // Dark border
    )
    
    val borderSubtle = ColorProvider(
        day = Color(0xFFF0F0F0), // Very light border
        night = Color(0xFF2A2A2A) // Very dark border
    )
    
    // Premium bank holiday colors
    val bankHoliday = ColorProvider(
        day = Color(0xFFFFEBEE), // Light red background
        night = Color(0xFF4A1A1A) // Dark red background
    )
    
    val onBankHoliday = ColorProvider(
        day = Color(0xFFC62828), // Dark red text
        night = Color(0xFFFFCDD2) // Light red text
    )
    
    // Premium button colors
    val buttonBackground = ColorProvider(
        day = Color(0xFFF5F5F5), // Light button
        night = Color(0xFF2A2A2A) // Dark button
    )
    
    val buttonBackgroundHover = ColorProvider(
        day = Color(0xFFEEEEEE), // Hover state
        night = Color(0xFF3A3A3A) // Dark hover
    )
    
    val onButton = ColorProvider(
        day = Color(0xFF424242), // Button text
        night = Color(0xFFE0E0E0) // Light button text
    )
    
    // Premium shadow colors
    val shadow = ColorProvider(
        day = Color(0x1A000000), // Light shadow
        night = Color(0x4D000000) // Dark shadow
    )
}
