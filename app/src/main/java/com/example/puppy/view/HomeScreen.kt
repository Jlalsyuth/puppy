package com.example.puppy.view // Perubahan: Package

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.* // Wildcard import
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.* // Wildcard import
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.puppy.R // Perubahan
import com.example.puppy.data.UserRepository // Perubahan
import com.example.puppy.service.RetrofitInstance // Perubahan
import com.example.puppy.service.TokenManager // Perubahan
import com.example.puppy.view_model.DogProfileViewModel // Perubahan: ViewModel
import com.example.puppy.view_model.ProfileViewModel // Perubahan

@Composable
fun HomeScreen(navController: NavController, context: Context = LocalContext.current) { // Default context

    val profileViewModel = remember { // ProfileViewModel untuk data pengguna umum
        val tokenManager = TokenManager(context)
        val repository = UserRepository(
            api = RetrofitInstance.userService,
            tokenManager = tokenManager,
            context = context
        )
        ProfileViewModel(repository)
    }

    val dogViewModel = remember { // DogProfileViewModel untuk data profil anjing pengguna
        val tokenManager = TokenManager(context)
        val repository = UserRepository(
            api = RetrofitInstance.userService,
            tokenManager = tokenManager,
            context = context
        )
        DogProfileViewModel(repository) // Perubahan
    }

    val userProfile by profileViewModel.userProfile.observeAsState()
    val isLoadingUserProfile by profileViewModel.isLoading.observeAsState(true) // Asumsi ProfileVM punya isLoading
    val dogResponse by dogViewModel.dogResult.observeAsState() // Perubahan

    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()
        dogViewModel.loadDogProfile() // Perubahan
    }

    val userName = if (isLoadingUserProfile) "Loading..." else userProfile?.fullName ?: "Guest"
    val dog = dogResponse?.firstOrNull() // Profil anjing pengguna

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background) // Perubahan: Warna latar
    ) {
        HeaderSection( // Header menggunakan data pengguna dan foto profil anjing
            userName = userName,
            userImage = dog?.photoUrl ?: "https://www.svgrepo.com/show/495590/profile-circle.svg", // Default jika tidak ada foto anjing
            onDogProfileClick = { // Perubahan nama parameter untuk kejelasan
                navController.navigate("dogprofile") // Perubahan: Navigasi ke profil anjing
            }
        )
        SearchBar() // SearchBar tetap generik
        FeatureIcons(navController) // Ikon fitur dengan teks yang diubah
        OnlineConsultationCard(navController) // Kartu konsultasi, teks mungkin perlu disesuaikan
        CommunitySection() // Bagian komunitas dengan konten yang diubah

        Spacer(modifier = Modifier.height(80.dp)) // Spacer untuk BottomNavBar jika ada
    }
}

@Composable
fun HeaderSection(
    userName: String,
    userImage: String,
    onDogProfileClick: () -> Unit, // Perubahan nama parameter
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp) // Ketinggian header bisa disesuaikan
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val width = size.width
            val height = size.height
            val waveHeight = height * 0.17f

            val path = Path().apply {
                moveTo(0f, height - waveHeight)
                cubicTo(
                    width * 0.25f, height,
                    width * 0.75f, height - waveHeight * 2,
                    width, height - waveHeight
                )
                lineTo(width, 0f)
                lineTo(0f, 0f)
                close()
            }
            // Gunakan warna primary dari tema untuk gelombang header
            drawPath(path, color = Color(0xFFFF8C2F)) // Perubahan: Warna gelombang
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp), // Padding disesuaikan
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Hi, $userName!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onPrimary, // Perubahan: Warna teks
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Go to Dog profile ->", // Perubahan: Teks
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), // Warna teks dengan sedikit transparansi
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable { onDogProfileClick() } // Perubahan
                )
            }
            AsyncImage(
                model = userImage,
                contentDescription = "User Avatar (Dog's Photo)", // Perubahan
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_default_dog_avatar), // TAMBAHKAN INI: default avatar anjing
                error = painterResource(id = R.drawable.ic_default_dog_avatar), // TAMBAHKAN INI: default avatar anjing
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant) // Warna placeholder jika AsyncImage lama load
            )
        }
    }
}

@Composable
fun SearchBar() { // Warna akan otomatis mengikuti tema OutlinedTextField
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Search products, articles, vets...", fontSize = 14.sp) }, // Teks placeholder bisa disesuaikan
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                modifier = Modifier.padding(start = 12.dp)
            )
        },
        shape = RoundedCornerShape(32.dp),
        textStyle = MaterialTheme.typography.bodyMedium, // Gunakan typography dari tema
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp), // Padding disesuaikan
        colors = OutlinedTextFieldDefaults.colors( // Sesuaikan warna jika perlu, atau biarkan default M3
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        )
    )
}

