package com.alirezaiyan.kalendar.widget.ui

import androidx.compose.ui.graphics.Color
import androidx.glance.color.ColorProvider

/**
 * Modern premium color palette for the calendar widget.
 */
object WidgetColors {
    val transparent = ColorProvider(
        day = Color(0x00000000),
        night = Color(0x00000000)
    )
    
    val surface = ColorProvider(
        day = Color(0xFFFFFFFF),
        night = Color(0xFF0A0A0A)
    )
    
    val surfaceElevated = ColorProvider(
        day = Color(0xFFFAFAFA),
        night = Color(0xFF1A1A1A)
    )
    
    val onSurface = ColorProvider(
        day = Color(0xFF1A1A1A),
        night = Color(0xFFFFFFFF)
    )
    
    val onSurfaceMuted = ColorProvider(
        day = Color(0xFF6B7280),
        night = Color(0xFF9CA3AF)
    )
    
    val onSurfaceSubtle = ColorProvider(
        day = Color(0xFF9CA3AF),
        night = Color(0xFF6B7280)
    )
    
    val primary = ColorProvider(
        day = Color(0xFFF3F4F6),
        night = Color(0xFF374151)
    )
    
    val primaryContainer = ColorProvider(
        day = Color(0xFFF3F4F6),
        night = Color(0xFF374151)
    )
    
    val onPrimaryContainer = ColorProvider(
        day = Color(0xFF000000),
        night = Color(0xFFFFFFFF)
    )
    
    val todayContainer = ColorProvider(
        day = Color(0xFF000000),
        night = Color(0xFFFFFFFF)
    )
    
    val onTodayContainer = ColorProvider(
        day = Color(0xFFFFFFFF),
        night = Color(0xFF000000)
    )
    
    val border = ColorProvider(
        day = Color(0xFFE5E7EB),
        night = Color(0xFF374151)
    )
}
