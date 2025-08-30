package com.alirezaiyan.kalendar.calendar

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * Gregorian calendar system implementation
 * Used for most countries except Iran
 */
class GregorianCalendarSystem(private val locale: Locale = Locale.getDefault()) : CalendarSystem {
    
    override fun getCurrentDate(): CalendarDate {
        val today = LocalDate.now()
        return CalendarDate(
            year = today.year,
            month = today.monthValue,
            day = today.dayOfMonth,
            dayOfWeek = today.dayOfWeek,
            isLeapYear = today.isLeapYear,
            isCurrentMonth = true,
            isToday = true
        )
    }
    
    override fun getCurrentYear(): Int = LocalDate.now().year
    
    override fun getCurrentMonth(): Int = LocalDate.now().monthValue
    
    override fun getFirstDayOfWeek(): DayOfWeek {
        return java.time.temporal.WeekFields.of(locale).firstDayOfWeek
    }
    
    override fun getLocale(): Locale = locale
    
    override fun getMonthNames(): List<String> {
        return (1..12).map { month ->
            java.time.Month.of(month).getDisplayName(TextStyle.FULL, locale)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
        }
    }
    
    override fun getDayOfWeekNames(): List<String> {
        return DayOfWeek.values().map { day ->
            day.getDisplayName(TextStyle.SHORT, locale)
        }
    }
    
    override fun buildMonthGrid(year: Int, month: Int, firstDayOfWeek: DayOfWeek): List<List<CalendarDate>> {
        val currentDate = getCurrentDate()
        val yearMonth = java.time.YearMonth.of(year, month)
        val firstDayOfMonth = yearMonth.atDay(1)
        
        // Find the first day to display (might be from previous month)
        val firstDisplayDay = firstDayOfMonth.with(
            java.time.temporal.TemporalAdjusters.previousOrSame(firstDayOfWeek)
        )
        
        val grid = mutableListOf<List<CalendarDate>>()
        var currentDay = firstDisplayDay
        
        // Build 6 weeks (42 days) to ensure we always have a complete grid
        repeat(6) { week ->
            val weekDays = mutableListOf<CalendarDate>()
            repeat(7) { day ->
                val isCurrentMonth = (currentDay.monthValue == month && currentDay.year == year)
                val isToday = (currentDay.year == currentDate.year && 
                             currentDay.monthValue == currentDate.month && 
                             currentDay.dayOfMonth == currentDate.day)
                
                val calendarDate = CalendarDate(
                    year = currentDay.year,
                    month = currentDay.monthValue,
                    day = currentDay.dayOfMonth,
                    dayOfWeek = currentDay.dayOfWeek,
                    isLeapYear = currentDay.isLeapYear,
                    isCurrentMonth = isCurrentMonth,
                    isToday = isToday
                )
                
                weekDays.add(calendarDate)
                currentDay = currentDay.plusDays(1)
            }
            grid.add(weekDays)
        }
        
        return grid
    }
    
    override fun getDaysInMonth(year: Int, month: Int): Int {
        return java.time.YearMonth.of(year, month).lengthOfMonth()
    }
    
    override fun isLeapYear(year: Int): Boolean {
        return java.time.Year.of(year).isLeap
    }
    
    override fun getMonthDisplayName(month: Int): String {
        return if (month in 1..12) {
            java.time.Month.of(month).getDisplayName(TextStyle.FULL, locale)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
        } else {
            month.toString()
        }
    }
    
    override fun getDayOfWeekDisplayName(dayOfWeek: DayOfWeek): String {
        return dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
    }
}
