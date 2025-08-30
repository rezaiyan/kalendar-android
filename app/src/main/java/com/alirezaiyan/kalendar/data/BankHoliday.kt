package com.alirezaiyan.kalendar.data

import java.time.LocalDate

import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField

data class BankHoliday(
    val name: String,
    val date: LocalDate,
    val isFixed: Boolean = true // true for fixed dates, false for calculated dates
)


object BankHolidayData {

    fun getBankHolidays(country: Country, year: Int): List<BankHoliday> {
        return when (country) {
            Country.UNITED_STATES   -> getUSBankHolidays(year)
            Country.UNITED_KINGDOM  -> getUKBankHolidays(year)
            Country.GERMANY         -> getGermanBankHolidays(year)
            Country.FRANCE          -> getFrenchBankHolidays(year)
            Country.ITALY           -> getItalianBankHolidays(year)
            Country.AUSTRALIA       -> getAustralianBankHolidays(year)
            Country.AUSTRIA         -> getAustrianBankHolidays(year)
            Country.IRAN            -> getIranBankHolidays(year)
        }
    }

    // -------------------- Austria (nationwide) --------------------
    // No weekend substitution/observed rules in general.
    private fun getAustrianBankHolidays(year: Int): List<BankHoliday> {
        val easter = easterDate(year)
        return listOf(
            BankHoliday("New Year's Day", LocalDate.of(year, 1, 1)),
            BankHoliday("Epiphany", LocalDate.of(year, 1, 6)),
            BankHoliday("Easter Monday", easter.plusDays(1)),
            BankHoliday("Labour Day", LocalDate.of(year, 5, 1)),
            BankHoliday("Ascension Day", easter.plusDays(39)),
            BankHoliday("Whit Monday", easter.plusDays(50)),
            BankHoliday("Corpus Christi", easter.plusDays(60)),
            BankHoliday("Assumption Day", LocalDate.of(year, 8, 15)),
            BankHoliday("National Day", LocalDate.of(year, 10, 26)),
            BankHoliday("All Saints' Day", LocalDate.of(year, 11, 1)),
            BankHoliday("Immaculate Conception", LocalDate.of(year, 12, 8)),
            BankHoliday("Christmas Day", LocalDate.of(year, 12, 25)),
            BankHoliday("St. Stephen's Day", LocalDate.of(year, 12, 26))
        ).sortedBy { it.date }
    }

    // -------------------- Iran (national) --------------------
    // Mix of Solar Hijri (fixed in Persian calendar, approximated here) and Islamic lunar (computed via HijrahDate).
    private fun getIranBankHolidays(year: Int): List<BankHoliday> {
        val out = mutableListOf<BankHoliday>()

        // --- Fixed Gregorian dates (exact) ---
        out += BankHoliday("Victory of the Islamic Revolution", LocalDate.of(year, 2, 11)) // 22 Bahman
        out += BankHoliday("Imam Khomeini’s Demise", LocalDate.of(year, 6, 3))            // 14 Khordad
        out += BankHoliday("15 Khordad Uprising", LocalDate.of(year, 6, 5))               // 15 Khordad

        // --- Nowruz block (Solar Hijri) ---
        // Approximation: Nowruz 1–4 Farvardin ~ Mar 21–24; Islamic Republic Day (Farvardin 12) ~ Apr 1; Nature Day (Farvardin 13) ~ Apr 2
        // For astronomical accuracy, replace with a Persian calendar conversion.
        out += BankHoliday("Nowruz (New Year)", LocalDate.of(year, 3, 21))
        out += BankHoliday("Nowruz Holiday", LocalDate.of(year, 3, 22))
        out += BankHoliday("Nowruz Holiday", LocalDate.of(year, 3, 23))
        out += BankHoliday("Nowruz Holiday", LocalDate.of(year, 3, 24))
        out += BankHoliday("Islamic Republic Day", LocalDate.of(year, 4, 1))
        out += BankHoliday("Nature Day (Sizdah-bedar)", LocalDate.of(year, 4, 2))

        // --- Islamic lunar holidays (calculated dynamically) ---
        // Using a simplified approach to calculate approximate Islamic holiday dates
        // Note: For production, consider using a proper Islamic calendar library
        
        // Calculate approximate Islamic holidays based on year
        val islamicHolidays = calculateIslamicHolidays(year)
        out.addAll(islamicHolidays)

        return out.sortedBy { it.date }
    }

