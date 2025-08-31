package com.alirezaiyan.kalendar.widget.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

/**
 * Calendar utility functions.
 */
object CalendarUtils {
    
    fun buildMonthGrid(yearMonth: YearMonth, firstDow: DayOfWeek): List<List<LocalDate>> {
        val firstOfMonth = yearMonth.atDay(1)
        val start = firstOfMonth.with(TemporalAdjusters.previousOrSame(firstDow))
        var current = start
        
        return List(6) {
            List(7) { 
                current.also { current = current.plusDays(1) } 
            }
        }
    }

    fun Array<DayOfWeek>.shifted(firstDay: DayOfWeek): Array<DayOfWeek> {
        val shift = firstDay.value - 1
        return Array(size) { this[(it + shift) % size] }
    }

    fun getFirstDayOfWeek(locale: Locale = Locale.getDefault()): DayOfWeek {
        return WeekFields.of(locale).firstDayOfWeek
    }
}
