package com.example.puppy.service // Perubahan: Package name

// Import model dari package com.example.puppy.model
import com.example.puppy.model.AuthResponse
import com.example.puppy.model.Dog // Perubahan: Cat -> Dog
import com.example.puppy.model.LoginRequest
import com.example.puppy.model.LoginResponse
// MidtransRequest dan MidtransResponse tidak diimpor karena fungsinya dihapus
import com.example.puppy.model.RegisterRequest
import com.example.puppy.model.StatusResponse
import com.example.puppy.model.UploadDogResponse // Perubahan: UploadResponse -> UploadDogResponse
import com.example.puppy.model.UserProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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

    @GET("users/statuses")
    suspend fun getStatuses(
        @Header("Authorization") token: String
    ): Response<List<StatusResponse>>


    @GET("users/puppyprofile") // <<-- PERUBAHAN DI SINI: Sesuaikan dengan backend
    suspend fun getDogs(
        @Header("Authorization") token: String
    ): List<Dog> // Pastikan tipe kembalian ini (List<Dog>) sesuai dengan apa yang dikirim backend puppyController.getPuppiesByUser

    @Multipart
    @POST("users/puppyprofile") // <<-- PERUBAHAN DI SINI: Sesuaikan dengan backend
    suspend fun uploadDog(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("username") username: RequestBody,
        @Part("birthDate") birthDate: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("breed") breed: RequestBody
    ): Response<UploadDogResponse> // Pastikan UploadDogResponse sesuai dengan respons dari puppyController.createPuppy


}