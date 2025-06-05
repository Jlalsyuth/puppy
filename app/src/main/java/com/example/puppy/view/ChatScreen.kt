package com.example.puppy.view

// Import Context jika diperlukan oleh fungsi lain, tapi tidak dipakai langsung di Composable ini
// import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.* // Wildcard
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Menggunakan auto-mirrored
import androidx.compose.material.icons.automirrored.filled.Send // Menggunakan auto-mirrored
import androidx.compose.material3.* // Wildcard
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
// import androidx.compose.ui.graphics.Color // Dihapus, gunakan dari MaterialTheme
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.puppy.R // Perubahan

@Composable
fun ChatScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Perubahan: Warna latar utama
    ) {
        // Custom Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface) // Latar header bisa surface atau primary
                .padding(horizontal = 8.dp, vertical = 12.dp) // Padding disesuaikan
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Menggunakan ikon auto-mirrored
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary // Perubahan: Warna ikon
                )
            }

            // Spacer(modifier = Modifier.width(8.dp)) // Dihapus agar gambar dokter lebih dekat ke tombol back

            Image(
                painter = painterResource(id = R.drawable.doctor_placeholder_1), // PERHATIAN: Ganti dengan gambar dokter Anda
                contentDescription = "Doctor Profile Picture",
                modifier = Modifier
                    .size(40.dp) // Ukuran disesuaikan
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant), // Placeholder bg
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp)) // Jarak lebih besar

            Column {
                Text(
                    text = "Dr. Rina Masturina", // Data dokter hardcoded
                    style = MaterialTheme.typography.titleMedium, // Gunakan style dari tema
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface // Perubahan: Warna teks
                )
                Text(
                    text = "Veterinary Cardiologist", // Data dokter hardcoded
                    style = MaterialTheme.typography.bodySmall, // Gunakan style dari tema
                    color = MaterialTheme.colorScheme.primary // Perubahan: Warna teks (bisa juga onSurfaceVariant)
                )
            }
        }

        Divider(color = MaterialTheme.colorScheme.outlineVariant) // Perubahan: Warna divider

        // Chat Area
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()) // Untuk scroll jika chat panjang
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            // ChatBubble("Halo dok!", isSender = true)
            // ChatBubble("Hai, ada yang bisa saya bantu?", isSender = false)
            // ChatBubble("Saya mau tanya soal vaksin untuk anak anjing saya", isSender = true) // Teks disesuaikan
            // ChatBubble("Tentu, anak anjingnya usia berapa ya?", isSender = false)
        }

        // Input Area
        ChatInputBar { message ->
            // Logika pengiriman pesan (saat ini hanya print)
            println("Pesan dikirim: $message")
            // TODO: Tambahkan logika untuk menampilkan pesan di Chat Area dan mengirim ke backend/ViewModel
        }
    }
}

@Composable
fun ChatInputBar(onSend: (String) -> Unit) {
    var message by remember { mutableStateOf("") }

    Surface( // Menggunakan Surface untuk input bar agar bisa diberi elevation jika perlu
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer // Warna latar input bar yang sedikit beda
        // elevation = 4.dp // Opsional: tambahkan shadow jika diinginkan
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp) // Padding lebih standar
                .fillMaxWidth(),
            // .background(MaterialTheme.colorScheme.surfaceContainerHighest, shape = RoundedCornerShape(24.dp)) // Alternatif background
            // .padding(horizontal = 16.dp, vertical = 8.dp), // Dihapus jika background di Surface
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField( // Menggunakan TextField dari Material 3
                value = message,
                onValueChange = { message = it },
                placeholder = { Text("Type your message...", style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier.weight(1f),
                maxLines = 4, // Batasi jumlah baris
                singleLine = false,
                colors = TextFieldDefaults.colors( // Theming untuk TextField M3
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceBright, // Warna kontainer saat fokus
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceBright, // Warna kontainer saat tidak fokus
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceBright,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent, // Hilangkan indikator bawah
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(24.dp) // Bentuk TextField
            )

            Spacer(modifier = Modifier.width(8.dp)) // Jarak antara TextField dan Tombol Kirim

            IconButton(
                onClick = {
                    if (message.isNotBlank()) {
                        onSend(message)
                        message = "" // Kosongkan field setelah dikirim
                    }
                },
                colors = IconButtonDefaults.iconButtonColors( // Theming untuk IconButton M3
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.size(48.dp) // Ukuran tombol
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send, // Menggunakan ikon auto-mirrored
                    contentDescription = "Send Message",
                    // tint = MaterialTheme.colorScheme.primary // Tint tidak perlu jika contentColor diatur di IconButtonDefaults
                )
            }
        }
    }
}

// Composable ChatBubble (jika ingin diimplementasikan dan di-theme)
// @Composable
// fun ChatBubble(text: String, isSender: Boolean) {
//    val alignment = if (isSender) Alignment.End else Alignment.Start
//    val backgroundColor = if (isSender) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
//    val textColor = if (isSender) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
//    val shape = if (isSender) {
//        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
//    } else {
//        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp),
//        contentAlignment = if (isSender) Alignment.CenterEnd else Alignment.CenterStart
//    ) {
//        Surface(
//            shape = shape,
//            color = backgroundColor,
//            modifier = Modifier.padding(horizontal = if (isSender) 32.dp else 0.dp, vertical = 0.dp) // Agar tidak full width
//                .padding(start = if(isSender) 32.dp else 0.dp, end = if(!isSender) 32.dp else 0.dp)
//        ) {
//            Text(
//                text = text,
//                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
//                color = textColor,
//                style = MaterialTheme.typography.bodyLarge
//            )
//        }
//    }
// }