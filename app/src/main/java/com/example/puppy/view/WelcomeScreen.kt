package com.example.puppy.view // Perubahan: Package

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button // Menggunakan Button dari Material3
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme // Ditambahkan
import androidx.compose.material3.OutlinedButton // Menggunakan OutlinedButton dari Material3
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush // Masih bisa digunakan jika ingin gradient border
import androidx.compose.ui.graphics.SolidColor // Untuk border solid
// import androidx.compose.ui.graphics.Color // Dihapus jika semua warna dari tema
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.puppy.R // Perubahan: Import R dari package puppy

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Latar belakang dari tema
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 24.dp), // Padding disesuaikan
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround // SpaceAround agar lebih merata
        ) {
            Spacer(modifier = Modifier.weight(0.5f)) // Spacer atas
            LogoSection()
            Spacer(modifier = Modifier.weight(1f)) // Spacer antara logo dan tombol
            ButtonsSection(
                onSignInClick = onNavigateToLogin,
                onNewAccountClick = onNavigateToRegister
            )
            Spacer(modifier = Modifier.weight(0.3f)) // Spacer bawah
        }
    }
}

@Composable
fun LogoSection() {
    Image(
        painter = painterResource(id = R.drawable.puppy_logo), // PERHATIAN: Ganti dengan logo Puppy Anda
        contentDescription = "Puppy App Logo", // Perubahan: Deskripsi logo
        modifier = Modifier.size(260.dp) // Ukuran logo bisa disesuaikan
    )
}

@Composable
fun ButtonsSection(
    onSignInClick: () -> Unit,
    onNewAccountClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth() // Memastikan kolom mengisi lebar
    ) {
        Button(
            onClick = onSignInClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Perubahan: Warna dari tema
                contentColor = MaterialTheme.colorScheme.onPrimary // Perubahan: Warna teks dari tema
            ),
            shape = RoundedCornerShape(50), // Bentuk tombol tetap
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp) // Tinggi tombol disesuaikan sedikit
        ) {
            Text("Sign In", fontSize = 16.sp, style = MaterialTheme.typography.labelLarge) // Gunakan style Typography
        }

        Spacer(modifier = Modifier.height(20.dp)) // Jarak antar tombol

        OutlinedButton(
            onClick = onNewAccountClick,
            shape = RoundedCornerShape(50),
            border = ButtonDefaults.outlinedButtonBorder.copy( // Menggunakan border default dengan warna primary
                width = 2.dp, // Ketebalan border
                brush = SolidColor(MaterialTheme.colorScheme.primary) // Perubahan: Border solid dengan warna primary
                // Jika tetap ingin gradient, pastikan warnanya dari PuppyTheme:
                // brush = Brush.linearGradient(
                // colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                // )
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp) // Tinggi tombol disesuaikan
        ) {
            Text(
                "Create New Account", // Teks diubah agar lebih jelas
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary, // Perubahan: Warna teks dari tema
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}