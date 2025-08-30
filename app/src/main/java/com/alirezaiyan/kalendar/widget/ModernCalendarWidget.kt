@file:Suppress("PackageDirectoryMismatch")

package com.alirezaiyan.kalendar.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.*
import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.*
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.compose.ui.graphics.Color as GlanceColor
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JTTextStyle
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

/**
 * A modern, clean calendar App Widget built with Jetpack Glance.
 *
 * Highlights
 * - Responsive sizing with rounded, adaptive background
 * - Month navigation (prev/next)
 * - Day selection state persisted per widget instance
 * - Today + selected day highlights
 * - Locale-aware first day of week & month title
 * - Clean architecture with small, testable helpers
 */
class ModernCalendarWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(
            DpSize(240.dp, 200.dp),
            DpSize(320.dp, 260.dp),
            DpSize(400.dp, 320.dp),
        )
    )

    val Context.myWidgetStore by preferencesDataStore("MyWidget")
    val Name = stringPreferencesKey("name")

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            CalendarContent()
        }
    }

    @Composable
    private fun CalendarContent() {
        val prefs = currentState<Preferences>()

        val locale = Locale.getDefault()
        val today = LocalDate.now()
        val yearMonth = prefs.readYearMonth() ?: YearMonth.from(today)
        val selected = prefs.readSelectedDay()?.let { LocalDate.ofEpochDay(it) }

        val weekFields = WeekFields.of(locale)
        val firstDow = weekFields.firstDayOfWeek
        val monthMatrix = MonthGrid.build(yearMonth, firstDow)

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Colors.surface)
                .padding(20.dp), 
            verticalAlignment = Alignment.Top
        ) {
            // Modern Header
            Header(
                yearMonth = yearMonth,
                onPrev = actionRunCallback<NavigateMonthAction>(
                    actionParametersOf(Params.Direction to -1)
                ),
                onNext = actionRunCallback<NavigateMonthAction>(
                    actionParametersOf(Params.Direction to 1)
                ),
            )

            Spacer(modifier = GlanceModifier.height(16.dp))
            
            // Days of week with modern styling
            DaysOfWeekRow(firstDow)
            
            Spacer(modifier = GlanceModifier.height(12.dp))

            // Calendar grid with premium spacing
            MonthGridView(
                matrix = monthMatrix,
                inMonth = { it.month == yearMonth.month },
                today = today,
                selected = selected,
                onDayClick = { date ->
                    actionRunCallback<SelectDayAction>(
                        actionParametersOf(Params.EpochDay to date.toEpochDay())
                    )
                }
            )

            Spacer(modifier = GlanceModifier.height(16.dp))
            
            // Minimal footer
            FooterOpenCalendar()
        }
    }
}

// -------------------------------
// UI Composables
// -------------------------------

