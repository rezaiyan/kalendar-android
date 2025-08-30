package com.alirezaiyan.kalendar.widget.actions

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.alirezaiyan.kalendar.widget.ModernCalendarWidget
import com.alirezaiyan.kalendar.widget.state.WidgetState
import com.alirezaiyan.kalendar.widget.state.WidgetState.readYearMonth
import java.time.LocalDate
import java.time.YearMonth

class NavigateMonthAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val dir = parameters[Params.Direction] ?: return
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            val ym = prefs.readYearMonth() ?: YearMonth.from(LocalDate.now())
            val next = if (dir < 0) ym.minusMonths(1) else ym.plusMonths(1)
            prefs.toMutablePreferences().apply {
                this[WidgetState.YEAR_MONTH] = next.toString() // ISO-8601: yyyy-MM
            }
        }
        ModernCalendarWidget().update(context, glanceId)
    }
}