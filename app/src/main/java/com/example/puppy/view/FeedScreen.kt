package com.example.puppy.view

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.puppy.R
import com.example.puppy.data.UserRepository
import com.example.puppy.model.StatusResponse
import com.example.puppy.service.RetrofitInstance
import com.example.puppy.service.TokenManager
import com.example.puppy.view_model.FeedUiState
import com.example.puppy.view_model.FeedViewModel
import com.example.puppy.utils.calculateAge
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeParseException
import java.util.*

@Composable
fun FeedScreen(
    navController: NavController
) {
    // DAPATKAN CONTEXT DI SINI, DI DALAM LINGKUP COMPOSABLE
    val context = LocalContext.current

    // Buat ViewModel Factory dengan meneruskan context
    val viewModel: FeedViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                // Gunakan context yang sudah diteruskan, jangan panggil LocalContext.current lagi
                val tokenManager = TokenManager(context)
                val repository = UserRepository(
                    api = RetrofitInstance.userService,
                    tokenManager = tokenManager,
                    context = context
                )
                @Suppress("UNCHECKED_CAST")
                return FeedViewModel(repository, tokenManager) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()
    val loggedInUserId by viewModel.loggedInUserId.collectAsState()

    // State untuk mengelola dialog
    var showDeleteDialog by remember { mutableStateOf<String?>(null) } // Simpan ID post yang akan dihapus
    var showEditDialog by remember { mutableStateOf<StatusResponse?>(null) } // Simpan data post yang akan diedit

    // Observe toast events
    LaunchedEffect(Unit) {
        viewModel.toastEvent.observeForever { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // --- UI Header ---
        Image(
            painter = painterResource(id = R.drawable.puppy_logo),
            contentDescription = "Puppy App Logo",
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp, bottom = 8.dp).size(60.dp),
            contentScale = ContentScale.Fit
        )

        // --- Konten Utama Berdasarkan State ---
        when (val state = uiState) {
            is FeedUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            is FeedUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, style = MaterialTheme.typography.bodyLarge)
                }
            }
            is FeedUiState.Success -> {
                if (state.posts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No posts yet. Be the first to share!", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(top = 80.dp),
                        contentPadding = PaddingValues(bottom = 80.dp, top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.posts, key = { it.id }) { post ->
                            PostCard(
                                post = post,
                                loggedInUserId = loggedInUserId,
                                onEditClicked = { showEditDialog = post },
                                // PERBAIKAN: Ubah post.id (Int) menjadi String
                                onDeleteClicked = { showDeleteDialog = post.id.toString() }
                            )
                        }
                    }
                }
            }
        }

        // --- Tombol Tambah Post ---
        AddPostButton(
            onClick = { navController.navigate("upload") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }

    // --- Dialog Konfirmasi Hapus ---
    if (showDeleteDialog != null) {
        DeleteConfirmationDialog(
            onConfirm = {
                viewModel.deletePost(showDeleteDialog!!)
                showDeleteDialog = null
            },
            onDismiss = { showDeleteDialog = null }
        )
    }

    // --- Dialog Edit ---
    if (showEditDialog != null) {
        EditPostDialog(
            post = showEditDialog!!,
            onConfirm = { postId, newContent ->
                // PERBAIKAN: Ubah postId (Int) menjadi String
                viewModel.updatePost(postId.toString(), newContent, null) // Untuk saat ini edit foto belum diimplementasikan
                showEditDialog = null
            },
            onDismiss = { showEditDialog = null }
        )
    }
}

@Composable
fun PostCard(
    post: StatusResponse,
    loggedInUserId: String?,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    val dogData = post.user?.dogs?.firstOrNull()
    val dogUsername = dogData?.username ?: (post.user?.fullName ?: "Unknown User")
    val breed = dogData?.breed ?: "Unknown Breed"
    val age = dogData?.birthDate?.let { calculateAge(it) } ?: "Unknown Age"
    val gender = dogData?.gender ?: "Unknown Gender"
    val userAvatarUrl = dogData?.photoUrl
    val postOwnerId = post.userId.toString()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // User Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                AsyncImage(
                    model = userAvatarUrl,
                    contentDescription = "$dogUsername's Avatar",
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_default_dog_avatar),
                    error = painterResource(id = R.drawable.ic_default_dog_avatar)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(dogUsername, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        InfoChip(text = gender, chipType = InfoChipType.PRIMARY)
                        InfoChip(text = age, chipType = InfoChipType.SECONDARY)
                        InfoChip(text = breed, chipType = InfoChipType.TERTIARY, maxLines = 1)
                    }
                }
            }

            // Menu Edit/Delete
            if (loggedInUserId != null && postOwnerId == loggedInUserId) {
                var menuExpanded by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(text = { Text("Edit") }, onClick = { menuExpanded = false; onEditClicked() })
                        DropdownMenuItem(text = { Text("Delete") }, onClick = { menuExpanded = false; onDeleteClicked() })
                    }
                }
            }
        }

        // Caption
        if (post.content.isNotBlank()) {
            Text(text = post.content, modifier = Modifier.padding(top = 12.dp, bottom = 8.dp), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Gambar Post
        if (post.photoUrl?.isNotBlank() == true) {
            AsyncImage(
                model = post.photoUrl,
                contentDescription = "Post Image",
                modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).padding(top = 8.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.image_placeholder),
                error = painterResource(id = R.drawable.image_placeholder)
            )
        }

        // Tombol Interaksi
        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.SpaceAround) {
            InteractionIcon(icon = Icons.Default.FavoriteBorder, count = "0", label = "Likes")
            InteractionIcon(icon = Icons.Default.ChatBubbleOutline, count = "0", label = "Comments")
            InteractionIcon(icon = Icons.Default.BookmarkBorder, count = "0", label = "Bookmarks")
        }
    }
}

// --- Composable Baru untuk Dialog Edit ---
@Composable
fun EditPostDialog(
    post: StatusResponse,
    onConfirm: (postId: Int, newContent: String) -> Unit,
    onDismiss: () -> Unit
) {
    var content by remember { mutableStateOf(post.content) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Status") },
        text = {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("What's on your mind?") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(post.id, content) }) {
                Text("Save", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ... (Composable lainnya seperti InfoChip, InteractionIcon, AddPostButton, DeleteConfirmationDialog, dan fungsi calculateAge tetap sama)

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

    if (text.isBlank() || text.lowercase(Locale.ROOT).contains("unknown")) return

    Box(
        modifier = Modifier
            .background(backgroundColor, shape = RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            maxLines = maxLines,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

@Composable
fun InteractionIcon(icon: ImageVector, count: String, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = { /* TODO: Aksi like, comment, dll. */ })
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = count,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier.size(64.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Post",
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Confirm Deletion")
        },
        text = {
            Text("Are you sure you want to delete this post? This action cannot be undone.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Delete", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