    /**
     * Calculate approximate Islamic holiday dates for a given year
     * This uses a simplified algorithm based on the Islamic lunar calendar
     * Note: For production accuracy, use a proper Islamic calendar library
     */
    private fun calculateIslamicHolidays(year: Int): List<BankHoliday> {
        val holidays = mutableListOf<BankHoliday>()
        
        // Base year for calculations (2024 as reference)
        val baseYear = 2024
        val baseEidFitr = LocalDate.of(2024, 4, 10) // Approximate Eid al-Fitr 2024
        val baseEidAdha = LocalDate.of(2024, 6, 16) // Approximate Eid al-Adha 2024
        val baseAshura = LocalDate.of(2024, 7, 17)  // Approximate Ashura 2024
        
        // Calculate year difference
        val yearDiff = year - baseYear
        
        // Islamic lunar year is approximately 11 days shorter than Gregorian year
        // So each year, Islamic holidays move about 11 days earlier
        val daysOffset = yearDiff * 11
        
        // Calculate approximate dates for this year
        val eidFitr1 = baseEidFitr.plusDays(daysOffset.toLong())
        val eidFitr2 = eidFitr1.plusDays(1)
        val eidAdha = baseEidAdha.plusDays(daysOffset.toLong())
        val ashura = baseAshura.plusDays(daysOffset.toLong())
        val tasua = ashura.minusDays(1)
        
        // Add major Islamic holidays
        holidays += BankHoliday("Eid al-Fitr (1st day)", eidFitr1)
        holidays += BankHoliday("Eid al-Fitr (2nd day)", eidFitr2)
        holidays += BankHoliday("Eid al-Adha", eidAdha)
        holidays += BankHoliday("Tasua (9 Muharram)", tasua)
        holidays += BankHoliday("Ashura (10 Muharram)", ashura)
        
        // Calculate other holidays relative to Ashura
        val arbaen = ashura.plusDays(40) // 40 days after Ashura
        val prophetDemise = ashura.plusDays(60) // Approximate
        val mabath = ashura.minusDays(150) // Approximate
        val imamAliBirthday = ashura.minusDays(160) // Approximate
        val prophetBirthday = ashura.plusDays(70) // Approximate
        val imamMahdiBirthday = ashura.minusDays(140) // Approximate
        val imamAliMartyrdom = eidFitr1.minusDays(10) // Approximate
        
        holidays += BankHoliday("Arba'een (20 Safar)", arbaen)
        holidays += BankHoliday("Prophet's Demise & Imam Hasan (28 Safar)", prophetDemise)
        holidays += BankHoliday("Mab'ath (27 Rajab)", mabath)
        holidays += BankHoliday("Imam Ali's Birthday (13 Rajab)", imamAliBirthday)
        holidays += BankHoliday("Prophet's Birthday & Imam Sadegh (17 Rabi' al-awwal)", prophetBirthday)
        holidays += BankHoliday("Imam Mahdi's Birthday (15 Sha'ban)", imamMahdiBirthday)
        holidays += BankHoliday("Imam Ali's Martyrdom (21 Ramadan)", imamAliMartyrdom)
        
        return holidays
    }

