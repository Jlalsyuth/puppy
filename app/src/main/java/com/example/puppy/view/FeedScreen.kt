package com.example.puppy.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // Wildcard
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder // Menggunakan versi Border
import androidx.compose.material.icons.filled.ChatBubbleOutline // Menggunakan versi Outline
import androidx.compose.material.icons.filled.FavoriteBorder // Menggunakan versi Border (Pets diganti Favorite)
import androidx.compose.material3.* // Wildcard
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
// import androidx.compose.ui.graphics.Color // Dihapus
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.puppy.model.StatusResponse // Pastikan StatusResponse sudah benar path-nya
import com.example.puppy.service.RetrofitInstance // Perubahan
import com.example.puppy.service.TokenManager // Perubahan
import com.example.puppy.view_model.FeedViewModel // Perubahan
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import android.util.Log // Untuk Log.d

// StorySection tetap dikomentari, jika diaktifkan, perlu adaptasi warna dan gambar

@Composable
fun FeedScreen(navController: NavController, context: Context = LocalContext.current) {

    // Inisialisasi ViewModel dengan TokenManager
    val viewModel = remember {
        val tokenManager = TokenManager(context)
        val repository = UserRepository(
            api = RetrofitInstance.userService,
            tokenManager = tokenManager,
            context = context
        )
        FeedViewModel(repository, tokenManager) // Berikan tokenManager ke ViewModel
    }

    val responseList by viewModel.postResult.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    // BARIS YANG SEBELUMNYA ERROR, SEKARANG SEHARUSNYA BEKERJA:
    val loggedInUserId by viewModel.loggedInUserId.collectAsState() // Kumpulkan dari StateFlow

    val posts = responseList ?: emptyList()

    // LaunchedEffect untuk loadFeed tidak perlu diubah
    LaunchedEffect(Unit) {
        viewModel.loadFeed()
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Image(
            painter = painterResource(id = R.drawable.puppy_logo),
            contentDescription = "Puppy App Logo",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, bottom = 8.dp)
                .size(60.dp),
            contentScale = androidx.compose.ui.layout.ContentScale.Fit
        )

        if (isLoading && posts.isEmpty()) { // Tampilkan loading hanya jika post kosong
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else if (posts.isEmpty() && !isLoading) { // Tampilkan pesan jika tidak ada post
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No posts yet. Be the first to share!", style = MaterialTheme.typography.bodyLarge)
            }
        }
        else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp), // Ruang untuk logo
                contentPadding = PaddingValues(bottom = 80.dp, top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(posts) { post ->
                    val dogData = post.user?.dogs?.firstOrNull()
                    val dogUsername = dogData?.username ?: (post.user?.fullName ?: "Unknown User")
                    val breed = dogData?.breed ?: "Unknown Breed"
                    val age = dogData?.birthDate?.let { calculateAge(it) } ?: "Unknown Age"
                    val gender = dogData?.gender ?: "Unknown Gender"
                    val userAvatarUrl = dogData?.photoUrl
                    val postOwnerId = post.userId // ID pemilik postingan

                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        PostCard(
                            username = dogUsername,
                            age = age,
                            breed = breed,
                            gender = gender,
                            caption = post.content,
                            imageList = listOfNotNull(post.photoUrl),
                            likeCount = "0",
                            commentCount = "0",
                            shareCount = "0",
                            avatarUrl = userAvatarUrl,
                            postOwnerId = postOwnerId,
                            loggedInUserId = loggedInUserId, // Teruskan loggedInUserId
                            onEditClicked = {
                                Log.d("FeedAction", "Edit clicked for post ID: ${post.id} (owned by $postOwnerId)")
                                // TODO: Navigasi ke layar edit atau panggil fungsi ViewModel untuk edit
                            },
                            onDeleteClicked = {
                                Log.d("FeedAction", "Delete clicked for post ID: ${post.id} (owned by $postOwnerId)")
                                // TODO: Tampilkan dialog konfirmasi dan panggil fungsi ViewModel untuk delete
                            }
                        )
                    }
                }
            }
        }

        AddPostButton(
            onClick = { navController.navigate("upload") }, // Asumsi "upload" adalah route PostFeedScreen
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

// ... (Import lainnya di FeedScreen.kt) ...
@Composable
fun PostCard(
    username: String,
    age: String,
    breed: String,
    gender: String,
    caption: String,
    imageList: List<String>,
    likeCount: String,
    commentCount: String,
    shareCount: String,
    avatarUrl: String?,
    // Parameter baru:
    postOwnerId: Int,
    loggedInUserId: Int?, // Bisa null jika pengguna belum login atau ID tidak ditemukan
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween, // Agar menu bisa di kanan
            modifier = Modifier.fillMaxWidth()
        ) {
            Row( // Bagian Kiri: Avatar dan Info Pengguna/Anjing
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f) // Ambil sisa ruang agar menu tidak terdorong
            ) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "$username's Avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_default_dog_avatar),
                    error = painterResource(id = R.drawable.ic_default_dog_avatar)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        username,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        InfoChip(text = gender, chipType = InfoChipType.PRIMARY)
                        InfoChip(text = age, chipType = InfoChipType.SECONDARY)
                        InfoChip(text = breed, chipType = InfoChipType.TERTIARY, maxLines = 1)
                    }
                }
            }

            // Bagian Kanan: Tombol Opsi (Edit/Delete)
            // Tampilkan menu hanya jika post milik pengguna yang login dan loggedInUserId tidak null
            if (loggedInUserId != null && postOwnerId == loggedInUserId) {
                var menuExpanded by remember { mutableStateOf(false) }
                Box { // Box diperlukan agar DropdownMenu diposisikan relatif terhadap IconButton
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                menuExpanded = false
                                onEditClicked() // Panggil fungsi edit dari parameter
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                menuExpanded = false
                                onDeleteClicked() // Panggil fungsi delete dari parameter
                            }
                        )
                    }
                }
            }
        }

        // ... (Sisa dari PostCard: Caption, ImageList, InteractionIcons tetap sama) ...
        if (caption.isNotBlank()){
            Text(
                text = caption,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (imageList.isNotEmpty() && imageList.firstOrNull()?.isNotBlank() == true) {
            AsyncImage(
                model = imageList.first(),
                contentDescription = "Post Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.image_placeholder),
                error = painterResource(id = R.drawable.image_placeholder)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            InteractionIcon(icon = Icons.Default.FavoriteBorder, count = likeCount, label = "Likes")
            InteractionIcon(icon = Icons.Default.ChatBubbleOutline, count = commentCount, label = "Comments")
            InteractionIcon(icon = Icons.Default.BookmarkBorder, count = shareCount, label = "Bookmarks")
        }
    }
}
// Enum untuk tipe InfoChip agar warnanya bisa dikelola dari tema
enum class InfoChipType { PRIMARY, SECONDARY, TERTIARY }

@Composable
fun InfoChip(text: String, chipType: InfoChipType, maxLines: Int = Int.MAX_VALUE) {
    val backgroundColor = when(chipType) {
        InfoChipType.PRIMARY -> MaterialTheme.colorScheme.primaryContainer
        InfoChipType.SECONDARY -> MaterialTheme.colorScheme.secondaryContainer
        InfoChipType.TERTIARY -> MaterialTheme.colorScheme.tertiaryContainer
    }
    val textColor = when(chipType) {
        InfoChipType.PRIMARY -> MaterialTheme.colorScheme.onPrimaryContainer
        InfoChipType.SECONDARY -> MaterialTheme.colorScheme.onSecondaryContainer
        InfoChipType.TERTIARY -> MaterialTheme.colorScheme.onTertiaryContainer
    }

    if (text.isBlank() || text.equals("Unknown", ignoreCase = true) || text.equals("Unknown Breed", ignoreCase = true) || text.equals("Unknown Age", ignoreCase = true) || text.equals("Unknown Gender", ignoreCase = true)) return // Jangan tampilkan jika data tidak valid

    Box(
        modifier = Modifier
            .background(backgroundColor, shape = RoundedCornerShape(50)) // Bentuk pil
            .padding(horizontal = 10.dp, vertical = 4.dp) // Padding disesuaikan
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall, // Style dari tema
            maxLines = maxLines,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

@Composable
fun InteractionIcon(icon: ImageVector, count: String, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = { /* Aksi ketika ikon diklik, misal like, comment */ })
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurfaceVariant // Perubahan warna
        )
        Spacer(modifier = Modifier.width(6.dp)) // Jarak ikon dan teks
        Text(
            text = count,
            style = MaterialTheme.typography.bodySmall, // Style dari tema
            color = MaterialTheme.colorScheme.onSurfaceVariant // Perubahan warna
        )
    }
}

@Composable
fun AddPostButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape, // Bentuk FAB menjadi lingkaran
        containerColor = MaterialTheme.colorScheme.primary, // Warna latar FAB dari tema
        contentColor = MaterialTheme.colorScheme.onPrimary, // Warna ikon di dalam FAB dari tema
        modifier = modifier.size(64.dp) // Ukuran FAB bisa disesuaikan
    ) {
        Icon(
            imageVector = Icons.Default.Add, // Ikon tambah standar
            contentDescription = "Add Post",
            modifier = Modifier.size(32.dp) // Ukuran ikon di dalam FAB
        )
    }
}

// Fungsi calculateAge perlu ada di sini atau diimpor jika sudah ada di file utilitas.
// Pastikan sudah diadaptasi seperti pada DogProfileScreen.kt
// fun calculateAge(birthDateStr: String): String { ... }