package com.alirezaiyan.kalendar.calendar

import com.alirezaiyan.kalendar.data.Country
import java.util.Locale

/**
 * Factory for creating calendar systems
 * Follows Factory pattern and Single Responsibility Principle
 */
object CalendarFactory {
    
    /**
     * Create a calendar system based on the country
     * @param country The country to determine calendar system
     * @param locale Optional locale override
     * @return Appropriate calendar system implementation
     */
    fun createCalendarSystem(country: Country, locale: Locale? = null): CalendarSystem {
        return when (country) {
            Country.IRAN -> {
                val iranianLocale = locale ?: Locale("fa", "IR")
                SolarHijriCalendarSystem(iranianLocale)
            }
            else -> {
                val defaultLocale = locale ?: Locale.getDefault()
                GregorianCalendarSystem(defaultLocale)
            }
        }
    }
    
    /**
     * Get the appropriate locale for a country
     * @param country The country
     * @return Locale for the country
     */
    fun getLocaleForCountry(country: Country): Locale {
        return when (country) {
            Country.IRAN -> Locale("fa", "IR")
            Country.GERMANY -> Locale("de", "DE")
            Country.FRANCE -> Locale("fr", "FR")
            Country.UNITED_KINGDOM -> Locale("en", "GB")
            Country.ITALY -> Locale("it", "IT")

            Country.UNITED_STATES -> Locale("en", "US")
            Country.AUSTRALIA -> Locale("en", "AU")
            Country.AUSTRIA -> Locale("de", "AT")
        }
    }
}