@Composable
private fun Header(yearMonth: YearMonth, onPrev: Action, onNext: Action) {
    val locale = Locale.getDefault()
    val monthName = yearMonth.month.getDisplayName(JTTextStyle.FULL, locale)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    val year = yearMonth.year.toString()

    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Minimal navigation button
        Box(
            modifier = GlanceModifier
                .size(32.dp)
                .background(Colors.surfaceElevated)
                .cornerRadius(16.dp)
                .clickable(onPrev),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                style = TextStyle(
                    color = Colors.onSurfaceMuted,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            )
        }

        // Centered title with modern typography
        Column(
            modifier = GlanceModifier.defaultWeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = monthName,
                style = TextStyle(
                    color = Colors.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
            Text(
                text = year,
                style = TextStyle(
                    color = Colors.onSurfaceMuted,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            )
        }

        // Minimal navigation button
        Box(
            modifier = GlanceModifier
                .size(32.dp)
                .background(Colors.surfaceElevated)
                .cornerRadius(16.dp)
                .clickable(onNext),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "›",
                style = TextStyle(
                    color = Colors.onSurfaceMuted,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            )
        }
    }
}

@Composable
private fun DaysOfWeekRow(firstDow: DayOfWeek) {
    val locale = Locale.getDefault()
    Row(modifier = GlanceModifier.fillMaxWidth()) {
        DayOfWeek.entries.toTypedArray().shifted(firstDow).forEach { dow ->
            Box(
                modifier = GlanceModifier
                    .defaultWeight()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dow.getDisplayName(JTTextStyle.SHORT, locale),
                    style = TextStyle(
                        color = Colors.onSurfaceSubtle,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun MonthGridView(
    matrix: List<List<LocalDate>>,
    inMonth: (LocalDate) -> Boolean,
    today: LocalDate,
    selected: LocalDate?,
    onDayClick: (LocalDate) -> Action,
) {
    Column(modifier = GlanceModifier.fillMaxWidth()) {
        matrix.forEach { week ->
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                week.forEach { date ->
                    val isInMonth = inMonth(date)
                    val isToday = date == today
                    val isSelected = selected == date

                    val bg = when {
                        isSelected -> Colors.primary
                        isToday -> Colors.todayContainer
                        else -> Colors.transparent
                    }

                    val textColor = when {
                        isSelected -> Colors.onPrimaryContainer
                        isToday -> Colors.onTodayContainer
                        isInMonth -> Colors.onSurface
                        else -> Colors.onSurfaceSubtle
                    }

                    val borderColor = when {
                        isToday && !isSelected -> Colors.border
                        else -> Colors.transparent
                    }

                    Box(
                        modifier = GlanceModifier
                            .defaultWeight()
                            .height(40.dp)
                            .padding(1.dp)
                            .background(bg)
                            .cornerRadius(20.dp)
                            .clickable(onDayClick(date)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = TextStyle(
                                color = textColor,
                                fontWeight = when {
                                    isSelected -> FontWeight.Bold
                                    isToday -> FontWeight.Medium
                                    else -> FontWeight.Normal
                                },
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FooterOpenCalendar() {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = GlanceModifier
                .background(Colors.surfaceElevated)
                .cornerRadius(20.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(
                    actionStartActivity(
                        Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_APP_CALENDAR) }
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Open Calendar",
                style = TextStyle(
                    color = Colors.onSurfaceMuted,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            )
        }
    }
}

// -------------------------------
// Actions (navigation & selection)
// -------------------------------

object Params {
    val Direction = ActionParameters.Key<Int>("dir")
    val EpochDay = ActionParameters.Key<Long>("epochDay")
}

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
                this[Keys.YEAR_MONTH] = next.toString() // ISO-8601: yyyy-MM
            }
        }
        ModernCalendarWidget().update(context, glanceId)
    }
}

class SelectDayAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val epochDay = parameters[Params.EpochDay] ?: return
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                this[Keys.SELECTED_EPOCH_DAY] = epochDay
            }
        }
        ModernCalendarWidget().update(context, glanceId)
    }
}

// -------------------------------
// State helpers
// -------------------------------

private object Keys {
    val YEAR_MONTH = stringPreferencesKey("year_month")
    val SELECTED_EPOCH_DAY = longPreferencesKey("selected_epoch_day")
}

private fun Preferences.readYearMonth(): YearMonth? = this[Keys.YEAR_MONTH]?.let(YearMonth::parse)
private fun Preferences.readSelectedDay(): Long? = this[Keys.SELECTED_EPOCH_DAY]

// -------------------------------
// Colors - Modern Premium Palette
// -------------------------------

private object Colors {
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
    val accent = ColorProvider(
        day = Color(0xFF6366F1),
        night = Color(0xFF8B5CF6)
    )
    val border = ColorProvider(
        day = Color(0xFFE5E7EB),
        night = Color(0xFF374151)
    )
}

// -------------------------------
// Data & utilities
// -------------------------------

private object MonthGrid {
    /**
     * Build a 6x7 matrix covering the full weeks that intersect [yearMonth], starting on [firstDow].
     */
    fun build(yearMonth: YearMonth, firstDow: DayOfWeek): List<List<LocalDate>> {
        val firstOfMonth = yearMonth.atDay(1)
        val start = firstOfMonth.with(TemporalAdjusters.previousOrSame(firstDow))
        var cur = start
        return List(6) {
            List(7) { cur.also { cur = cur.plusDays(1) } }
        }
    }
}

private fun Array<DayOfWeek>.shifted(first: DayOfWeek): List<DayOfWeek> {
    val idx = indexOf(first)
    return if (idx <= 0) toList() else slice(idx until size) + slice(0 until idx)
}


