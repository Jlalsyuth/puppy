package com.example.puppy.model

import com.google.gson.annotations.SerializedName

data class CreateStatusRequest(
    val content: String
)

data class UserData( // UserData sekarang akan mereferensikan DogData
    val id: Int,
    val fullName: String,
    val email: String,
    @SerializedName("Dogs") // Perubahan di sini
    val dogs: List<DogData>? = null // Perubahan di sini
)

data class StatusResponse(
    val id: Int,
    val content: String,
    val userId: Int,
    val photoUrl: String?,
    val createdAt: String,
    val updatedAt: String,
    @SerializedName("User")
    val user: UserData?,
)

data class DogData( // Perubahan nama class
    val id: Int,
    val name: String,
    val username: String,
    val birthDate: String,
    val photoUrl: String,
    val gender: String,
    val breed: String
)