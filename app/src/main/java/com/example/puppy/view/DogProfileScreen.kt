package com.example.puppy.view

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
// import androidx.compose.ui.graphics.Color // Dihapus, gunakan dari MaterialTheme
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.puppy.data.UserRepository // Perubahan
import com.example.puppy.model.Dog
import com.example.puppy.model.StatusResponse
import com.example.puppy.service.RetrofitInstance // Perubahan
import com.example.puppy.service.TokenManager // Perubahan
import com.example.puppy.view_model.DogProfileViewModel // Perubahan: ViewModel
import com.example.puppy.view_model.FeedViewModel // Perubahan
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeParseException
import java.util.Locale

@Composable
fun DogProfileScreen(navController: NavController, context: Context = LocalContext.current) { // Perubahan nama fungsi

    val dogViewModel = remember { // Perubahan: Nama ViewModel
        val tokenManager = TokenManager(context)
        val repository = UserRepository(
            api = RetrofitInstance.userService,
            tokenManager = tokenManager,
            context = context
        )
        DogProfileViewModel(repository) // Perubahan: Inisialisasi DogProfileViewModel
    }

    // FeedViewModel bisa tetap, atau Anda bisa buat DogFeedViewModel jika ada logika khusus
    val feedViewModel = remember {
        val tokenManager = TokenManager(context) // Anda sudah membuat instance TokenManager di sini
        val repository = UserRepository(
            api = RetrofitInstance.userService,
            tokenManager = tokenManager, // <<-- TAMBAHKAN tokenManager DI SINI
            context = context
        )
        FeedViewModel(repository, tokenManager) // <<-- TAMBAHKAN tokenManager DI SINI JUGA
    }

    val dogProfileResult by dogViewModel.dogResult.observeAsState() // Perubahan: Menggunakan dogResult
    val postList by feedViewModel.postResult.observeAsState()
    // val isDogLoading by dogViewModel.isLoading.observeAsState(false) // Uncomment jika ada isLoading di DogProfileViewModel
    // val isFeedLoading by feedViewModel.isLoading.observeAsState(false)

    LaunchedEffect(Unit) {
        dogViewModel.loadDogProfile() // Perubahan: Memuat profil anjing
        feedViewModel.loadFeed()
    }

    val dog = dogProfileResult?.firstOrNull() // Mengambil anjing pertama dari list (asumsi hanya satu profil anjing per user untuk layar ini)
    val dogUserId = dog?.userId

    val filteredPosts = if (dogUserId != null) {
        postList.orEmpty().filter {
            it.userId == dogUserId && !it.photoUrl.isNullOrBlank()
        }
    } else {
        emptyList()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background), // Perubahan: Warna latar
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cover Photo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // Tinggi cover photo
                    .background(MaterialTheme.colorScheme.surfaceVariant) // Warna placeholder jika tidak ada gambar
            ) {
                dog?.photoUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = "Dog Cover Photo", // Perubahan
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Profile Picture and Name
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = (-50).dp) // Tarik ke atas agar menimpa cover
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface) // Background untuk border/shadow jika perlu
                        .padding(4.dp) // Padding untuk efek border
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant), // Placeholder color
                    contentAlignment = Alignment.Center
                ) {
                    if (dog?.photoUrl != null) {
                        AsyncImage(
                            model = dog.photoUrl,
                            contentDescription = "Dog Avatar", // Perubahan
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Pets, // Atau ikon anjing spesifik
                            contentDescription = "Default Dog Avatar", // Perubahan
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(60.dp)
                        )
                    }

                    // Tombol Tambah Profil jika belum ada
                    if (dog == null /*&& !isDogLoading*/) { // Cek jika dog null dan tidak sedang loading
                        IconButton(
                            onClick = { navController.navigate("adddogprofile") }, // Perubahan: Navigasi
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 8.dp, y = 8.dp) // Sesuaikan offset
                                .size(32.dp)
                                .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Dog Profile", // Perubahan
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    dog?.name ?: "No Dog Profile", // Perubahan
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                dog?.username?.takeIf { it.isNotBlank() }?.let { // Hanya tampilkan jika username ada
                    Text(
                        text = "@$it",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Perubahan
                    )
                }
            }


            if (dog != null) {
                // Followers/Following (Data dummy, sesuaikan jika ada data sebenarnya)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ProfileStat("0", "Posts") // Ganti dengan data yang relevan untuk anjing
                    ProfileStat(calculateAge(dog.birthDate), "Age") // Menggunakan calculateAge
                    ProfileStat(dog.breed, "Breed")
                }

                // Info Tags (Gender, Age, Breed) - Disederhanakan
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    InfoTag(dog.gender.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
                    // InfoTag(calculateAge(dog.birthDate)) // Usia sudah ditampilkan di atas
                    // InfoTag(dog.breed) // Ras sudah ditampilkan di atas
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bio (Contoh)
                Text(
                    "Hi, I'm ${dog.name}! A very good ${if (dog.gender.equals("male", true)) "boy" else "girl"} who loves walks and treats!",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )

                Divider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp))


                // Filter Chips (Contoh, fungsionalitas perlu diimplementasikan jika ada)
                // Row(
                //    modifier = Modifier.padding(horizontal = 16.dp),
                //    horizontalArrangement = Arrangement.spacedBy(8.dp)
                // ) {
                //    FilterChip("All Posts", selected = true, onClick = {})
                //    FilterChip("Photos", onClick = {})
                // FilterChip("Videos")
                // }

                Text(
                    text = "My Posts",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp).align(Alignment.Start)
                )
                // Post Grid
                if (filteredPosts.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 800.dp), // Beri batasan tinggi atau gunakan nested scroll
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        // NDisable scroll untuk LazyVerticalGrid jika di dalam Column yang sudah scrollable
                        // untuk menghindari konflik scroll, atau gunakan nested scrolling.
                        // Jika menggunakan heightIn, ini seharusnya oke.
                    ) {
                        items(filteredPosts) { post ->
                            AsyncImage(
                                model = post.photoUrl,
                                contentDescription = "Dog Post Image", // Perubahan
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp)) // Bentuk lebih lembut
                                    .background(MaterialTheme.colorScheme.surfaceVariant) // Placeholder
                            )
                        }
                    }
                } else {
                    Text(
                        "${dog.name} hasn't posted anything yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

            } else /*if (!isDogLoading)*/ { // Jika profil anjing tidak ada dan tidak sedang loading
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 50.dp), // Sesuaikan padding
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No dog profile found for this user.", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.navigate("adddogprofile") }) { // Navigasi ke tambah profil
                        Text("Add Dog Profile")
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp)) // Padding di akhir
        }

        // Tombol Kembali
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), shape = CircleShape) // Warna dengan transparansi
                .size(40.dp) // Ukuran tombol
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Menggunakan ikon auto-mirrored
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface // Warna ikon
            )
        }
    }
}