@Composable
fun FeatureIcons(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp, vertical = 8.dp), // Padding disesuaikan
        horizontalArrangement = Arrangement.SpaceAround // SpaceAround untuk distribusi yang lebih merata
    ) {
        // Pastikan nama drawable 'dog_feed_icon', 'dog_health_icon', 'dog_market_icon' ada
        FeatureItem("Puppy Feed", R.drawable.dog_feed_icon) { navController.navigate("feed") } // Perubahan teks & ikon
        FeatureItem("Puppy Health", R.drawable.dog_health_icon) { navController.navigate("health") } // Perubahan teks & ikon
        FeatureItem("Puppy Market", R.drawable.dog_market_icon) { navController.navigate("market") } // Perubahan teks & ikon
    }
}

@Composable
fun FeatureItem(label: String, imageResId: Int, onClick: () -> Unit = {}) { // Ubah parameter image ke Int (ID drawable)
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = true, color = MaterialTheme.colorScheme.primary), // Ripple yang lebih jelas
                onClick = onClick
            )
            .padding(8.dp), // Padding untuk area klik
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp) // Ukuran box luar
                .clip(RoundedCornerShape(20.dp)) // Bentuk lebih lembut
                .background(MaterialTheme.colorScheme.primaryContainer) // Perubahan: Warna
                .padding(12.dp), // Padding dalam untuk ikon
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = imageResId), // Menggunakan ID drawable langsung
                contentDescription = label,
                modifier = Modifier.size(36.dp) // Ukuran ikon
            )
        }
        Text(
            label,
            style = MaterialTheme.typography.labelLarge, // Style dari tema
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurface // Warna teks dari tema
        )
    }
}

@Composable
fun OnlineConsultationCard(navController: NavController) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer), // Perubahan: Warna kartu
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Sedikit shadow
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pastikan R.drawable.consultation_dog_icon ada
            Image(
                painter = painterResource(id = R.drawable.consultatian_dog_icon), // Ganti dengan ikon konsultasi anjing
                contentDescription = "Online Consultation Icon",
                modifier = Modifier.size(100.dp) // Ukuran gambar disesuaikan
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Online Vet Consultation",
                    style = MaterialTheme.typography.titleMedium, // Style dari tema
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer // Perubahan
                )
                Text(
                    text = "Chat with a veterinarian for your puppy now!", // Perubahan: Teks
                    style = MaterialTheme.typography.bodyMedium, // Style dari tema
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f) // Perubahan
                )
                Button(
                    onClick = { navController.navigate("health") }, // Route bisa disesuaikan
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary, // Perubahan
                        contentColor = MaterialTheme.colorScheme.onTertiary // Perubahan
                    ),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = "Chat Now",
                        style = MaterialTheme.typography.labelLarge // Style dari tema
                    )
                }
            }
        }
    }
}

@Composable
fun CommunitySection() {
    Text(
        "Puppy Community", // Perubahan: Teks
        style = MaterialTheme.typography.titleLarge, // Style dari tema
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp) // Padding disesuaikan
    )

    Row( // Untuk contoh, bisa menggunakan LazyRow jika item banyak
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        // Konten CommunityCard perlu diubah total menjadi relevan dengan anjing
        CommunityCard(
            title = "Dog Grooming 101",
            subtitle = "Tips for a happy and clean puppy!",
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer, // Perubahan
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer, // Perubahan
            buttonColor = MaterialTheme.colorScheme.secondary, // Perubahan
            buttonContentColor = MaterialTheme.colorScheme.onSecondary, // Perubahan
            modifier = Modifier.weight(1f) // Agar kartu responsif
        )
        CommunityCard(
            title = "Puppy Training Basics",
            subtitle = "Positive reinforcement techniques.",
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer, // Perubahan
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer, // Perubahan
            buttonColor = MaterialTheme.colorScheme.secondary, // Perubahan
            buttonContentColor = MaterialTheme.colorScheme.onSecondary, // Perubahan
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CommunityCard(
    title: String,
    subtitle: String,
    backgroundColor: androidx.compose.ui.graphics.Color, // Menggunakan tipe Color dari MaterialTheme
    contentColor: androidx.compose.ui.graphics.Color,
    buttonColor: androidx.compose.ui.graphics.Color,
    buttonContentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(180.dp) // Atau biarkan responsif dengan weight
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall, // Style dari tema
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall, // Style dari tema
                color = contentColor.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { /* Aksi Lihat Detail */ },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = buttonContentColor
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp) // Padding tombol
            ) {
                Text("See More", style = MaterialTheme.typography.labelMedium) // Style dari tema
            }
        }
    }
}