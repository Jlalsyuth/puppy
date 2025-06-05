package com.example.puppy.model

data class UploadDogResponse( // Nama class bisa disesuaikan, misal UploadProfileResponse
    val message: String,
    val dog: Dog // Perubahan di sini
)

data class Dog( // Perubahan nama class
    val id: Int,
    val name: String,
    val username: String, // Sesuaikan jika perlu
    val birthDate: String,
    val gender: String,
    val breed: String,
    val photoUrl: String,
    val userId: Int,
    val createdAt: String,
    val updatedAt: String
)