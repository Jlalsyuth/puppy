package com.example.puppy.view_model


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.puppy.data.UserRepository
import com.example.puppy.model.UploadDogResponse
import kotlinx.coroutines.launch
import java.io.File

class UploadDogViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _uploadResult = MutableLiveData<Result<UploadDogResponse>>()
    val uploadResult: LiveData<Result<UploadDogResponse>> = _uploadResult

    val name = MutableLiveData("")
    val username = MutableLiveData("")
    val birthDate = MutableLiveData("")
    val gender = MutableLiveData("male")
    val breed = MutableLiveData("")
    val photoFile = MutableLiveData<File?>(null)

    fun uploadDog() {
        val file = photoFile.value ?: run {
            Log.e("UploadDogVM", "Photo file is null, cannot upload.")
            _uploadResult.postValue(Result.failure(Exception("Foto tidak boleh kosong.")))
            return
        }
        val nameValue = name.value ?: ""
        val usernameValue = username.value ?: ""
        val birthDateValue = birthDate.value ?: ""
        val genderValue = gender.value ?: "male"
        val breedValue = breed.value ?: ""

        if (nameValue.isBlank() || birthDateValue.isBlank() || breedValue.isBlank()) {
            Log.e("UploadDogVM", "Required fields are blank.")
            _uploadResult.postValue(Result.failure(Exception("Nama, tanggal lahir, dan ras tidak boleh kosong.")))
            return
        }

        Log.d("UploadDogVM", "Name: $nameValue")
        Log.d("UploadDogVM", "Username: $usernameValue")
        Log.d("UploadDogVM", "Birth Date: $birthDateValue")
        Log.d("UploadDogVM", "Gender: $genderValue")
        Log.d("UploadDogVM", "Breed: $breedValue")
        Log.d("UploadDogVM", "Photo exists: ${file.exists()}, size: ${file.length()}")

        viewModelScope.launch {
            try {
                val response = repository.uploadDog(
                    photo = file,
                    name = nameValue,
                    username = usernameValue,
                    birthDate = birthDateValue,
                    gender = genderValue,
                    breed = breedValue
                )
                if (response.isSuccessful && response.body() != null) {
                    _uploadResult.postValue(Result.success(response.body()!!))
                    Log.d("UploadDogVM", "Upload successful: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string() ?: response.message()
                    Log.e("UploadDogVM", "Upload failed: Code: ${response.code()}, Body: $errorBody")
                    _uploadResult.postValue(Result.failure(Exception("Upload gagal: ($errorBody)")))
                }
            } catch (e: Exception) {
                Log.e("UploadDogVM", "Exception during upload: ${e.message}", e)
                _uploadResult.postValue(Result.failure(e))
            }
        }
    }
}