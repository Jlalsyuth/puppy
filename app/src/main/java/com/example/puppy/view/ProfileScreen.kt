package com.example.puppy.view

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.puppy.R
import com.example.puppy.data.UserRepository
import com.example.puppy.service.RetrofitInstance
import com.example.puppy.service.TokenManager
import com.example.puppy.view_model.ProfileViewModel

@Composable
fun ProfileScreen(navController: NavController) {

    val context = LocalContext.current
    val viewModel = remember {
        val tokenManager = TokenManager(context)
        val repository = UserRepository(
            api = RetrofitInstance.userService,
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
    val userRole = "Regular"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_default_user_avatar),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .border(
                                4.dp,
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                                CircleShape
                            )
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = userRole,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
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
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                ProfileOption(title = "Change Email")
                ProfileOption(title = "Change Password")
                ProfileOption(title = "Settings")
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
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                ProfileOption(title = "FAQ")
                ProfileOption(title = "Terms & Conditions")
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Tombol Log Out
        item {
            Button(
                onClick = {
                    // --- PERBAIKAN DI SINI ---
                    // Gunakan nama fungsi yang benar: clearData()
                    TokenManager(context).clearData()

                    navController.navigate("welcome") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp, horizontal = 24.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(
                    text = "Log Out",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileOption(title: String, onClick: () -> Unit = { /* TODO */ }) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(56.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Go to $title",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
