package com.example.puppy.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.puppy.data.UserRepository
import com.example.puppy.model.StatusResponse
import com.example.puppy.service.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File


// ... (Sealed class FeedUiState tetap sama)
sealed class FeedUiState {
    data class Success(val posts: List<StatusResponse>) : FeedUiState()
    data class Error(val message: String) : FeedUiState()
    object Loading : FeedUiState()
}

class FeedViewModel(
    private val repository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val _loggedInUserId = MutableStateFlow<String?>(null)
    val loggedInUserId: StateFlow<String?> = _loggedInUserId.asStateFlow()

    private val _toastEvent = MutableLiveData<String>()
    val toastEvent: LiveData<String> = _toastEvent

    init {
        fetchLoggedInUserId()
        loadFeed()
    }

    fun loadFeed() {
        // ... (fungsi loadFeed tidak berubah)
        viewModelScope.launch {
            _uiState.value = FeedUiState.Loading
            try {
                val token = tokenManager.getToken() ?: run {
                    _uiState.value = FeedUiState.Error("Sesi Anda telah berakhir. Silakan login kembali.")
                    return@launch
                }
                val response = repository.getStatuses("Bearer $token")
                if (response.isSuccessful) {
                    _uiState.value = FeedUiState.Success(response.body() ?: emptyList())
                } else {
                    _uiState.value = FeedUiState.Error("Gagal memuat feed: ${response.message()}")
                }
            } catch (e: Exception) {
                _uiState.value = FeedUiState.Error("Terjadi kesalahan: ${e.message}")
            }
        }
    }

    private fun fetchLoggedInUserId() {
        viewModelScope.launch {
            _loggedInUserId.value = tokenManager.getUserId()
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken() ?: run {
                    _toastEvent.value = "Aksi gagal: Sesi tidak valid."
                    return@launch
                }
                val response = repository.deleteStatus("Bearer $token", postId)
                if (response.isSuccessful) {
                    _toastEvent.value = "Status berhasil dihapus"
                    loadFeed() // Muat ulang feed
                } else {
                    _toastEvent.value = "Gagal menghapus status: ${response.message()}"
                }
            } catch (e: Exception) {
                _toastEvent.value = "Gagal menghapus status: ${e.message}"
            }
        }
    }

    /**
     * BARU: FUNGSI UNTUK UPDATE STATUS
     * Dipanggil dari UI (misal, setelah user menekan tombol 'Simpan' di dialog edit)
     */
    fun updatePost(postId: String, newContent: String, newPhotoFile: File?) {
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken() ?: run {
                    _toastEvent.value = "Aksi gagal: Sesi tidak valid."
                    return@launch
                }

                _toastEvent.value = "Memperbarui status..." // Memberi feedback ke user

                val response = repository.updateStatus(token, postId, newContent, newPhotoFile)
                if (response.isSuccessful) {
                    _toastEvent.value = "Status berhasil diperbarui"
                    // Cara 1: Muat ulang seluruh feed
                    loadFeed()

                    // Cara 2 (Lebih efisien): Perbarui item secara lokal
                    // Anda bisa memperbarui state _uiState secara manual untuk mengganti
                    // item yang lama dengan item yang baru dari response.body()
                    // Ini menghindari panggilan jaringan lagi.
                } else {
                    _toastEvent.value = "Gagal memperbarui status: ${response.message()}"
                }
            } catch (e: Exception) {
                _toastEvent.value = "Gagal memperbarui status: ${e.message}"
                Log.e("FeedViewModel", "Update failed", e)
            }
        }
    }
}