    // -------------------- Existing countries (unchanged from my previous reply) --------------------
    private fun getUSBankHolidays(year: Int): List<BankHoliday> {
        val dates = mutableListOf<BankHoliday>()
        fun observedUS(date: LocalDate) = when (date.dayOfWeek) {
            DayOfWeek.SATURDAY -> date.minusDays(1)
            DayOfWeek.SUNDAY   -> date.plusDays(1)
            else               -> date
        }
        fun fixed(name: String, m: Int, d: Int) {
            val date = LocalDate.of(year, m, d)
            dates += BankHoliday(name, observedUS(date))
        }
        dates += BankHoliday("New Year's Day", observedUS(LocalDate.of(year, 1, 1)))
        dates += BankHoliday("Martin Luther King Jr. Day", nthWeekdayOfMonth(year, 1, 3, DayOfWeek.MONDAY))
        dates += BankHoliday("Presidents' Day", nthWeekdayOfMonth(year, 2, 3, DayOfWeek.MONDAY))
        dates += BankHoliday("Memorial Day", lastWeekdayOfMonth(year, 5, DayOfWeek.MONDAY))
        fixed("Juneteenth National Independence Day", 6, 19)
        dates += BankHoliday("Independence Day", observedUS(LocalDate.of(year, 7, 4)))
        dates += BankHoliday("Labor Day", nthWeekdayOfMonth(year, 9, 1, DayOfWeek.MONDAY))
        dates += BankHoliday("Columbus Day", nthWeekdayOfMonth(year, 10, 2, DayOfWeek.MONDAY))
        fixed("Veterans Day", 11, 11)
        dates += BankHoliday("Thanksgiving Day", nthWeekdayOfMonth(year, 11, 4, DayOfWeek.THURSDAY))
        dates += BankHoliday("Christmas Day", observedUS(LocalDate.of(year, 12, 25)))
        return dates.sortedBy { it.date }
    }

    private fun getUKBankHolidays(year: Int): List<BankHoliday> {
        val easter = easterDate(year)
        val base = mutableListOf(
            BankHoliday("New Year's Day", LocalDate.of(year, 1, 1)),
            BankHoliday("Good Friday", easter.minusDays(2)),
            BankHoliday("Easter Monday", easter.plusDays(1)),
            BankHoliday("Early May Bank Holiday", nthWeekdayOfMonth(year, 5, 1, DayOfWeek.MONDAY)),
            BankHoliday("Spring Bank Holiday", lastWeekdayOfMonth(year, 5, DayOfWeek.MONDAY)),
            BankHoliday("Summer Bank Holiday", lastWeekdayOfMonth(year, 8, DayOfWeek.MONDAY)),
            BankHoliday("Christmas Day", LocalDate.of(year, 12, 25)),
            BankHoliday("Boxing Day", LocalDate.of(year, 12, 26))
        )
        val used = mutableSetOf<LocalDate>()
        return base.map { bh ->
            val observed = observedUKAvoidingClash(bh.date, used)
            used += observed
            bh.copy(date = observed)
        }.sortedBy { it.date }
    }

    private fun observedUKAvoidingClash(date: LocalDate, taken: Set<LocalDate>): LocalDate {
        fun nextWeekday(d: LocalDate): LocalDate {
            var x = d
            while (x.dayOfWeek == DayOfWeek.SATURDAY || x.dayOfWeek == DayOfWeek.SUNDAY) x = x.plusDays(1)
            return x
        }
        var d = if (date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY) nextWeekday(date) else date
        var tmp = d
        while (tmp in taken) tmp = tmp.plusDays(1)
        return tmp
    }

    private fun getGermanBankHolidays(year: Int): List<BankHoliday> {
        val easter = easterDate(year)
        return listOf(
            BankHoliday("New Year's Day", LocalDate.of(year, 1, 1)),
            BankHoliday("Good Friday", easter.minusDays(2)),
            BankHoliday("Easter Monday", easter.plusDays(1)),
            BankHoliday("Labour Day", LocalDate.of(year, 5, 1)),
            BankHoliday("Ascension Day", easter.plusDays(39)),
            BankHoliday("Whit Monday", easter.plusDays(50)),
            BankHoliday("German Unity Day", LocalDate.of(year, 10, 3)),
            BankHoliday("Christmas Day", LocalDate.of(year, 12, 25)),
            BankHoliday("Boxing Day", LocalDate.of(year, 12, 26))
        ).sortedBy { it.date }
    }

    private fun getFrenchBankHolidays(year: Int): List<BankHoliday> {
        val easter = easterDate(year)
        return listOf(
            BankHoliday("New Year's Day", LocalDate.of(year, 1, 1)),
            BankHoliday("Easter Monday", easter.plusDays(1)),
            BankHoliday("Labour Day", LocalDate.of(year, 5, 1)),
            BankHoliday("Victory in Europe Day", LocalDate.of(year, 5, 8)),
            BankHoliday("Ascension Day", easter.plusDays(39)),
            BankHoliday("Whit Monday", easter.plusDays(50)),
            BankHoliday("Bastille Day", LocalDate.of(year, 7, 14)),
            BankHoliday("Assumption Day", LocalDate.of(year, 8, 15)),
            BankHoliday("All Saints' Day", LocalDate.of(year, 11, 1)),
            BankHoliday("Armistice Day", LocalDate.of(year, 11, 11)),
            BankHoliday("Christmas Day", LocalDate.of(year, 12, 25))
        ).sortedBy { it.date }
    }

