package com.alirezaiyan.kalendar.calendar

import java.time.DayOfWeek
import java.util.Locale
import saman.zamani.persiandate.PersianDate

/**
 * Solar Hijri (Jalali) calendar system using PersianDate 1.7.1
 */
class SolarHijriCalendarSystem(
    private val locale: Locale = Locale("fa", "IR")
) : CalendarSystem {

    private val persianMonthNames = listOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    private val persianDayNames = listOf(
        "ش", "ی", "د", "س", "چ", "پ", "ج" // شنبه..جمعه
    )

    private val persianFillDayNames = listOf(
        "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهار‌شنبه", "پنح‌شنبه", "جمعه" // شنبه..جمعه
    )

    override fun getCurrentDate(): CalendarDate {
        val today = java.time.LocalDate.now()
        return gregorianToSolarHijri(today)
    }

    override fun getCurrentYear(): Int {
        return getCurrentDate().year
    }

    override fun getCurrentMonth(): Int {
        return getCurrentDate().month
    }

    override fun getFirstDayOfWeek(): DayOfWeek = DayOfWeek.SATURDAY

    override fun getLocale(): Locale = locale

    override fun getMonthNames(): List<String> = persianMonthNames

    override fun getDayOfWeekNames(): List<String> = persianDayNames

    override fun buildMonthGrid(year: Int, month: Int, firstDayOfWeek: DayOfWeek): List<List<CalendarDate>> {
        val currentDate = getCurrentDate()
        
        // Create a 6x7 grid
        val grid = mutableListOf<List<CalendarDate>>()
        
        // Find the first day of the month and its day of week
        val firstDayOfMonth = CalendarDate(year, month, 1, getDayOfWeekForDate(year, month, 1), isLeapYear(year))
        
        // Calculate how many days from previous month to show
        val firstDayWeekday = firstDayOfMonth.dayOfWeek
        val daysFromPrevMonth = (firstDayWeekday.value - firstDayOfWeek.value + 7) % 7
        
        // Start from the first day to display (might be from previous month)
        var currentDay = 1 - daysFromPrevMonth
        var currentMonth = month
        var currentYear = year
        
        // Handle previous month
        if (currentDay <= 0) {
            currentMonth = if (month == 1) 12 else month - 1
            currentYear = if (month == 1) year - 1 else year
            val prevMonthDays = getDaysInMonth(currentYear, currentMonth)
            currentDay = prevMonthDays + currentDay + 1
        }
        
        // Build 6 weeks
        repeat(6) { week ->
            val weekDays = mutableListOf<CalendarDate>()
            repeat(7) { day ->
                // Validate the date before creating CalendarDate
                val daysInCurrentMonth = getDaysInMonth(currentYear, currentMonth)
                val validDay = if (currentDay > daysInCurrentMonth) {
                    // This day doesn't exist in current month, use day 1 of next month
                    1
                } else if (currentDay < 1) {
                    // This day doesn't exist, use last day of previous month
                    getDaysInMonth(
                        if (currentMonth == 1) currentYear - 1 else currentYear,
                        if (currentMonth == 1) 12 else currentMonth - 1
                    )
                } else {
                    currentDay
                }
                
                val isCurrentMonth = (currentMonth == month && currentYear == year)
                val isToday = (currentYear == currentDate.year && 
                             currentMonth == currentDate.month && 
                             validDay == currentDate.day)
                
                val calendarDate = CalendarDate(
                    year = currentYear,
                    month = currentMonth,
                    day = validDay,
                    dayOfWeek = getDayOfWeekForDate(currentYear, currentMonth, validDay),
                    isLeapYear = isLeapYear(currentYear),
                    isCurrentMonth = isCurrentMonth,
                    isToday = isToday
                )
                
                weekDays.add(calendarDate)
                
                // Move to next day
                currentDay++
                if (currentDay > getDaysInMonth(currentYear, currentMonth)) {
                    currentDay = 1
                    currentMonth++
                    if (currentMonth > 12) {
                        currentMonth = 1
                        currentYear++
                    }
                }
            }
            grid.add(weekDays)
        }
        
        return grid
    }

    override fun getDaysInMonth(year: Int, month: Int): Int {
        val pd = PersianDate()
        pd.setShYear(year)
        pd.setShMonth(month)
        pd.setShDay(1)
        return pd.monthLength
    }

    override fun isLeapYear(year: Int): Boolean {
        val pd = PersianDate()
        pd.setShYear(year)
        pd.setShMonth(1)
        pd.setShDay(1)
        return pd.isLeap
    }

    override fun getMonthDisplayName(month: Int): String {
        return if (month in 1..persianMonthNames.size) {
            persianMonthNames[month - 1]
        } else {
            month.toString()
        }
    }

    override fun getDayOfWeekDisplayName(dayOfWeek: DayOfWeek): String {
        // Map Java DayOfWeek (MON=0..SUN=6 by ordinal) to Persian order (SAT..FRI)
        // SATURDAY -> 0, SUNDAY -> 1, MONDAY -> 2, ..., FRIDAY -> 6
        val index = (dayOfWeek.ordinal + 2) % 7
        return if (index in persianDayNames.indices) persianDayNames[index] else dayOfWeek.name
    }


    override fun getFullDayOfWeekDisplayName(dayOfWeek: DayOfWeek): String {
        // Map Java DayOfWeek (MON=0..SUN=6 by ordinal) to Persian order (SAT..FRI)
        // SATURDAY -> 0, SUNDAY -> 1, MONDAY -> 2, ..., FRIDAY -> 6
        val index = (dayOfWeek.ordinal + 2) % 7
        return if (index in persianDayNames.indices) persianFillDayNames[index] else dayOfWeek.name
    }

    // ---------- Internals powered by PersianDate ----------

    private fun gregorianToSolarHijri(g: java.time.LocalDate): CalendarDate {
        val arr = PersianDate().gregorian_to_jalali(g.year, g.monthValue, g.dayOfMonth) // [jy, jm, jd]
        val jy = arr[0]
        val jm = arr[1]
        val jd = arr[2]
        return CalendarDate(
            year = jy,
            month = jm,
            day = jd,
            dayOfWeek = g.dayOfWeek,
            isLeapYear = isLeapYear(jy),
            isCurrentMonth = true,
            isToday = false
        )
    }

    private fun getDayOfWeekForDate(year: Int, month: Int, day: Int): DayOfWeek {
        // Convert Solar Hijri to Gregorian to get the day of week
        val g = PersianDate().jalali_to_gregorian(year, month, day)
        val gregorianDate = java.time.LocalDate.of(g[0], g[1], g[2])
        return gregorianDate.dayOfWeek
    }
}
