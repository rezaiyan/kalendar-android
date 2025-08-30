package com.alirezaiyan.kalendar.calendar

import java.time.DayOfWeek
import java.util.Locale

/**
 * Interface for different calendar systems following SOLID principles
 * Single Responsibility: Each implementation handles one calendar system
 * Open/Closed: Easy to extend with new calendar systems
 * Liskov Substitution: All implementations are interchangeable
 */
interface CalendarSystem {
    /**
     * Get the current date in this calendar system
     */
    fun getCurrentDate(): CalendarDate
    
    /**
     * Get the current year in this calendar system
     */
    fun getCurrentYear(): Int
    
    /**
     * Get the current month in this calendar system
     */
    fun getCurrentMonth(): Int
    
    /**
     * Get the first day of week for this calendar system
     */
    fun getFirstDayOfWeek(): DayOfWeek
    
    /**
     * Get the locale for this calendar system
     */
    fun getLocale(): Locale
    
    /**
     * Get month names in this calendar system
     */
    fun getMonthNames(): List<String>
    
    /**
     * Get day of week names in this calendar system
     */
    fun getDayOfWeekNames(): List<String>
    
    /**
     * Build a month grid for this calendar system
     * Returns a 6x7 grid of CalendarDate objects
     */
    fun buildMonthGrid(year: Int, month: Int, firstDayOfWeek: DayOfWeek): List<List<CalendarDate>>
    
    /**
     * Get the number of days in a specific month
     */
    fun getDaysInMonth(year: Int, month: Int): Int
    
    /**
     * Check if a year is a leap year
     */
    fun isLeapYear(year: Int): Boolean
    
    /**
     * Get the display name for a month
     */
    fun getMonthDisplayName(month: Int): String
    
    /**
     * Get the display name for a day of week
     */
    fun getDayOfWeekDisplayName(dayOfWeek: DayOfWeek): String
}

/**
 * Represents a date in any calendar system
 */
data class CalendarDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val dayOfWeek: DayOfWeek,
    val isLeapYear: Boolean = false,
    val isCurrentMonth: Boolean = true,
    val isToday: Boolean = false
)
