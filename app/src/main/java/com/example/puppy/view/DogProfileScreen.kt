package com.example.puppy.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.puppy.data.UserRepository
import com.example.puppy.model.StatusResponse
import com.example.puppy.service.RetrofitInstance
import com.example.puppy.service.TokenManager
import com.example.puppy.utils.calculateAge
import com.example.puppy.view_model.DogProfileViewModel
import com.example.puppy.view_model.FeedUiState
import com.example.puppy.view_model.FeedViewModel
import java.util.*

@Composable
fun DogProfileScreen(navController: NavController) {

    // ViewModel Factory yang lebih rapi
    val factory = remember {
        ViewModelFactory(navController.context)
    }
    val dogViewModel: DogProfileViewModel = viewModel(factory = factory)
    val feedViewModel: FeedViewModel = viewModel(factory = factory)

    // Mengamati state dari kedua ViewModel
    val dogProfile by dogViewModel.dogResult.observeAsState()
    val feedUiState by feedViewModel.uiState.collectAsState()

    // Memuat data saat layar pertama kali ditampilkan
    LaunchedEffect(Unit) {
        dogViewModel.loadDogProfile()
        feedViewModel.loadFeed()
    }

    // Mengambil data anjing pertama dari daftar (asumsi hanya satu)
    val dog = dogProfile?.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- BAGIAN 1: HEADER PROFIL (ditempatkan di dalam satu item) ---
        item {
            ProfileHeader(
                dog = dog,
                postCount = when(feedUiState){
                    is FeedUiState.Success -> (feedUiState as FeedUiState.Success).posts.count { it.userId == dog?.userId }
                    else -> 0
                },
                onAddProfileClick = { navController.navigate("adddogprofile") }
            )
        }

        // --- BAGIAN 2: GRID POSTINGAN ---
        when (val state = feedUiState) {
            is FeedUiState.Loading -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            is FeedUiState.Error -> {
                item {
                    Text(state.message, modifier = Modifier.padding(16.dp))
                }
            }
            is FeedUiState.Success -> {
                // Filter postingan yang sesuai dengan ID user dari profil anjing
                val filteredPosts = state.posts.filter {
                    it.userId == dog?.userId && !it.photoUrl.isNullOrBlank()
                }

                if (filteredPosts.isNotEmpty()) {
                    // Membuat grid 3 kolom dengan membagi postingan menjadi baris-baris
                    val postRows = filteredPosts.chunked(3)
                    items(postRows) { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { post ->
                                AsyncImage(
                                    model = post.photoUrl,
                                    contentDescription = "Dog Post Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                )
                            }
                            // Jika satu baris tidak penuh 3 item, tambahkan Spacer untuk mengisi ruang
                            repeat(3 - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else if (dog != null){
                    item {
                        Text(
                            "${dog.name} hasn't posted anything yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    // Tombol Kembali (tetap di atas segalanya)
    IconButton(
        onClick = { navController.popBackStack() },
        modifier = Modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), shape = CircleShape)
            .size(40.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}


@Composable
fun ProfileHeader(dog: com.example.puppy.model.Dog?, postCount: Int, onAddProfileClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Cover Photo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            dog?.photoUrl?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Dog Cover Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Profile Picture and Name
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = (-50).dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (dog?.photoUrl != null) {
                    AsyncImage(
                        model = dog.photoUrl,
                        contentDescription = "Dog Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = "Default Dog Avatar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                dog?.name ?: "No Dog Profile",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            dog?.username?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = "@$it",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (dog != null) {
            // Profile Stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ProfileStat(postCount.toString(), "Posts")
                ProfileStat(calculateAge(dog.birthDate), "Age")
                ProfileStat(dog.breed, "Breed")
            }

            // Info Tag
            InfoTag(dog.gender.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
            Spacer(modifier = Modifier.height(16.dp))

            // Bio
            Text(
                "Hi, I'm ${dog.name}! A very good ${if (dog.gender.equals("male", true)) "boy" else "girl"} who loves walks and treats!",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                textAlign = TextAlign.Center
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp))

            Text(
                text = "My Posts",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp).fillMaxWidth()
            )

        } else {
            // Tampilan jika tidak ada profil anjing
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No dog profile found for this user.", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onAddProfileClick) {
                    Text("Add Dog Profile")
                }
            }
        }
    }
}


// --- Helper Composables & ViewModel Factory ---

// Factory sederhana untuk menyediakan dependensi ke ViewModel
class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val tokenManager = TokenManager(context)
        val repository = UserRepository(
            api = RetrofitInstance.userService,
            tokenManager = tokenManager,
            context = context
        )
        return when {
            modelClass.isAssignableFrom(FeedViewModel::class.java) ->
                FeedViewModel(repository, tokenManager) as T
            modelClass.isAssignableFrom(DogProfileViewModel::class.java) ->
                DogProfileViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

@Composable
fun InfoTag(text: String) {
    if (text.isBlank()) return
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, color = MaterialTheme.colorScheme.onSecondaryContainer, style = MaterialTheme.typography.labelMedium)
    }
}

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
