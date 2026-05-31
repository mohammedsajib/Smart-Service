package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(db.dao())

    // 1. Language Toggle State (Defaults to true/Bangla)
    private val _isBangla = MutableStateFlow(true)
    val isBangla: StateFlow<Boolean> = _isBangla.asStateFlow()

    fun toggleLanguage() {
        _isBangla.value = !_isBangla.value
    }

    // 2. Active Logged In User State
    val currentUser: StateFlow<UserProfile?> = repository.currentUserState

    // 3. User Favorites State
    val favoriteIds: StateFlow<List<Int>> = currentUser
        .flatMapLatest { user ->
            if (user != null) {
                repository.getFavoriteIds(user.uid)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 4. Notices State
    val notices: StateFlow<List<Notice>> = repository.allNotices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 5. Raw Services
    val services: StateFlow<List<SmartService>> = repository.allServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 6. Filter & Search Query State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Combined filtered services logic
    fun getFilteredServices(categoryFilter: String? = null): Flow<List<SmartService>> {
        return combine(services, searchQuery) { list, query ->
            list.filter { service ->
                val matchesCategory = categoryFilter == null || service.category.equals(categoryFilter, ignoreCase = true)
                val matchesQuery = query.isEmpty() ||
                        service.titleBn.contains(query, ignoreCase = true) ||
                        service.titleEn.contains(query, ignoreCase = true) ||
                        service.subCategoryBn.contains(query, ignoreCase = true) ||
                        service.subCategoryEn.contains(query, ignoreCase = true) ||
                        service.descriptionBn.contains(query, ignoreCase = true) ||
                        service.descriptionEn.contains(query, ignoreCase = true)

                matchesCategory && matchesQuery
            }
        }
    }

    // 7. Operations - Notices (ADMIN ONLY)
    fun addNotice(titleBn: String, titleEn: String, contentBn: String, contentEn: String, isEmergency: Boolean) {
        viewModelScope.launch {
            val date = "31 May, 2026"
            repository.insertNotice(
                Notice(
                    titleBn = titleBn,
                    titleEn = titleEn,
                    contentBn = contentBn,
                    contentEn = contentEn,
                    dateString = date,
                    isEmergency = isEmergency
                )
            )
        }
    }

    fun deleteNotice(noticeId: Int) {
        viewModelScope.launch {
            repository.deleteNotice(noticeId)
        }
    }

    // 8. Operations - Services (ADMIN ONLY)
    fun addService(
        category: String,
        titleBn: String,
        titleEn: String,
        subCategoryBn: String,
        subCategoryEn: String,
        descriptionBn: String,
        descriptionEn: String,
        contactNo: String,
        webUrl: String,
        locationBn: String,
        locationEn: String,
        imageUrl: String = ""
    ) {
        viewModelScope.launch {
            repository.insertService(
                SmartService(
                    category = category,
                    titleBn = titleBn,
                    titleEn = titleEn,
                    subCategoryBn = subCategoryBn,
                    subCategoryEn = subCategoryEn,
                    descriptionBn = descriptionBn,
                    descriptionEn = descriptionEn,
                    contactNo = contactNo,
                    webUrl = webUrl,
                    imageUrl = imageUrl,
                    locationBn = locationBn,
                    locationEn = locationEn
                )
            )
        }
    }

    fun updateService(service: SmartService) {
        viewModelScope.launch {
            repository.updateService(service)
        }
    }

    fun deleteService(serviceId: Int) {
        viewModelScope.launch {
            repository.deleteService(serviceId)
        }
    }

    // 9. Operations - Favorites
    fun toggleFavorite(serviceId: Int) {
        val user = currentUser.value
        if (user == null) return
        viewModelScope.launch {
            val currentFavs = favoriteIds.value
            if (currentFavs.contains(serviceId)) {
                repository.removeFavorite(user.uid, serviceId)
            } else {
                repository.addFavorite(user.uid, serviceId)
            }
        }
    }

    // 10. Operations - Profile & Authentication
    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.loginUser(email, password)
            onResult(success)
        }
    }

    fun logout() {
        repository.logout()
    }

    fun updateProfile(fullName: String, phone: String, address: String, bloodGroup: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val updated = user.copy(
                fullName = fullName,
                phone = phone,
                address = address,
                bloodGroup = bloodGroup
            )
            repository.updateProfile(updated)
        }
    }

    fun upgradeToAdmin() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val updated = user.copy(role = "admin")
            repository.updateProfile(updated)
        }
    }
}