    private fun getItalianBankHolidays(year: Int): List<BankHoliday> {
        val easter = easterDate(year)
        return listOf(
            BankHoliday("New Year's Day", LocalDate.of(year, 1, 1)),
            BankHoliday("Epiphany", LocalDate.of(year, 1, 6)),
            BankHoliday("Easter Monday", easter.plusDays(1)),
            BankHoliday("Liberation Day", LocalDate.of(year, 4, 25)),
            BankHoliday("Labour Day", LocalDate.of(year, 5, 1)),
            BankHoliday("Republic Day", LocalDate.of(year, 6, 2)),
            BankHoliday("Assumption Day", LocalDate.of(year, 8, 15)),
            BankHoliday("All Saints' Day", LocalDate.of(year, 11, 1)),
            BankHoliday("Immaculate Conception", LocalDate.of(year, 12, 8)),
            BankHoliday("Christmas Day", LocalDate.of(year, 12, 25)),
            BankHoliday("St. Stephen's Day", LocalDate.of(year, 12, 26))
        ).sortedBy { it.date }
    }

    private fun getAustralianBankHolidays(year: Int): List<BankHoliday> {
        val used = mutableSetOf<LocalDate>()
        fun observedAU(date: LocalDate): LocalDate {
            var d = when (date.dayOfWeek) {
                DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> {
                    var x = date
                    while (x.dayOfWeek == DayOfWeek.SATURDAY || x.dayOfWeek == DayOfWeek.SUNDAY) x = x.plusDays(1)
                    x
                }
                else -> date
            }
            while (d in used) d = d.plusDays(1)
            used += d
            return d
        }
        val easter = easterDate(year)
        return listOf(
            BankHoliday("New Year's Day", observedAU(LocalDate.of(year, 1, 1))),
            BankHoliday("Australia Day", observedAU(LocalDate.of(year, 1, 26))),
            BankHoliday("Good Friday", easter.minusDays(2)),
            BankHoliday("Easter Monday", easter.plusDays(1)),
            BankHoliday("ANZAC Day", LocalDate.of(year, 4, 25)),
            BankHoliday("King's Birthday", nthWeekdayOfMonth(year, 6, 2, DayOfWeek.MONDAY)),
            BankHoliday("Christmas Day", observedAU(LocalDate.of(year, 12, 25))),
            BankHoliday("Boxing Day", observedAU(LocalDate.of(year, 12, 26)))
        ).sortedBy { it.date }
    }

    // -------------------- Helpers --------------------
    private fun easterDate(year: Int): LocalDate {
        // Butcher’s algorithm (Gregorian)
        val a = year % 19
        val b = year / 100
        val c = year % 100
        val d = b / 4
        val e = b % 4
        val f = (b + 8) / 25
        val g = (b - f + 1) / 3
        val h = (19 * a + b - d - g + 15) % 30
        val i = c / 4
        val k = c % 4
        val l = (32 + 2 * e + 2 * i - h - k) % 7
        val m = (a + 11 * h + 22 * l) / 451
        val month = (h + l - 7 * m + 114) / 31
        val day = ((h + l - 7 * m + 114) % 31) + 1
        return LocalDate.of(year, month, day)
    }

    private fun nthWeekdayOfMonth(year: Int, month: Int, nth: Int, dayOfWeek: DayOfWeek): LocalDate {
        val first = LocalDate.of(year, month, 1)
        return first.with(TemporalAdjusters.firstInMonth(dayOfWeek)).plusWeeks((nth - 1).toLong())
    }

    private fun lastWeekdayOfMonth(year: Int, month: Int, dayOfWeek: DayOfWeek): LocalDate {
        val first = LocalDate.of(year, month, 1)
        return first.with(TemporalAdjusters.lastInMonth(dayOfWeek))
    }
}
