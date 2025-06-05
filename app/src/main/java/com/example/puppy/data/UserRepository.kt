package com.example.puppy.data

import android.content.Context
import com.example.puppy.model.AuthResponse
import com.example.puppy.model.Dog
import com.example.puppy.model.ErrorResponse
import com.example.puppy.model.LoginRequest
import com.example.puppy.model.RegisterRequest
import com.example.puppy.model.StatusResponse
import com.example.puppy.model.UploadDogResponse
import com.example.puppy.model.UserProfileResponse
import com.example.puppy.service.TokenManager
import com.example.puppy.service.UserService
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

class UserRepository(
    private val api: UserService,
    private val tokenManager: TokenManager,
    private val context: Context
) {
    suspend fun register(request: RegisterRequest): Response<AuthResponse> {
        return api.register(request)
    }

    suspend fun login(request: LoginRequest): Result<String> {
        return try {
            val response = api.login(request)
            if (response.isSuccessful) {
                val token = response.body()?.token ?: return Result.failure(Exception("No token found"))
                tokenManager.saveToken(token)
                Result.success(token)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).message
                } catch (e: Exception) {
                    "Login failed"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(): Response<UserProfileResponse> {
        val token = tokenManager.getToken()
            ?: throw IllegalStateException("Token is null")
        return api.getProfile("Bearer $token")
    }

    suspend fun postStatus(content: String, photoFile: File?): Response<StatusResponse> {
        val token = tokenManager.getToken()
            ?: throw IllegalStateException("Token is null")
        val contentPart = content.toRequestBody("text/plain".toMediaType())
        val photoPart = photoFile?.let {
            val requestFile = it.asRequestBody("image/*".toMediaType())
            MultipartBody.Part.createFormData("photo", it.name, requestFile)
        }
        return api.postStatus(
            token = "Bearer $token",
            content = contentPart,
            photo = photoPart
        )
    }

    suspend fun getStatuses(): Response<List<StatusResponse>> {
        val token = tokenManager.getToken()
            ?: throw IllegalStateException("Token is null")
        return api.getStatuses("Bearer $token")
    }

    suspend fun getDogs(): List<Dog> {
        val token = tokenManager.getToken()
            ?: throw IllegalStateException("Token is null")
        return api.getDogs("Bearer $token")
    }

    suspend fun uploadDog(
        photo: File,
        name: String,
        username: String,
        birthDate: String,
        gender: String,
        breed: String
    ): Response<UploadDogResponse> {
        val token = tokenManager.getToken()
            ?: throw IllegalStateException("Token is null")
        val requestFile = photo.asRequestBody("image/*".toMediaType())
        val photoPart = MultipartBody.Part.createFormData("photo", photo.name, requestFile)

        return api.uploadDog(
            token = "Bearer $token",
            photo = photoPart,
            name = name.toRequestBody("text/plain".toMediaType()),
            username = username.toRequestBody("text/plain".toMediaType()),
            birthDate = birthDate.toRequestBody("text/plain".toMediaType()),
            gender = gender.toRequestBody("text/plain".toMediaType()),
            breed = breed.toRequestBody("text/plain".toMediaType())
        )
    }
}