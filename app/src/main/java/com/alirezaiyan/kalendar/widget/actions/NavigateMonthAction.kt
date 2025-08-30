package com.alirezaiyan.kalendar.widget.actions

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.alirezaiyan.kalendar.calendar.CalendarService
import com.alirezaiyan.kalendar.data.CalendarType
import com.alirezaiyan.kalendar.data.Country
import com.alirezaiyan.kalendar.data.CountryRepository
import com.alirezaiyan.kalendar.widget.ModernCalendarWidget
import com.alirezaiyan.kalendar.widget.state.WidgetState
import com.alirezaiyan.kalendar.widget.state.WidgetState.readYearMonth
import kotlinx.coroutines.runBlocking
import saman.zamani.persiandate.PersianDate
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
            // Get country from the shared repository (same as MainActivity uses)
            val countryRepository = CountryRepository(context)
            val selectedCountry = runBlocking { countryRepository.getCurrentCountry() }

            val next = if (dir == 0) {
                // Special case: go to current month
                if (selectedCountry.calendarType == CalendarType.Solar) {
                    // For Solar Hijri calendar, get current Persian date
                    val persianDate = PersianDate()
                    val currentYear = persianDate.shYear
                    val currentMonth = persianDate.shMonth
                    // Store as Gregorian equivalent for consistency
                    val gregorianDate = LocalDate.of(persianDate.grgYear, persianDate.grgMonth, persianDate.grgDay)
                    YearMonth.from(gregorianDate)
                } else {
                    // For Gregorian calendar
                    YearMonth.from(LocalDate.now())
                }
            } else {
                val ym = prefs.readYearMonth() ?: if (selectedCountry.calendarType == CalendarType.Solar) {
                    val persianDate = PersianDate()
                    val gregorianDate = LocalDate.of(persianDate.grgYear, persianDate.grgMonth, persianDate.grgDay)
                    YearMonth.from(gregorianDate)
                } else {
                    YearMonth.from(LocalDate.now())
                }
                if (dir < 0) ym.minusMonths(1) else ym.plusMonths(1)
            }
            prefs.toMutablePreferences().apply {
                this[WidgetState.YEAR_MONTH] = next.toString() // ISO-8601: yyyy-MM
            }
        }
        ModernCalendarWidget().update(context, glanceId)
    }
}