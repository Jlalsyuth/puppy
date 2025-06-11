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
    private val context: Context // context tidak digunakan, bisa dihapus jika tidak ada rencana penggunaan
) {
    suspend fun register(request: RegisterRequest): Response<AuthResponse> {
        return api.register(request)
    }

    suspend fun login(request: LoginRequest): Result<String> {
        try {
            // STEP 1: Panggil API Login untuk mendapatkan token
            val loginResponse = api.login(request)

            if (loginResponse.isSuccessful) {
                val token = loginResponse.body()?.token ?: return Result.failure(Exception("Token tidak ditemukan dari API"))

                // Setelah token didapat, simpan sementara untuk panggilan berikutnya
                tokenManager.saveToken(token)

                // STEP 2: Panggil API Get Profile untuk mendapatkan data user (termasuk ID)
                val profileResponse = api.getProfile("Bearer $token")
                if (profileResponse.isSuccessful) {
                    val userId = profileResponse.body()?.id ?: return Result.failure(Exception("User ID tidak ditemukan dari API profil"))

                    // Setelah User ID juga didapat, simpan secara permanen
                    tokenManager.saveUserId(userId.toString())

                    // Jika semua berhasil, kembalikan Result sukses
                    return Result.success(token)
                } else {
                    // Handle jika panggilan getProfile gagal
                    tokenManager.clearData() // Bersihkan token yang sempat tersimpan
                    return Result.failure(Exception("Gagal mengambil profil pengguna setelah login"))
                }
            } else {
                // Handle jika panggilan login gagal
                val errorBody = loginResponse.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).message
                } catch (e: Exception) {
                    "Login gagal"
                }
                return Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            return Result.failure(e)
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

    // --- FUNGSI YANG DIPERBAIKI DAN DITAMBAHKAN ---

    /**
     * DIPERBAIKI: Menggunakan token dari parameter, bukan mengambil lagi dari TokenManager.
     * ViewModel yang akan bertanggung jawab memberikan token.
     */
    suspend fun getStatuses(token: String): Response<List<StatusResponse>> {
        return api.getStatuses(token) // Langsung teruskan token dari parameter
    }

    /**
     * BARU: Fungsi untuk menghapus status.
     * Menyelesaikan error "Unresolved reference" di FeedViewModel.
     */
    suspend fun deleteStatus(token: String, postId: String): Response<Unit> {
        return api.deleteStatus(token, postId)
    }

    suspend fun updateStatus(
        token: String,
        postId: String,
        content: String,
        photoFile: File?
    ): Response<StatusResponse> {
        // Ubah string content menjadi RequestBody
        val contentPart = content.toRequestBody("text/plain".toMediaType())

        // Siapkan foto jika ada
        val photoPart = photoFile?.let {
            val requestFile = it.asRequestBody("image/*".toMediaType())
            MultipartBody.Part.createFormData("photo", it.name, requestFile)
        }

        return api.updateStatus("Bearer $token", postId, contentPart, photoPart)
    }


    // --- FUNGSI LAINNYA (TETAP SAMA) ---

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