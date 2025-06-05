package com.example.puppy.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // Wildcard
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Pastikan ini diimpor
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.* // Wildcard
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
// import androidx.compose.ui.graphics.Color // Dihapus
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.puppy.R // Perubahan
import com.example.puppy.data.UserRepository // Perubahan
import com.example.puppy.service.RetrofitInstance // Perubahan
import com.example.puppy.service.TokenManager // Perubahan
import com.example.puppy.ui.theme.LexendFont // Menggunakan LexendFont dari theme
import com.example.puppy.view_model.DogProfileViewModel // Perubahan: ViewModel

// Data class untuk informasi dokter statis
data class DoctorInfo(
    val id: String, // ID unik jika diperlukan untuk navigasi atau key
    val name: String,
    val specialization: String,
    val experience: String,
    val price: String,
    val imageResId: Int // Menggunakan ID resource drawable untuk gambar dokter
)

// Daftar dokter statis
val staticDoctorList = listOf(
    DoctorInfo(
        id = "doc1",
        name = "Dr. Adinda S.",
        specialization = "General Veterinarian",
        experience = "5 Years of Experience",
        price = "75k",
        imageResId = R.drawable.doctor_placeholder_1 // Ganti dengan drawable Anda
    ),
    DoctorInfo(
        id = "doc2",
        name = "Dr. Budi Setiawan",
        specialization = "Veterinary Surgeon",
        experience = "8 Years of Experience",
        price = "120k",
        imageResId = R.drawable.doctor_placeholder_1 // Ganti dengan drawable Anda
    ),
    DoctorInfo(
        id = "doc3",
        name = "Dr. Citra Lestari",
        specialization = "Small Animal Specialist",
        experience = "3 Years of Experience",
        price = "60k",
        imageResId = R.drawable.doctor_placeholder_1 // Ganti dengan drawable Anda
    ),
    DoctorInfo(
        id = "doc4",
        name = "Dr. Dian Kusuma",
        specialization = "Veterinary Dermatologist",
        experience = "7 Years of Experience",
        price = "95k",
        imageResId = R.drawable.doctor_placeholder_1 // Ganti dengan drawable Anda
    )
)

@Composable
fun HealthScreen(navController: NavController, context: Context = LocalContext.current) {

    val dogViewModel = remember {
        val tokenManager = TokenManager(context)
        val repository = UserRepository(
            api = RetrofitInstance.userService,
            tokenManager = tokenManager,
            context = context
        )
        DogProfileViewModel(repository)
    }

    val dogResponse by dogViewModel.dogResult.observeAsState()

    LaunchedEffect(Unit) {
        dogViewModel.loadDogProfile()
    }

    val dog = dogResponse?.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 16.dp)
            ) {
                AsyncImage(
                    model = dog?.photoUrl,
                    contentDescription = dog?.name ?: "Dog Avatar",
                    placeholder = painterResource(id = R.drawable.ic_default_dog_avatar),
                    error = painterResource(id = R.drawable.ic_default_dog_avatar),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        dog?.name ?: "No Dog Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    dog?.breed?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "Favorite", tint = MaterialTheme.colorScheme.primary)
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Konsultasi Kesehatan Anjing",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rekomendasi Dokter",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Menggunakan daftar dokter statis
        items(staticDoctorList) { doctorInfo ->
            DoctorCard(
                navController = navController,
                doctorId = doctorInfo.id, // Menggunakan ID dokter
                doctorName = doctorInfo.name,
                specialization = doctorInfo.specialization,
                experience = doctorInfo.experience,
                price = doctorInfo.price,
                imageResId = doctorInfo.imageResId // Menggunakan ID resource drawable
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}


@Composable
fun DoctorCard(
    navController: NavController,
    doctorId: String, // Tambahkan doctorId untuk navigasi
    doctorName: String,
    specialization: String,
    experience: String,
    price: String,
    imageResId: Int // Menggunakan ID resource drawable
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { navController.navigate("bookhealth/$doctorId") } // Navigasi dengan ID dokter
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageResId), // Menggunakan ID resource drawable
            contentDescription = "Doctor $doctorName",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                doctorName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                specialization,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                experience,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        "Rp.",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            price.replace("k", "", ignoreCase = true),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (price.endsWith("k", ignoreCase = true)) {
                            Text(
                                "K",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
                            )
                        }
                    }
                }

                Button(
                    onClick = { navController.navigate("bookhealth/$doctorId") }, // Navigasi dengan ID dokter
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Book Now",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}