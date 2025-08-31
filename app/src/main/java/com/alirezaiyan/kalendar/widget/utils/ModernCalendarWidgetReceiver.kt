package com.alirezaiyan.kalendar.widget.utils

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.alirezaiyan.kalendar.widget.ModernCalendarWidget

class ModernCalendarWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ModernCalendarWidget()
}