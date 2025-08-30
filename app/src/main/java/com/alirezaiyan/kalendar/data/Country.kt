package com.alirezaiyan.kalendar.data

/**
 * Supported countries for bank holidays.
 */

enum class CalendarType { Gregorian, Solar }
enum class Country(
    val displayName: String,
    val countryCode: String,
    val calendarType: CalendarType,
) {
    UNITED_STATES("United States", "US", CalendarType.Gregorian),
    UNITED_KINGDOM("United Kingdom", "GB", CalendarType.Gregorian),
    GERMANY("Germany", "DE", CalendarType.Gregorian),
    FRANCE("France", "FR", CalendarType.Gregorian),
    ITALY("Italy", "IT", CalendarType.Gregorian),
    AUSTRALIA("Australia", "AU", CalendarType.Gregorian),
    AUSTRIA("Austria", "AT", CalendarType.Gregorian),
    IRAN("Iran", "IR", CalendarType.Solar);

    companion object {
        fun fromCountryCode(code: String): Country? {
            return Country.entries.find { it.countryCode == code }
        }
    }
}
