package com.example.puppy.service // Perubahan: Package name


import com.example.puppy.model.AuthResponse
import com.example.puppy.model.Dog
import com.example.puppy.model.LoginRequest
import com.example.puppy.model.LoginResponse
import com.example.puppy.model.RegisterRequest
import com.example.puppy.model.StatusResponse
import com.example.puppy.model.UploadDogResponse // Perubahan: UploadResponse -> UploadDogResponse
import com.example.puppy.model.UserProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Part
import retrofit2.http.Path

interface UserService {
    @POST("/users/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("/users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("/users/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<UserProfileResponse>

    @Multipart
    @POST("users/statuses")
    suspend fun postStatus(
        @Header("Authorization") token: String,
        @Part("content") content: RequestBody,
        @Part photo: MultipartBody.Part? = null
    ): Response<StatusResponse>

    @DELETE("users/statuses/{id}")
    suspend fun deleteStatus(
        @Header("Authorization") token: String,
        @Path("id") postId: String
    ): Response<Unit>


    /**
     * BARU: UPDATE STATUS
     * Sesuai dengan backend: PUT /statuses/{id}
     * Mengirim data sebagai multipart karena ada kemungkinan foto diubah.
     */
    @Multipart
    @PUT("users/statuses/{id}")
    suspend fun updateStatus(
        @Header("Authorization") token: String,
        @Path("id") postId: String,
        @Part("content") content: RequestBody,
        @Part photo: MultipartBody.Part? // Foto bersifat opsional
    ): Response<StatusResponse> // Backend mengembalikan objek status yang diperbarui
    @GET("users/statuses")
    suspend fun getStatuses(
        @Header("Authorization") token: String
    ): Response<List<StatusResponse>>


    @GET("users/puppyprofile") // <<-- PERUBAHAN DI SINI: Sesuaikan dengan backend
    suspend fun getDogs(
        @Header("Authorization") token: String
    ): List<Dog> // Pastikan tipe kembalian ini (List<Dog>) sesuai dengan apa yang dikirim backend puppyController.getPuppiesByUser

    @Multipart
    @POST("puppyprofile") // <-- PERBAIKAN: Hapus awalan "/users/"
    suspend fun uploadDog(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("username") username: RequestBody,
        @Part("birthDate") birthDate: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("breed") breed: RequestBody
    ): Response<UploadDogResponse>


}