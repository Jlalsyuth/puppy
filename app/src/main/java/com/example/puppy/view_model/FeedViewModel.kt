package com.example.puppy.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.puppy.data.UserRepository
import com.example.puppy.service.TokenManager
import com.example.puppy.model.StatusResponse
import kotlinx.coroutines.launch

class FeedViewModel(
    private val repository: UserRepository,
    private val tokenManager: TokenManager // Tambahkan TokenManager
) : ViewModel() {

    private val _postResult = MutableLiveData<List<StatusResponse>?>()
    val postResult: LiveData<List<StatusResponse>?> = _postResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loggedInUserId = MutableStateFlow<Int?>(null) // Menggunakan StateFlow
    val loggedInUserId: StateFlow<Int?> = _loggedInUserId.asStateFlow()

    init {
        fetchLoggedInUserId()
        loadFeed()
    }

    private fun fetchLoggedInUserId() {
        viewModelScope.launch {
            // Implementasi Anda untuk mendapatkan userId dari tokenManager
            // _loggedInUserId.value = tokenManager.getUserId() // Contoh
        }
    }

    fun loadFeed() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getStatuses()
                if (response.isSuccessful) {
                    _postResult.value = response.body()
                    val body = response.body()
                    Log.d("FeedViewModel", "Statuses: $body")
                } else {
                    Log.e("FeedViewModel", "Failed with code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}