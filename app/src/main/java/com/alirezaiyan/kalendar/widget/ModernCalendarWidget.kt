@file:Suppress("PackageDirectoryMismatch")

package com.alirezaiyan.kalendar.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.ImageProvider
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.alirezaiyan.kalendar.R
import com.alirezaiyan.kalendar.data.BankHoliday
import com.alirezaiyan.kalendar.widget.actions.NavigateMonthAction
import com.alirezaiyan.kalendar.widget.actions.Params
import com.alirezaiyan.kalendar.widget.actions.SelectDayAction
import com.alirezaiyan.kalendar.widget.state.WidgetState.readSelectedDay
import com.alirezaiyan.kalendar.widget.state.WidgetState.readYearMonth
import com.alirezaiyan.kalendar.data.CountryRepository
import com.alirezaiyan.kalendar.data.Country
import com.alirezaiyan.kalendar.widget.ui.WidgetColors
import androidx.compose.runtime.collectAsState
import com.alirezaiyan.kalendar.widget.utils.CalendarUtils.buildMonthGrid
import com.alirezaiyan.kalendar.widget.utils.CalendarUtils.shifted
import com.alirezaiyan.kalendar.data.BankHolidayData
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale
import java.time.format.TextStyle as JTTextStyle

class ModernCalendarWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(
            DpSize(240.dp, 200.dp),
            DpSize(320.dp, 260.dp),
            DpSize(400.dp, 320.dp),
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            CalendarContent(context = context)
        }
    }

    @Composable
    private fun CalendarContent(context: Context) {
        val prefs = currentState<Preferences>()

        val locale = Locale.getDefault()
        val today = LocalDate.now()
        val yearMonth = prefs.readYearMonth() ?: YearMonth.from(today)
        val selected = prefs.readSelectedDay()?.let { LocalDate.ofEpochDay(it) }
        
        // Get country from the shared repository
        val countryRepository = CountryRepository(context)
        val selectedCountry = countryRepository.selectedCountry.collectAsState(initial = Country.UNITED_STATES).value

        val weekFields = WeekFields.of(locale)
        val firstDow = weekFields.firstDayOfWeek
        val monthMatrix = buildMonthGrid(yearMonth, firstDow)
        
        // Get bank holidays for the current year
        val bankHolidays = BankHolidayData.getBankHolidays(selectedCountry, yearMonth.year)

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(WidgetColors.surface)
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
                bankHolidays = bankHolidays,
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
        Box(
            modifier = GlanceModifier
                .size(32.dp)
                .background(WidgetColors.surfaceElevated)
                .cornerRadius(16.dp)
                .clickable(onPrev),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_chevron_left),
                contentDescription = "Previous month"
            )
        }

        Column(
            modifier = GlanceModifier.defaultWeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = monthName,
                style = TextStyle(
                    color = WidgetColors.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
            Text(
                text = year,
                style = TextStyle(
                    color = WidgetColors.onSurfaceMuted,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            )
        }

        // Next button with icon
        Box(
            modifier = GlanceModifier
                .size(32.dp)
                .background(WidgetColors.surfaceElevated)
                .cornerRadius(16.dp)
                .clickable(onNext),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_chevron_right),
                contentDescription = "Next month"
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
                        color = WidgetColors.onSurfaceSubtle,
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
    bankHolidays: List<BankHoliday>,
    onDayClick: (LocalDate) -> Action,
) {
    Column(modifier = GlanceModifier.fillMaxWidth()) {
        matrix.forEach { week ->
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                week.forEach { date ->
                    val isInMonth = inMonth(date)
                    val isToday = date == today
                    val isSelected = selected == date
                    val isBankHoliday = bankHolidays.any { it.date == date }

                    val bg = when {
                        isBankHoliday -> WidgetColors.bankHoliday
                        isSelected -> WidgetColors.primary
                        isToday -> WidgetColors.todayContainer
                        else -> WidgetColors.transparent
                    }

                    val textColor = when {
                        isBankHoliday -> WidgetColors.onBankHoliday
                        isSelected -> WidgetColors.onPrimaryContainer
                        isToday -> WidgetColors.onTodayContainer
                        isInMonth -> WidgetColors.onSurface
                        else -> WidgetColors.onSurfaceSubtle
                    }

                    val borderColor = when {
                        isToday && !isSelected && !isBankHoliday -> WidgetColors.border
                        else -> WidgetColors.transparent
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
                                    isBankHoliday -> FontWeight.Bold
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
                .background(WidgetColors.surfaceElevated)
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
                    color = WidgetColors.onSurfaceMuted,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            )
        }
    }
}