// Fungsi calculateAge tetap sama, pastikan diimpor atau ada di file ini
fun calculateAge(birthDateStr: String): String {
    if (birthDateStr.isBlank()) return "Age unknown"
    return try {
        val birthDate = LocalDate.parse(birthDateStr)
        val today = LocalDate.now()
        val period = Period.between(birthDate, today)

        when {
            period.years > 0 -> "${period.years} year${if (period.years > 1) "s" else ""}"
            period.months > 0 -> "${period.months} month${if (period.months > 1) "s" else ""}"
            else -> {
                val days = java.time.temporal.ChronoUnit.DAYS.between(birthDate, today)
                "$days day${if (days != 1L) "s" else ""}" // Handle 1 day
            }
        }  + " old"
    } catch (e: DateTimeParseException) {
        Log.e("CalculateAge", "Invalid date format for $birthDateStr", e)
        "Age unknown"
    }
}

// Adaptasi InfoTag dengan warna tema
@Composable
fun InfoTag(text: String) {
    if (text.isBlank()) return // Jangan tampilkan tag jika teks kosong

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(16.dp)) // Perubahan
            .padding(horizontal = 12.dp, vertical = 6.dp) // Padding lebih besar sedikit
    ) {
        Text(text, color = MaterialTheme.colorScheme.onSecondaryContainer, style = MaterialTheme.typography.labelMedium) // Perubahan
    }
}

// Composable untuk menampilkan stat di profil (Contoh)
@Composable
fun RowScope.ProfileStat(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
    ) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}


// Adaptasi FilterChip dengan warna tema (fungsionalitas klik perlu ditambahkan jika mau)
@Composable
fun FilterChip(text: String, selected: Boolean = false, onClick: () -> Unit = {}) {
    Button( // Menggunakan Button agar bisa diklik dan punya state visual
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant, // Perubahan
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant // Perubahan
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp) // Padding untuk Button
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge) // Style bisa disesuaikan
    }
}