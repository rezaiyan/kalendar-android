package com.alirezaiyan.kalendar.data

/**
 * Supported countries for bank holidays.
 */
enum class Country(
    val displayName: String,
    val countryCode: String
) {
    UNITED_STATES("United States", "US"),
    UNITED_KINGDOM("United Kingdom", "GB"),
    GERMANY("Germany", "DE"),
    FRANCE("France", "FR"),
    ITALY("Italy", "IT"),
    AUSTRALIA("Australia", "AU"),
    AUSTRIA("Austria", "AT"),
    IRAN("Iran", "IR");

    companion object {
        fun fromCountryCode(code: String): Country? {
            return Country.entries.find { it.countryCode == code }
        }
    }
}
