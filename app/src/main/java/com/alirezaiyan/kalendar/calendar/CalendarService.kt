package com.alirezaiyan.kalendar.calendar

import com.alirezaiyan.kalendar.data.Country
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale

/**
 * Service class that provides calendar functionality
 * Acts as a facade and follows Single Responsibility Principle
 */
class CalendarService(private val country: Country) {
    
    private val calendarSystem: CalendarSystem = CalendarFactory.createCalendarSystem(country)
    
    /**
     * Get the current date in the appropriate calendar system
     */
    fun getCurrentDate(): CalendarDate = calendarSystem.getCurrentDate()
    
    /**
     * Get the current year in the appropriate calendar system
     */
    fun getCurrentYear(): Int = calendarSystem.getCurrentYear()
    
    /**
     * Get the current month in the appropriate calendar system
     */
    fun getCurrentMonth(): Int = calendarSystem.getCurrentMonth()
    
    /**
     * Get the first day of week for this calendar system
     */
    fun getFirstDayOfWeek(): DayOfWeek = calendarSystem.getFirstDayOfWeek()
    
    /**
     * Get the locale for this calendar system
     */
    fun getLocale(): Locale = calendarSystem.getLocale()
    
    /**
     * Get month names in this calendar system
     */
    fun getMonthNames(): List<String> = calendarSystem.getMonthNames()
    
    /**
     * Get day of week names in this calendar system
     */
    fun getDayOfWeekNames(): List<String> = calendarSystem.getDayOfWeekNames()
    
    /**
     * Build a month grid for this calendar system
     */
    fun buildMonthGrid(year: Int, month: Int, firstDayOfWeek: DayOfWeek): List<List<CalendarDate>> {
        return calendarSystem.buildMonthGrid(year, month, firstDayOfWeek)
    }
    
    /**
     * Get the number of days in a specific month
     */
    fun getDaysInMonth(year: Int, month: Int): Int = calendarSystem.getDaysInMonth(year, month)
    
    /**
     * Check if a year is a leap year
     */
    fun isLeapYear(year: Int): Boolean = calendarSystem.isLeapYear(year)
    
    /**
     * Check if the current country uses Solar Hijri calendar
     */
    fun isSolarHijri(): Boolean = country == Country.IRAN
    
    /**
     * Get the display name for a month
     */
    fun getMonthDisplayName(month: Int): String = calendarSystem.getMonthDisplayName(month)
    
    /**
     * Get the display name for a day of week
     */
    fun getDayOfWeekDisplayName(dayOfWeek: DayOfWeek): String = calendarSystem.getDayOfWeekDisplayName(dayOfWeek)

    /**
     * Get the full display name for a day of week
     */
    fun getFullDayOfWeekDisplayName(dayOfWeek: DayOfWeek): String = calendarSystem.getFullDayOfWeekDisplayName(dayOfWeek)

    /**
     * Convert a LocalDate to CalendarDate in the current calendar system
     */
    fun convertToCalendar(localDate: LocalDate): CalendarDate {
        return when (country) {
            Country.IRAN -> {
                // For Solar Hijri, convert Gregorian to Solar Hijri
                val persianDate = saman.zamani.persiandate.PersianDate()
                persianDate.setGrgYear(localDate.year)
                persianDate.setGrgMonth(localDate.monthValue)
                persianDate.setGrgDay(localDate.dayOfMonth)
                
                CalendarDate(
                    year = persianDate.shYear,
                    month = persianDate.shMonth,
                    day = persianDate.shDay,
                    dayOfWeek = localDate.dayOfWeek,
                    isLeapYear = persianDate.isLeap,
                    isCurrentMonth = true,
                    isToday = false
                )
            }
            else -> {
                // For Gregorian, use the LocalDate directly
                CalendarDate(
                    year = localDate.year,
                    month = localDate.monthValue,
                    day = localDate.dayOfMonth,
                    dayOfWeek = localDate.dayOfWeek,
                    isLeapYear = localDate.isLeapYear,
                    isCurrentMonth = true,
                    isToday = false
                )
            }
        }
    }
}
