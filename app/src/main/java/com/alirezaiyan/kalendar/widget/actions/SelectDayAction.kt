package com.alirezaiyan.kalendar.widget.actions

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.alirezaiyan.kalendar.widget.ModernCalendarWidget
import com.alirezaiyan.kalendar.widget.state.WidgetState

class SelectDayAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val epochDay = parameters[Params.EpochDay] ?: return
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                this[WidgetState.SELECTED_EPOCH_DAY] = epochDay
            }
        }
        ModernCalendarWidget().update(context, glanceId)
    }
}