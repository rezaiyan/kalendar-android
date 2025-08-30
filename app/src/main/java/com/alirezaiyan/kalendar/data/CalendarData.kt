package com.alirezaiyan.kalendar.data

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean
)

data class CalendarMonth(
    val yearMonth: YearMonth,
    val days: List<CalendarDay>
)

object CalendarUtils {
    
    private val weekFields = WeekFields.of(Locale.getDefault())
    private val dayFormatter = DateTimeFormatter.ofPattern("d")
    private val monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    
    fun getCurrentMonth(): CalendarMonth {
        return getMonthFor(YearMonth.now())
    }
    
    fun getMonthFor(yearMonth: YearMonth): CalendarMonth {
        val firstDayOfMonth = yearMonth.atDay(1)
        val lastDayOfMonth = yearMonth.atEndOfMonth()
        
        // Get the first day of the week that contains the first day of the month
        val firstDayOfWeek = firstDayOfMonth.with(weekFields.dayOfWeek(), 1)
        
        // Get the last day of the week that contains the last day of the month
        val lastDayOfWeek = lastDayOfMonth.with(weekFields.dayOfWeek(), 7)
        
        val days = mutableListOf<CalendarDay>()
        var currentDate = firstDayOfWeek
        
        while (!currentDate.isAfter(lastDayOfWeek)) {
            val isCurrentMonth = currentDate.month == yearMonth.month && 
                                currentDate.year == yearMonth.year
            val isToday = currentDate == LocalDate.now()
            
            days.add(CalendarDay(currentDate, isCurrentMonth, isToday))
            currentDate = currentDate.plusDays(1)
        }
        
        return CalendarMonth(yearMonth, days)
    }
    
    fun formatDay(day: CalendarDay): String {
        return day.date.format(dayFormatter)
    }
    
    fun formatMonth(yearMonth: YearMonth): String {
        return yearMonth.format(monthFormatter)
    }
    
    fun getWeekDayHeaders(): List<String> {
        val formatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())
        return (1..7).map { dayOfWeek ->
            LocalDate.now()
                .with(weekFields.dayOfWeek(), dayOfWeek.toLong())
                .format(formatter)
        }
    }
}

