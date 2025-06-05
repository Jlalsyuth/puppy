package com.example.puppy.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.puppy.data.UserRepository
import com.example.puppy.model.StatusResponse
import kotlinx.coroutines.launch
import java.io.File

class PostFeedViewModel(
    private val repository: UserRepository
) : ViewModel() {
    private val _postResult = MutableLiveData<StatusResponse?>()
    val postResult: LiveData<StatusResponse?> = _postResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    var selectedImageFile: File? = null

    fun postStatus(content: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.postStatus(content, selectedImageFile)
                if (response.isSuccessful) {
                    _postResult.value = response.body()
                    Log.d("PostFeedViewModel", "Status created: ${response.body()?.content}")
                } else {
                    Log.e("PostFeedViewModel", "Gagal: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("PostFeedViewModel", "Exception: ${e.message}")
            }
            _isLoading.value = false
        }
    }
}