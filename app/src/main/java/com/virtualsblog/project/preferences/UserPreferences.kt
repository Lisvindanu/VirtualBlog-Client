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
    private val fullnameKey = stringPreferencesKey(Constants.PREF_FULLNAME)
    private val emailKey = stringPreferencesKey(Constants.PREF_EMAIL)
    private val userImageKey = stringPreferencesKey(Constants.PREF_USER_IMAGE)

    // Flow untuk observe token
    val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[accessTokenKey]
    }

    // Flow untuk observe user data lengkap
    val userData: Flow<UserData> = dataStore.data.map { preferences ->
        UserData(
            accessToken = preferences[accessTokenKey],
            userId = preferences[userIdKey],
            username = preferences[usernameKey],
            fullname = preferences[fullnameKey],
            email = preferences[emailKey],
            image = preferences[userImageKey]
        )
    }

    // Check if user is logged in
    val isLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        !preferences[accessTokenKey].isNullOrEmpty()
    }

    // Save user session dengan data lengkap
    suspend fun saveUserSession(
        accessToken: String,
        userId: String,
        username: String,
        fullname: String,
        email: String,
        image: String?
    ) {
        dataStore.edit { preferences ->
            preferences[accessTokenKey] = accessToken
            preferences[userIdKey] = userId
            preferences[usernameKey] = username
            preferences[fullnameKey] = fullname
            preferences[emailKey] = email
            if (image != null) {
                preferences[userImageKey] = image
            } else {
                preferences.remove(userImageKey)
            }
        }
    }

    // Update profile data
    suspend fun updateProfile(
        username: String,
        fullname: String,
        email: String,
        image: String?
    ) {
        dataStore.edit { preferences ->
            preferences[usernameKey] = username
            preferences[fullnameKey] = fullname
            preferences[emailKey] = email
            if (image != null) {
                preferences[userImageKey] = image
            } else {
                preferences.remove(userImageKey)
            }
        }
    }

    // Update hanya image
    suspend fun updateImage(image: String?) {
        dataStore.edit { preferences ->
            if (image != null) {
                preferences[userImageKey] = image
            } else {
                preferences.remove(userImageKey)
            }
        }
    }

    // Clear user session (logout)
    suspend fun clearUserSession() {
        dataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
            preferences.remove(userIdKey)
            preferences.remove(usernameKey)
            preferences.remove(fullnameKey)
            preferences.remove(emailKey)
            preferences.remove(userImageKey)
        }
    }

    // Get access token untuk immediate use
    suspend fun getAccessToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[accessTokenKey]
        }.first()
    }

    // Get current user ID
    suspend fun getCurrentUserId(): String? {
        return dataStore.data.map { preferences ->
            preferences[userIdKey]
        }.first()
    }

    data class UserData(
        val accessToken: String?,
        val userId: String?,
        val username: String?,
        val fullname: String?,
        val email: String?,
        val image: String?
    )
}