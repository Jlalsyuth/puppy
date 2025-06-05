package com.example.puppy.view // Perubahan: Package

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // Wildcard
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight // Menggunakan auto-mirrored
import androidx.compose.material3.* // Wildcard
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
// import androidx.compose.ui.graphics.Color // Dihapus jika semua dari tema
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage // Untuk memuat gambar dari URL jika userProfile punya photoUrl
import com.example.puppy.R // Perubahan
import com.example.puppy.data.UserRepository // Perubahan
import com.example.puppy.service.RetrofitInstance // Perubahan
import com.example.puppy.service.TokenManager // Perubahan
import com.example.puppy.view_model.ProfileViewModel // Perubahan

@Composable
fun ProfileScreen(navController: NavController, context: Context = LocalContext.current) { // Default context

    val viewModel = remember {
        val tokenManager = TokenManager(context) // Dari com.example.puppy
        val repository = UserRepository( // Dari com.example.puppy
            api = RetrofitInstance.userService, // Dari com.example.puppy
            tokenManager = tokenManager,
            context = context
        )
        ProfileViewModel(repository)
    }

    val userProfile by viewModel.userProfile.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(true)

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    val userName = if (isLoading) "Loading..." else userProfile?.fullName ?: "Guest"
    val userEmail = userProfile?.email // Ambil email jika ada, untuk ditampilkan atau sbg alt text
    val userRole = "Regular" // Tetap hardcoded sesuai asli

    // Idealnya userImage diambil dari userProfile?.photoUrl jika ada
    // Untuk sekarang, kita gunakan placeholder jika tidak ada data dari userProfile
    // val userImageUrl = userProfile?.photoUrl

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Perubahan: Warna latar utama
    ) {
        item {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp) // Ketinggian header bisa disesuaikan
                    .background(MaterialTheme.colorScheme.primary) // Perubahan: Warna latar header
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.Center) // Pusatkan seluruh kolom header
                        .padding(bottom = 20.dp) // Beri padding bawah agar tidak terlalu mepet ke opsi
                ) {
                    // Jika userProfile.photoUrl ada, gunakan AsyncImage, jika tidak, gunakan placeholder
                    // Untuk contoh ini, kita gunakan placeholder default karena UserProfileResponse tidak punya photoUrl
                    Image(
                        painter = painterResource(id = R.drawable.ic_default_user_avatar), // PERHATIAN: Ganti dengan placeholder avatar Anda
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .border(
                                4.dp,
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f), // Perubahan: Warna border dengan sedikit transparansi
                                CircleShape
                            )
                            .background(MaterialTheme.colorScheme.surfaceVariant), // Background jika gambar gagal load atau transparan
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(12.dp)) // Jarak lebih besar
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall, // Style dari tema
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary // Perubahan: Warna teks
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = userRole,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) // Perubahan: Warna teks dengan transparansi
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Bagian "Account"
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Account",
                    style = MaterialTheme.typography.titleLarge, // Style dari tema
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground, // Perubahan: Warna teks
                    modifier = Modifier.padding(bottom = 12.dp) // Padding lebih besar
                )
                ProfileOption(title = "Change Email") { /* TODO: Navigasi atau aksi */ }
                ProfileOption(title = "Change Password") { /* TODO: Navigasi atau aksi */ }
                ProfileOption(title = "Settings") { /* TODO: Navigasi atau aksi */ }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Bagian "Help Center"
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Help Center",
                    style = MaterialTheme.typography.titleLarge, // Style dari tema
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground, // Perubahan: Warna teks
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                ProfileOption(title = "FAQ") { /* TODO: Navigasi atau aksi */ }
                ProfileOption(title = "Terms & Conditions") { /* TODO: Navigasi atau aksi */ }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp)) // Jarak lebih besar sebelum tombol Log Out
        }

        // Tombol Log Out
        item {
            Button(
                onClick = {
                    TokenManager(context).clearToken() // Menggunakan TokenManager dari com.example.puppy
                    navController.navigate("welcome") {
                        popUpTo("home") { inclusive = true } // Asumsi "home" adalah route utama setelah login
                        launchSingleTop = true
                    }
                },
                shape = RoundedCornerShape(50), // Bentuk tombol tetap
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp, horizontal = 24.dp)
                    .height(52.dp), // Tinggi tombol disesuaikan
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer, // Perubahan: Warna tombol error
                    contentColor = MaterialTheme.colorScheme.onErrorContainer // Perubahan: Warna teks tombol error
                )
            ) {
                Text(
                    text = "Log Out",
                    style = MaterialTheme.typography.labelLarge // Style dari tema
                )
            }
            Spacer(modifier = Modifier.height(24.dp)) // Padding di akhir layar
        }
    }
}

@Composable
private fun ProfileOption(title: String, onClick: () -> Unit = { /* TODO */ }) { // Tambahkan parameter onClick
    Card(
        shape = RoundedCornerShape(16.dp), // Bentuk lebih lembut
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), // Perubahan: Warna border
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(56.dp) // Tinggi opsi disesuaikan
            .clickable(onClick = onClick), // Menggunakan onClick dari parameter
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Tanpa shadow untuk tampilan bersih
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // Perubahan: Warna kontainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge, // Style dari tema
                color = MaterialTheme.colorScheme.onSurface, // Perubahan: Warna teks
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, // Menggunakan ikon auto-mirrored
                contentDescription = "Go to $title",
                tint = MaterialTheme.colorScheme.onSurfaceVariant // Perubahan: Warna ikon
            )
        }
    }
}