package com.example.puppy.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.puppy.data.UserRepository
import com.example.puppy.model.Dog
import kotlinx.coroutines.launch


class DogProfileViewModel(
    private val repository: UserRepository
): ViewModel() {

    private val _dogResult = MutableLiveData<List<Dog>?>()
    val dogResult: LiveData<List<Dog>?> = _dogResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadDogProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val dogs = repository.getDogs()
                _dogResult.value = dogs
                Log.d("DogProfileViewModel", "Dogs: $dogs")
            } catch (e: Exception) {
                Log.e("DogProfileViewModel", "Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}