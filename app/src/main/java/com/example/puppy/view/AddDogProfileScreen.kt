package com.example.puppy.view

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
// import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.puppy.data.UserRepository
import com.example.puppy.service.RetrofitInstance
import com.example.puppy.service.TokenManager
import com.example.puppy.view_model.UploadDogViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDogProfile(navController: NavController, context: Context = LocalContext.current) {

    val viewModel = remember {
        val tokenManager = TokenManager(context)
        val repository = UserRepository(
            api = RetrofitInstance.userService,
            tokenManager = tokenManager,
            context = context
        )
        UploadDogViewModel(repository)
    }

    val name by viewModel.name.observeAsState("")
    val username by viewModel.username.observeAsState("")
    val birthDate by viewModel.birthDate.observeAsState("")
    val gender by viewModel.gender.observeAsState("male")
    val breed by viewModel.breed.observeAsState("")
    val photoFile by viewModel.photoFile.observeAsState(null)
    val uploadState by viewModel.uploadResult.observeAsState()
    val errorMessage = remember { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(uploadState) {
        uploadState?.let { result ->
            result
                .onSuccess {
                    navController.navigate("dogprofile") {
                        popUpTo("adddogprofile") { inclusive = true }
                    }
                }
                .onFailure {
                    Log.e("UploadDog", "Upload gagal", it)
                    errorMessage.value = it.localizedMessage ?: "Unknown error during upload"
                }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val file = uriToFile(context, it)
            viewModel.photoFile.value = file
        }
    }

    val datePickerState = rememberDatePickerState()
    val showDatePicker = remember { mutableStateOf(false) }

    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(Date(millis))
                        viewModel.birthDate.value = formatted
                        showDatePicker.value = false
                    }
                }) {
                    Text("OK", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = { // Tambahkan dismiss button
                TextButton(onClick = { showDatePicker.value = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.primary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { launcher.launch("image/*") }
        ) {
            if (photoFile != null) {
                Image(
                    painter = rememberAsyncImagePainter(photoFile!!.toUri()),
                    contentDescription = "Dog Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Pets, // Atau ikon kamera/tambah foto
                    contentDescription = "Add Dog Photo Icon",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(60.dp)
                )
            }
            // Tombol tambah kecil di pojok (jika masih diinginkan, styling perlu disesuaikan)
            // Box(modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp).background(MaterialTheme.colorScheme.primary, CircleShape).padding(4.dp)){
            //    Icon(Icons.Default.Add, contentDescription = "Add Image", tint = MaterialTheme.colorScheme.onPrimary)
            // }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { viewModel.name.value = it },
            label = { Text("Dog's Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { viewModel.username.value = it },
            label = { Text("Username (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    showDatePicker.value = true
                }
        ) {
            OutlinedTextField(
                value = birthDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Birthday") },
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Select Date", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {},
                enabled = false,
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f),
                    disabledIndicatorColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = breed,
            onValueChange = { viewModel.breed.value = it },
            label = { Text("Breed (e.g., Golden Retriever)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Dog's Gender", modifier = Modifier.align(Alignment.Start), style = MaterialTheme.typography.titleSmall) // Perubahan teks & style
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            GenderButton("Male", gender == "male", ) {
                viewModel.gender.value = "male"
            }
            GenderButton("Female", gender == "female") {
                viewModel.gender.value = "female"
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage.value.isNotEmpty()) {
            Text(
                text = errorMessage.value,
                color = MaterialTheme.colorScheme.error, // Perubahan: Warna error
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = { viewModel.uploadDog() }, // Perubahan: Memanggil uploadDog()
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp), // Konsistensi bentuk
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Perubahan
                contentColor = MaterialTheme.colorScheme.onPrimary // Perubahan
            )
        ) {
            Text("Add Dog Profile", style = MaterialTheme.typography.titleMedium, color = Color.White) // Perubahan: Teks tombol
        }
        Spacer(modifier = Modifier.height(16.dp)) // Spacer di akhir untuk padding
    }
}

fun uriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    // Membuat nama file unik dengan timestamp untuk menghindari konflik
    val fileName = "upload_${System.currentTimeMillis()}.jpg"
    val file = File(context.cacheDir, fileName)
    inputStream?.use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return file
}

@Composable
fun RowScope.GenderButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.weight(1f), // Agar tombol sama lebar
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, // Perubahan
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant // Perubahan
        )
    ) {
        Text(text = text)
    }
}