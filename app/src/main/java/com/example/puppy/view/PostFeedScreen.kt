package com.example.puppy.view // Perubahan: Package

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // Wildcard
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Gif // Atau androidx.compose.material.icons.outlined.GifBox
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.* // Wildcard
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
// import androidx.compose.ui.graphics.Color // Dihapus, gunakan dari MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.puppy.data.UserRepository // Perubahan
import com.example.puppy.service.RetrofitInstance // Perubahan
import com.example.puppy.service.TokenManager // Perubahan
import com.example.puppy.view_model.PostFeedViewModel // Perubahan
import java.io.File

// Fungsi uriToFile harus ada di sini atau diimpor dari lokasi yang benar.
// Jika sudah ada di AddDogProfileScreen.kt atau file utilitas, pastikan bisa diakses.
// Untuk contoh ini, saya asumsikan definisi fungsi ini tersedia.
// fun uriToFile(uri: Uri, context: Context): File { ... }


@Composable
fun PostFeedScreen(navController: NavController, context: Context = LocalContext.current) {
    val viewModel = remember {
        val tokenManager = TokenManager(context) // Dari com.example.puppy
        val repository = UserRepository( // Dari com.example.puppy
            api = RetrofitInstance.userService, // Dari com.example.puppy
            tokenManager = tokenManager,
            context = context
        )
        PostFeedViewModel(repository)
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val file = uriToFile(uri, context) // Menggunakan fungsi uriToFile
            viewModel.selectedImageFile = file
        }
    }

    var content by remember { mutableStateOf("") }

    val postResult by viewModel.postResult.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)

    // Efek untuk navigasi setelah post berhasil
    LaunchedEffect(postResult) { // Menggunakan postResult sebagai key
        postResult?.let {
            // Navigasi ke feed setelah berhasil
            // Perhatikan route "addCat", jika ini bukan route untuk PostFeedScreen,
            // perilaku popUpTo mungkin tidak sesuai harapan.
            // Idealnya, popUpTo(routeSaatIni) { inclusive = true }
            navController.navigate("feed") {
                popUpTo("addDog") { inclusive = true } // Sesuai kode asli Anda
                launchSingleTop = true
            }
            // Pertimbangkan untuk me-reset postResult di ViewModel setelah navigasi
            // agar tidak terpicu lagi: viewModel.clearPostResult()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary) // Warna latar utama dari tema
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cancel",
                color = MaterialTheme.colorScheme.onPrimary, // Warna teks dari tema
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.clickable {
                    navController.popBackStack()
                }
            )
            // Tombol Post akan ada di dalam Surface, sesuai struktur asli
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background, // Warna surface dari tema
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row( // Baris untuk tombol Draft dan Post, sesuai struktur asli
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Draft",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground // Warna teks dari tema
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Post",
                        color = MaterialTheme.colorScheme.onPrimary, // Warna teks dari tema
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary) // Warna tombol Post dari tema
                            .clickable {
                                if (!isLoading) viewModel.postStatus(content) // Hanya post jika tidak sedang loading
                            }
                            .padding(horizontal = 16.dp, vertical = 6.dp) // Padding disesuaikan
                    )
                }

                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp).align(Alignment.CenterHorizontally), // Ukuran & alignment disesuaikan
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp), // Tinggi sesuai kode asli
                    placeholder = { Text("What's happening, puppy lover?") }, // Placeholder disesuaikan
                    shape = RoundedCornerShape(12.dp), // Bentuk disesuaikan
                    colors = OutlinedTextFieldDefaults.colors( // Warna dari tema
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                viewModel.selectedImageFile?.let { file ->
                    Image(
                        painter = rememberAsyncImagePainter(file.toUri()),
                        contentDescription = "Selected Image", // Deskripsi generik
                        modifier = Modifier
                            .fillMaxWidth() // Dibuat fillMaxWidth agar responsif
                            .heightIn(max = 250.dp) // Batasi tinggi maksimum
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit // Fit agar gambar terlihat utuh
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tombol "Pilih Gambar" dikembalikan ke Button biasa
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(Icons.Default.Image, contentDescription = "Select Image Icon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pilih Gambar")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Everyone can reply",
                    color = MaterialTheme.colorScheme.primary, // Warna dari tema
                    style = MaterialTheme.typography.bodySmall // Style dari tema
                )

                Spacer(modifier = Modifier.height(16.dp))
                Divider() // Tambahkan divider jika perlu
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround // Distribusi ikon
                ) {
                    val iconColor = MaterialTheme.colorScheme.primary // Warna ikon dari tema
                    Icon(Icons.Default.TextFields, contentDescription = "Text Formatting", tint = iconColor)
                    Icon(Icons.Default.Image, contentDescription = "Add Image", tint = iconColor)
                    Icon(Icons.Default.Brush, contentDescription = "Drawing", tint = iconColor)
                    Icon(Icons.Default.LiveTv, contentDescription = "Go Live", tint = iconColor)
                    Icon(Icons.Filled.Gif, contentDescription = "Add GIF", tint = iconColor) // Menggunakan Filled.Gif
                    Icon(Icons.Default.MoreHoriz, contentDescription = "More Options", tint = iconColor)
                }
            }
        }
    }
}

// Pastikan fungsi uriToFile ada di sini atau diimpor dengan benar
// Saya akan menyertakannya di sini untuk kelengkapan,
// tapi idealnya ini ada di file utilitas jika dipakai di banyak tempat.
fun uriToFile(uri: Uri, context: Context): File {
    val inputStream = context.contentResolver.openInputStream(uri)!! // Handle non-null assertion carefully
    // Membuat nama file unik dengan timestamp
    val fileName = "temp_image_${System.currentTimeMillis()}.jpg"
    val file = File(context.cacheDir, fileName)
    file.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
    }
    inputStream.close() // Tutup input stream
    return file
}