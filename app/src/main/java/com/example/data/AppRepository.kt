package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppRepository(private val dao: AppDao) {

    // Active logged-in user session state
    private val _currentUserState = MutableStateFlow<UserProfile?>(null)
    val currentUserState: StateFlow<UserProfile?> = _currentUserState.asStateFlow()

    // 1. Notices
    val allNotices: Flow<List<Notice>> = dao.getAllNotices()

    suspend fun insertNotice(notice: Notice) = dao.insertNotice(notice)
    suspend fun updateNotice(notice: Notice) = dao.updateNotice(notice)
    suspend fun deleteNotice(id: Int) = dao.deleteNoticeById(id)

    // 2. Services
    val allServices: Flow<List<SmartService>> = dao.getAllServices()

    fun getServicesByCategory(category: String): Flow<List<SmartService>> {
        return dao.getServicesByCategory(category)
    }

    suspend fun insertService(service: SmartService) = dao.insertService(service)
    suspend fun updateService(service: SmartService) = dao.updateService(service)
    suspend fun deleteService(id: Int) = dao.deleteServiceById(id)

    // 3. User & Authentication simulation
    suspend fun loginUser(email: String, password: String): Boolean {
        // Since we are offline-first, if email matches 'admin@smart.com', redirect to prefilled admin
        // If email matches user@smart.com, login to prefilled user, or sign up any entry instantly!
        val existing = dao.getUserByEmail(email)
        return if (existing != null) {
            _currentUserState.value = existing
            true
        } else {
            // Self-register brand new emails instantly to keep it frictionless!
            val randomUid = "uid_" + email.hashCode().toString()
            val newUser = UserProfile(
                uid = randomUid,
                fullName = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = email,
                phone = "017XXXXXXXX",
                address = "Lalmonirhat",
                role = if (email.startsWith("admin")) "admin" else "user"
            )
            dao.insertUserProfile(newUser)
            _currentUserState.value = newUser
            true
        }
    }

    fun logout() {
        _currentUserState.value = null
    }

    fun getUserProfile(uid: String): Flow<UserProfile?> = dao.getUserProfileFlow(uid)

    suspend fun updateProfile(profile: UserProfile) {
        dao.updateUserProfile(profile)
        // If the updated profile is the active user, refresh session
        if (_currentUserState.value?.uid == profile.uid) {
            _currentUserState.value = profile
        }
    }

    // 4. Favorites
    fun getFavoriteIds(userId: String): Flow<List<Int>> = dao.getFavoriteIds(userId)

    suspend fun addFavorite(userId: String, serviceId: Int) {
        dao.addFavorite(Favorite(userId, serviceId))
    }

    suspend fun removeFavorite(userId: String, serviceId: Int) {
        dao.removeFavorite(userId, serviceId)
    }
}
