package com.example.puppy.view // Perubahan: Package

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // Wildcard
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Menggunakan auto-mirrored
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VerifiedUser // Mengganti Verified dengan VerifiedUser atau Shield
import androidx.compose.material3.* // Wildcard
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
// import androidx.compose.ui.graphics.Color // Dihapus
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.puppy.R // Perubahan
import com.example.puppy.data.UserRepository // Perubahan
// import com.example.meowspace.model.MidtransRequest // Dihapus
import com.example.puppy.service.RetrofitInstance // Perubahan
import com.example.puppy.service.TokenManager // Perubahan
import com.example.puppy.view_model.DogProfileViewModel // Perubahan: ViewModel
// kotlinx.coroutines.CoroutineScope, Dispatchers, launch, withContext dihapus karena tidak ada lagi pemanggilan API di sini

@Composable
fun BookHealthScreen(navController: NavController, context: Context = LocalContext.current) {
    var selectedDogName by remember { mutableStateOf<String?>(null) } // Perubahan: selectedCat -> selectedDogName

    val dogViewModel = remember { // Perubahan: ViewModel
        val tokenManager = TokenManager(context)
        val repository = UserRepository(
            api = RetrofitInstance.userService,
            tokenManager = tokenManager,
            context = context
        )
        DogProfileViewModel(repository) // Perubahan
    }

    val dogResponse by dogViewModel.dogResult.observeAsState() // Perubahan

    LaunchedEffect(Unit) {
        dogViewModel.loadDogProfile() // Perubahan
    }

    val dog = dogResponse?.firstOrNull() // Mengambil profil anjing pengguna

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Perubahan: Warna latar
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp) // Padding bawah agar tombol tidak terlalu mepet
    ) {
        // Tombol Kembali di atas
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp) // Padding disesuaikan
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), // Warna dengan transparansi
                    shape = CircleShape
                )
                .size(40.dp) // Ukuran tombol
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface // Perubahan: Warna ikon
            )
        }

        // Detail Dokter Card (Data dokter masih hardcoded)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp), // Padding sekeliling kartu
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), // Warna kartu
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.doctor_placeholder_1), // PERHATIAN: Ganti dengan gambar dokter Anda
                        contentDescription = "Doctor Photo",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Dr. Dewi Rahma",
                            style = MaterialTheme.typography.titleLarge, // Style dari tema
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Veterinary Cardiologist",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary // Warna aksen
                        )
                        Text(
                            "2 Years of Experience",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Rp.", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "89k",
                            style = MaterialTheme.typography.headlineSmall, // Style dari tema
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                DoctorInfoRow(icon = Icons.Default.School, title = "Alumnus", detail = "Universitas Brawijaya, 2025")
                Spacer(modifier = Modifier.height(8.dp))
                DoctorInfoRow(icon = Icons.Default.Place, title = "Praktik di", detail = "Klinik Hewan PuppyCare, Malang") // Teks disesuaikan
                Spacer(modifier = Modifier.height(8.dp))
                DoctorInfoRow(icon = Icons.Default.VerifiedUser, title = "Nomor STR", detail = "656527623454254554", iconTint = MaterialTheme.colorScheme.tertiary) // Warna ikon STR
            }
        }

        // Pemilihan Anjing
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                "Choose Your Puppy", // Perubahan: Teks
                color = MaterialTheme.colorScheme.primary, // Warna dari tema
                style = MaterialTheme.typography.titleMedium, // Style dari tema
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (dog != null) {
                DogSelector( // Perubahan: Menggunakan DogSelector
                    name = dog.name,
                    photoUrl = dog.photoUrl, // Menggunakan URL foto
                    selected = selectedDogName == dog.name,
                    onClick = {
                        selectedDogName = if (selectedDogName == dog.name) null else dog.name
                    }
                )
            } else {
                Text(
                    "Add your dog's profile first to book.", // Perubahan: Teks
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f)) // Mendorong tombol ke bawah

        // Tombol Book Now
        Button(
            onClick = {
                if (selectedDogName != null) {
                    // Logika Pembayaran Midtrans Dihapus
                    Log.d("BookHealthScreen", "Book Now button clicked for dog: $selectedDogName. Payment logic removed/mocked.")
                    // Navigasi ke layar konfirmasi tiruan atau tampilkan pesan Toast/Snackbar
                    // Contoh navigasi ke route placeholder:
                    navController.navigate("booking_confirmation_mock/${dog?.name ?: "Your Dog"}") {
                        // Opsi popUpTo bisa disesuaikan
                    }
                } else {
                    // Tampilkan pesan bahwa anjing perlu dipilih
                    Log.d("BookHealthScreen", "Please select a dog first.")
                    // Mungkin tampilkan Snackbar: "Please select your dog."
                }
            },
            enabled = selectedDogName != null, // Tombol aktif jika anjing sudah dipilih
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp) // Padding untuk tombol
                .height(52.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Perubahan: Warna tombol
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), // Warna saat disabled
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // Warna teks saat disabled
            )
        ) {
            Text("Book Now (Mock)", style = MaterialTheme.typography.labelLarge) // Teks disesuaikan
        }
    }
}

@Composable
private fun DoctorInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, detail: String, iconTint: androidx.compose.ui.graphics.Color? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconTint ?: MaterialTheme.colorScheme.onSurfaceVariant // Warna ikon dari tema, atau custom jika diberikan
        )
        Spacer(modifier = Modifier.width(12.dp)) // Jarak lebih besar
        Column {
            Text(
                title,
                style = MaterialTheme.typography.labelLarge, // Style dari tema
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                detail,
                style = MaterialTheme.typography.bodyMedium, // Style dari tema
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}


@Composable
fun DogSelector(name: String, photoUrl: String?, selected: Boolean, onClick: () -> Unit) { // photoRes diubah ke photoUrl
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest // Perubahan warna
    val textColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Card( // Menggunakan Card untuk tampilan yang lebih baik
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "$name's Photo",
                placeholder = painterResource(id = R.drawable.ic_default_dog_avatar), // Placeholder
                error = painterResource(id = R.drawable.ic_default_dog_avatar), // Placeholder
                modifier = Modifier
                    .size(48.dp) // Ukuran disesuaikan
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                name,
                style = MaterialTheme.typography.titleMedium, // Style dari tema
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}