package com.alirezaiyan.kalendar.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Single source of truth for country selection across the app and widget.
 */
class CountryRepository(private val context: Context) {
    
    companion object {
        private val Context.countryDataStore: DataStore<Preferences> by preferencesDataStore("country_preferences")
        private val SELECTED_COUNTRY_KEY = stringPreferencesKey("selected_country")
    }
    
    /**
     * Flow of the currently selected country
     */
    val selectedCountry: Flow<Country> = context.countryDataStore.data.map { preferences ->
        val countryCode = preferences[SELECTED_COUNTRY_KEY] ?: Country.UNITED_STATES.countryCode
        Country.fromCountryCode(countryCode) ?: Country.UNITED_STATES
    }
    
    /**
     * Update the selected country
     */
    suspend fun setSelectedCountry(country: Country) {
        context.countryDataStore.edit { preferences ->
            preferences[SELECTED_COUNTRY_KEY] = country.countryCode
        }
    }
    
    /**
     * Get the current selected country synchronously
     */
    suspend fun getCurrentCountry(): Country {
        val preferences = context.countryDataStore.data.first()
        val countryCode = preferences[SELECTED_COUNTRY_KEY] ?: Country.UNITED_STATES.countryCode
        return Country.fromCountryCode(countryCode) ?: Country.UNITED_STATES
    }
}
