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
import com.alirezaiyan.kalendar.data.CalendarType
import com.alirezaiyan.kalendar.widget.ui.WidgetColors
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.alirezaiyan.kalendar.MainActivity
import com.alirezaiyan.kalendar.data.BankHolidayData
import com.alirezaiyan.kalendar.calendar.CalendarDate
import com.alirezaiyan.kalendar.calendar.CalendarService
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale
import java.time.format.TextStyle as JTTextStyle

// Utility function for shifting day of week array
private fun Array<DayOfWeek>.shifted(firstDay: DayOfWeek): Array<DayOfWeek> {
    val shift = firstDay.value - 1
    return Array(size) { this[(it + shift) % size] }
}

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

        val countryRepository = remember { CountryRepository(context) }
        val selectedCountry by countryRepository.selectedCountry.collectAsState(initial = Country.UNITED_STATES)

        // Create calendar service for the selected country
        val calendarService = remember(selectedCountry.calendarType) {
            CalendarService(selectedCountry)
        }

        // Always get current date (not remembered, so it updates when country changes)
        val today = calendarService.getCurrentDate()
        val currentYear = calendarService.getCurrentYear()
        val currentMonth = calendarService.getCurrentMonth()
        
        // Get selected day from preferences, fallback to today
        val selected = prefs.readSelectedDay()?.let { epochDay ->
            try {
                val localDate = java.time.LocalDate.ofEpochDay(epochDay)
                calendarService.convertToCalendar(localDate)
            } catch (e: java.time.DateTimeException) {
                today
            }
        } ?: today

        // Use persisted year-month if present (set by NavigateMonthAction)
        // The stored YearMonth is always in Gregorian format, so we need to convert it
        val persistedYm = prefs.readYearMonth()
        val displayYear: Int
        val displayMonth: Int
        
        if (persistedYm != null) {
            // Convert the stored Gregorian YearMonth to the current calendar system
            val gregorianDate = persistedYm.atDay(1) // Use first day of month
            if (selectedCountry.calendarType == CalendarType.Solar) {
                // Convert Gregorian to Solar Hijri
                val persianDate = saman.zamani.persiandate.PersianDate()
                persianDate.setGrgYear(gregorianDate.year)
                persianDate.setGrgMonth(gregorianDate.monthValue)
                persianDate.setGrgDay(gregorianDate.dayOfMonth)
                displayYear = persianDate.shYear
                displayMonth = persianDate.shMonth
            } else {
                // For Gregorian, use directly
                displayYear = persistedYm.year
                displayMonth = persistedYm.monthValue
            }
        } else {
            // No persisted month, use current month
            displayYear = currentYear
            displayMonth = currentMonth
        }

        val firstDow = calendarService.getFirstDayOfWeek()
        val monthMatrix = calendarService.buildMonthGrid(displayYear, displayMonth, firstDow)

        // Get bank holidays for the displayed year
        val bankHolidays = BankHolidayData.getBankHolidays(selectedCountry, displayYear)

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(WidgetColors.surface)
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Modern Header
            Header(
                year = displayYear,
                month = displayMonth,
                calendarService = calendarService,
                isCompact = true,
                today = today,
                selected = selected,
                onPrev = actionRunCallback<NavigateMonthAction>(
                    actionParametersOf(Params.Direction to -1)
                ),
                onNext = actionRunCallback<NavigateMonthAction>(
                    actionParametersOf(Params.Direction to 1)
                ),
                onGoToToday = actionRunCallback<NavigateMonthAction>(
                    actionParametersOf(Params.Direction to 0) // Special value to go to current month
                ),
            )

            Spacer(modifier = GlanceModifier.height(2.dp))

            // Days of week with modern styling
            DaysOfWeekRow(calendarService, true)

            Spacer(modifier = GlanceModifier.height(2.dp))

            // Calendar grid with premium spacing
            MonthGridView(
                matrix = monthMatrix,
                selected = selected,
                bankHolidays = bankHolidays,
                dayCellHeight = 26.dp,
                dayCellRadius = 13.dp,
                onDayClick = { date ->
                    try {
                        // Convert CalendarDate to LocalDate for epoch day calculation
                        val localDate = java.time.LocalDate.of(date.year, date.month, date.day)
                        actionRunCallback<SelectDayAction>(
                            actionParametersOf(Params.EpochDay to localDate.toEpochDay())
                        )
                    } catch (e: java.time.DateTimeException) {
                        // Handle invalid dates gracefully - do nothing
                        actionRunCallback<SelectDayAction>(
                            actionParametersOf(Params.EpochDay to 0L)
                        )
                    }
                }
            )

            Spacer(modifier = GlanceModifier.height(2.dp))

            FooterOpenCalendar(context)
        }
    }


}

