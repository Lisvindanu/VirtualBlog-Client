package com.virtualsblog.project.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.virtualsblog.project.data.local.database.BlogDatabase
import com.virtualsblog.project.data.local.entities.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var database: BlogDatabase
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            BlogDatabase::class.java
        ).allowMainThreadQueries().build()

        userDao = database.userDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertUser_andGetCurrentUser_returnsUser() = runTest {
        // Given
        val user = UserEntity(
            id = "user-123",
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            image = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            isCurrent = true
        )

        // When
        userDao.insertUser(user)

        // Then
        val currentUser = userDao.getCurrentUser().first()
        assertNotNull(currentUser)
        assertEquals("user-123", currentUser?.id)
        assertEquals("testuser", currentUser?.username)
        assertTrue(currentUser?.isCurrent == true)
    }

    @Test
    fun setCurrentUser_updatesCurrentUserFlag() = runTest {
        // Given
        val user1 = UserEntity(
            id = "user-1",
            username = "user1",
            fullname = "User One",
            email = "user1@example.com",
            image = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            isCurrent = false
        )

        val user2 = UserEntity(
            id = "user-2",
            username = "user2",
            fullname = "User Two",
            email = "user2@example.com",
            image = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            isCurrent = true
        )

        userDao.insertUser(user1)
        userDao.insertUser(user2)

        // When
        userDao.clearCurrentUser()
        userDao.setCurrentUser("user-1")

        // Then
        val currentUser = userDao.getCurrentUser().first()
        assertNotNull(currentUser)
        assertEquals("user-1", currentUser?.id)
        assertTrue(currentUser?.isCurrent == true)
    }

    @Test
    fun clearCurrentUser_removesCurrentUserFlag() = runTest {
        // Given
        val user = UserEntity(
            id = "user-123",
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            image = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            isCurrent = true
        )

        userDao.insertUser(user)

        // When
        userDao.clearCurrentUser()

        // Then
        val currentUser = userDao.getCurrentUser().first()
        assertNull(currentUser)
    }

    @Test
    fun updateUserProfile_updatesUserData() = runTest {
        // Given
        val user = UserEntity(
            id = "user-123",
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            image = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            isCurrent = true
        )

        userDao.insertUser(user)

        // When
        userDao.updateUserProfile(
            userId = "user-123",
            fullname = "Updated Test User",
            email = "updated@example.com",
            username = "updateduser",
            image = "https://example.com/image.jpg",
            updatedAt = "2024-01-02T00:00:00Z"
        )

        // Then
        val updatedUser = userDao.getCurrentUser().first()
        assertNotNull(updatedUser)
        assertEquals("Updated Test User", updatedUser?.fullname)
        assertEquals("updated@example.com", updatedUser?.email)
        assertEquals("updateduser", updatedUser?.username)
        assertEquals("https://example.com/image.jpg", updatedUser?.image)
        assertEquals("2024-01-02T00:00:00Z", updatedUser?.updatedAt)
    }

    @Test
    fun updateUserImage_updatesImageOnly() = runTest {
        // Given
        val user = UserEntity(
            id = "user-123",
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            image = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            isCurrent = true
        )

        userDao.insertUser(user)

        // When
        userDao.updateUserImage(
            userId = "user-123",
            imageUrl = "https://example.com/new-image.jpg",
            updatedAt = "2024-01-02T00:00:00Z"
        )

        // Then
        val updatedUser = userDao.getCurrentUser().first()
        assertNotNull(updatedUser)
        assertEquals("https://example.com/new-image.jpg", updatedUser?.image)
        assertEquals("2024-01-02T00:00:00Z", updatedUser?.updatedAt)
        // Pastikan data lain tidak berubah
        assertEquals("Test User", updatedUser?.fullname)
        assertEquals("test@example.com", updatedUser?.email)
    }

    @Test
    fun getUserByUsername_returnsCorrectUser() = runTest {
        // Given
        val user1 = UserEntity(
            id = "user-1",
            username = "user1",
            fullname = "User One",
            email = "user1@example.com",
            image = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            isCurrent = false
        )

        val user2 = UserEntity(
            id = "user-2",
            username = "user2",
            fullname = "User Two",
            email = "user2@example.com",
            image = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            isCurrent = false
        )

        userDao.insertUser(user1)
        userDao.insertUser(user2)

        // When
        val foundUser = userDao.getUserByUsername("user2")

        // Then
        assertNotNull(foundUser)
        assertEquals("user-2", foundUser?.id)
        assertEquals("user2", foundUser?.username)
        assertEquals("User Two", foundUser?.fullname)
    }

    @Test
    fun getUserByEmail_returnsCorrectUser() = runTest {
        // Given
        val user = UserEntity(
            id = "user-123",
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            image = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            isCurrent = false
        )

        userDao.insertUser(user)

        // When
        val foundUser = userDao.getUserByEmail("test@example.com")

        // Then
        assertNotNull(foundUser)
        assertEquals("user-123", foundUser?.id)
        assertEquals("testuser", foundUser?.username)
    }

    @Test
    fun deleteUser_removesUserFromDatabase() = runTest {
        // Given
        val user = UserEntity(
            id = "user-123",
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            image = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            isCurrent = true
        )

        userDao.insertUser(user)

        // When
        userDao.deleteUser("user-123")

        // Then
        val deletedUser = userDao.getUserById("user-123")
        assertNull(deletedUser)

        val currentUser = userDao.getCurrentUser().first()
        assertNull(currentUser)
    }

    @Test
    fun deleteAllUsers_removesAllUsers() = runTest {
        // Given
        val user1 = UserEntity(
            id = "user-1",
            username = "user1",
            fullname = "User One",
            email = "user1@example.com",
            image = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            isCurrent = false
        )

        val user2 = UserEntity(
            id = "user-2",
            username = "user2",
            fullname = "User Two",
            email = "user2@example.com",
            image = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            isCurrent = true
        )

        userDao.insertUser(user1)
        userDao.insertUser(user2)

        // When
        userDao.deleteAllUsers()

        // Then
        val user1After = userDao.getUserById("user-1")
        val user2After = userDao.getUserById("user-2")
        val currentUser = userDao.getCurrentUser().first()

        assertNull(user1After)
        assertNull(user2After)
        assertNull(currentUser)
    }
}