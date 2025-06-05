package com.example.puppy.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.puppy.data.UserRepository
import com.example.puppy.model.UserProfileResponse
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfileResponse?>()
    val userProfile: LiveData<UserProfileResponse?> = _userProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getUserProfile()
                if (response.isSuccessful) {
                    _userProfile.value = response.body()
                } else {
                    Log.e("ProfileViewModel", "Failed with code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}