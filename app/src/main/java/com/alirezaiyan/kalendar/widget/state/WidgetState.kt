package com.alirezaiyan.kalendar.widget.state

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alirezaiyan.kalendar.data.Country
import java.time.LocalDate
import java.time.YearMonth

/**
 * Widget state management utilities.
 */
object WidgetState {
    
    val YEAR_MONTH = stringPreferencesKey("year_month")
    val SELECTED_EPOCH_DAY = longPreferencesKey("selected_epoch_day")
    val SELECTED_COUNTRY = stringPreferencesKey("selected_country")
    
    fun Preferences.readYearMonth(): YearMonth? = 
        this[YEAR_MONTH]?.let(YearMonth::parse)
    
    fun Preferences.readSelectedDay(): Long? = 
        this[SELECTED_EPOCH_DAY]
    
    fun Preferences.readSelectedCountry(): Country? = 
        this[SELECTED_COUNTRY]?.let { Country.fromCountryCode(it) }
    
    fun Long.toLocalDate(): LocalDate = LocalDate.ofEpochDay(this)
    
    fun LocalDate.toEpochDay(): Long = this.toEpochDay()
}
