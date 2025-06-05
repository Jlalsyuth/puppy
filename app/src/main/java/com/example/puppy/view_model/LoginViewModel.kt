package com.example.puppy.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.puppy.data.UserRepository
import com.example.puppy.model.LoginRequest
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: UserRepository
) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var passwordVisible by mutableStateOf(false)

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun login(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank() || password.length < 8 || !isValidEmail(email)) {
            errorMessage = when {
                email.isBlank() || password.isBlank() -> "Please fill in all fields correctly"
                !isValidEmail(email) -> "Invalid email format"
                password.length < 8 -> "Password must be at least 8 characters"
                else -> "An error occurred"
            }
            return
        }

        isLoading = true

        viewModelScope.launch {
            try {
                val request = LoginRequest(email = email, password = password)
                val result = repository.login(request)

                result.onSuccess {
                    println("Token: $it")
                    onSuccess()
                }.onFailure {
                    errorMessage = it.message ?: "Login gagal"
                }
            } catch (e: Exception) {
                errorMessage = "Login gagal: ${e.message ?: "Terjadi kesalahan"}"
            } finally {
                isLoading = false
            }
        }
    }
}