@Composable
private fun Header(
    year: Int,
    month: Int,
    calendarService: CalendarService,
    isCompact: Boolean = false,
    today: CalendarDate,
    selected: CalendarDate?,
    onPrev: Action,
    onNext: Action,
    onGoToToday: Action
) {
    val monthName = calendarService.getMonthDisplayName(month)
    val yearStr = year.toString()

    val buttonSize = if (isCompact) 28.dp else 32.dp
    val monthFontSize = if (isCompact) 16.sp else 18.sp
    val yearFontSize = if (isCompact) 12.sp else 14.sp

    Column(
        modifier = GlanceModifier.fillMaxWidth()
    ) {
        // Main header row with navigation and month/year
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = GlanceModifier
                    .size(buttonSize)
                    .background(WidgetColors.surfaceElevated)
                    .cornerRadius(buttonSize / 2)
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
                        fontSize = monthFontSize
                    )
                )
                Text(
                    text = yearStr,
                    style = TextStyle(
                        color = WidgetColors.onSurfaceMuted,
                        fontWeight = FontWeight.Normal,
                        fontSize = yearFontSize
                    )
                )
            }

            // Next button with icon
            Box(
                modifier = GlanceModifier
                    .size(buttonSize)
                    .background(WidgetColors.surfaceElevated)
                    .cornerRadius(buttonSize / 2)
                    .clickable(onNext),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_chevron_right),
                    contentDescription = "Next month"
                )
            }
        }

        // "Go to Today" button - only show if we're not viewing the current month
        val isCurrentMonth = (year == today.year && month == today.month)
        if (!isCurrentMonth) {
            Spacer(modifier = GlanceModifier.height(4.dp))
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = GlanceModifier
                        .background(WidgetColors.todayContainer)
                        .cornerRadius(12.dp)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .clickable(onGoToToday),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Go to Today",
                        style = TextStyle(
                            color = WidgetColors.onTodayContainer,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DaysOfWeekRow(calendarService: CalendarService, isCompact: Boolean = false) {
    val fontSize = if (isCompact) 10.sp else 11.sp
    val verticalPadding = if (isCompact) 4.dp else 8.dp
    val firstDow = calendarService.getFirstDayOfWeek()

    Row(modifier = GlanceModifier.fillMaxWidth()) {
        DayOfWeek.entries.toTypedArray().shifted(firstDow).forEach { dow ->
            Box(
                modifier = GlanceModifier
                    .defaultWeight()
                    .padding(vertical = verticalPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = calendarService.getDayOfWeekDisplayName(dow),
                    style = TextStyle(
                        color = WidgetColors.onSurfaceSubtle,
                        fontWeight = FontWeight.Medium,
                        fontSize = fontSize
                    )
                )
            }
        }
    }
}

@Composable
private fun MonthGridView(
    matrix: List<List<CalendarDate>>,
    selected: CalendarDate?,
    bankHolidays: List<BankHoliday>,
    dayCellHeight: androidx.compose.ui.unit.Dp = 32.dp,
    dayCellRadius: androidx.compose.ui.unit.Dp = 16.dp,
    onDayClick: (CalendarDate) -> Action,
) {
    Column(modifier = GlanceModifier.fillMaxWidth()) {
        matrix.forEach { week ->
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                week.forEach { date ->
                    val isInMonth = date.isCurrentMonth
                    val isToday = date.isToday
                    val isSelected =
                        selected?.let { it.year == date.year && it.month == date.month && it.day == date.day }
                            ?: false
                    val isBankHoliday = try {
                        // Convert CalendarDate to LocalDate for comparison with BankHoliday
                        val localDate = java.time.LocalDate.of(date.year, date.month, date.day)
                        bankHolidays.any { it.date == localDate }
                    } catch (e: java.time.DateTimeException) {
                        // Handle invalid dates gracefully (e.g., June 31st)
                        false
                    }

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



                    Box(
                        modifier = GlanceModifier
                            .defaultWeight()
                            .height(dayCellHeight)
                            .padding(1.dp)
                            .background(bg)
                            .cornerRadius(dayCellRadius)
                            .clickable(onDayClick(date)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.day.toString(),
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
private fun FooterOpenCalendar(context: Context) {
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
                .clickable(actionStartActivity(Intent(context, MainActivity::class.java))),
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

