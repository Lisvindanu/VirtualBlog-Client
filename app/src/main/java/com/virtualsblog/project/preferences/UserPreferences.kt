package com.virtualsblog.project.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.virtualsblog.project.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val accessTokenKey = stringPreferencesKey(Constants.PREF_ACCESS_TOKEN)
    private val userIdKey = stringPreferencesKey(Constants.PREF_USER_ID)
    private val usernameKey = stringPreferencesKey(Constants.PREF_USERNAME)

    // Flow untuk observe token
    val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[accessTokenKey]
    }

    // Flow untuk observe user data
    val userData: Flow<Triple<String?, String?, String?>> = dataStore.data.map { preferences ->
        Triple(
            preferences[accessTokenKey],
            preferences[userIdKey],
            preferences[usernameKey]
        )
    }

    // Check if user is logged in
    val isLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        !preferences[accessTokenKey].isNullOrEmpty()
    }

    // Save user session
    suspend fun saveUserSession(
        accessToken: String,
        userId: String,
        username: String
    ) {
        dataStore.edit { preferences ->
            preferences[accessTokenKey] = accessToken
            preferences[userIdKey] = userId
            preferences[usernameKey] = username
        }
    }

    // Update username
    suspend fun updateUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[usernameKey] = username
        }
    }

    // Clear user session (logout)
    suspend fun clearUserSession() {
        dataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
            preferences.remove(userIdKey)
            preferences.remove(usernameKey)
        }
    }

    // Get access token untuk immediate use
    suspend fun getAccessToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[accessTokenKey]
        }.first()
    }
}