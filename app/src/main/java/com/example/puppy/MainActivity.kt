package com.example.puppy // Perubahan: Package

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home // Atau ikon yang lebih relevan untuk anjing jika ada
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets // Ikon hewan peliharaan umum, cocok untuk feed
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme // Ditambahkan
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
// import androidx.compose.ui.graphics.Color // Dihapus jika semua dari tema
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // Tetap ada sesuai kode asli
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
// Import untuk NavHost dan composable dari accompanist-navigation-animation
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable // composable dari accompanist
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
// Import Tema Puppy
import com.example.puppy.ui.theme.PuppyTheme // Perubahan: Nama Tema
// Import semua layar dari package puppy.view
import com.example.puppy.view.AddDogProfile // Perubahan
import com.example.puppy.view.BookHealthScreen
import com.example.puppy.view.DogProfileScreen // Perubahan
import com.example.puppy.view.ChatScreen
import com.example.puppy.view.ProductDetailScreen
import com.example.puppy.view.FeedScreen
import com.example.puppy.view.HealthScreen
import com.example.puppy.view.HomeScreen
import com.example.puppy.view.LoginScreen
import com.example.puppy.view.MarketScreen
import com.example.puppy.view.PostFeedScreen
import com.example.puppy.view.ProfileScreen
import com.example.puppy.view.RegisterScreen
import com.example.puppy.view.SplashScreen
import com.example.puppy.view.WelcomeScreen

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Catatan: Thread.sleep() di main thread bisa menyebabkan ANR.
        // Jika SplashScreen.kt Anda sudah menangani delay, ini mungkin tidak perlu.
        Thread.sleep(3000) // Sesuai kode asli
        installSplashScreen() // Sesuai kode asli

        setContent {
            PuppyTheme { // Perubahan: Menggunakan PuppyTheme
                val navController = rememberAnimatedNavController()
                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStack?.destination?.route
                // Daftar route yang menampilkan bottom bar tetap sama
                val showBottomBar = currentRoute in listOf("home", "profile", "feed", "market", "health")
                val authRoutes = listOf("login", "register", "welcome", "splash") // Tambahkan splash ke authRoutes jika animasinya berlaku

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            CustomBottomBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    AnimatedNavHost(
                        navController = navController,
                        startDestination = "splash", // Start destination tetap splash
                        modifier = Modifier.padding(innerPadding),
                        // Transisi animasi tetap sama sesuai kode asli
                        enterTransition = {
                            val from = initialState.destination.route
                            val to = targetState.destination.route
                            if (from in authRoutes && to in authRoutes && from != to) { // Cek from != to untuk splash -> welcome/login
                                slideInVertically(initialOffsetY = { 1000 }) + fadeIn(animationSpec = tween(300))
                            } else {
                                fadeIn(animationSpec = tween(300))
                            }
                        },
                        exitTransition = {
                            val from = initialState.destination.route
                            val to = targetState.destination.route
                            if (from in authRoutes && to in authRoutes && from != to) {
                                slideOutVertically(targetOffsetY = { 1000 }) + fadeOut(animationSpec = tween(300))
                            } else {
                                fadeOut(animationSpec = tween(300))
                            }
                        },
                        popEnterTransition = { fadeIn(animationSpec = tween(300)) }, // Disederhanakan jika tidak ada animasi khusus pop antar auth
                        popExitTransition = { fadeOut(animationSpec = tween(300)) }  // Disederhanakan
                    )
                    {
                        composable("splash") {
                            val context = LocalContext.current
                            SplashScreen(navController, context)
                        }
                        composable("welcome") {
                            WelcomeScreen(
                                onNavigateToLogin = { navController.navigate("login") },
                                onNavigateToRegister = { navController.navigate("register") }
                            )
                        }
                        composable("login") {
                            val context = LocalContext.current
                            LoginScreen(
                                context = context,
                                onNavigateToRegister = { navController.navigate("register") },
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true } // Pop up ke start destination graph
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = { navController.popBackStack() }
                            )
                        }
                        composable("home") {
                            val context = LocalContext.current
                            HomeScreen(navController, context)
                        }
                        composable("profile") {
                            val context = LocalContext.current
                            ProfileScreen(navController, context)
                        }
                        composable("feed") {
                            val context = LocalContext.current
                            FeedScreen(navController, context)
                        }
                        composable("market") {
                            MarketScreen(navController) // Context tidak dibutuhkan MarketScreen statis
                        }
                        composable("health") {
                            val context = LocalContext.current
                            HealthScreen(navController, context)
                        }
                        composable("dogprofile") { // Perubahan: Route catprofile -> dogprofile
                            val context = LocalContext.current
                            DogProfileScreen(navController, context) // Perubahan: Memanggil DogProfileScreen
                        }
                        composable("upload") { // Route untuk PostFeedScreen
                            val context = LocalContext.current
                            PostFeedScreen(navController, context)
                        }
                        composable("adddogprofile") { // Perubahan: Route addcatprofile -> adddogprofile
                            val context = LocalContext.current
                            AddDogProfile(navController, context) // Perubahan: Memanggil AddDogProfile
                        }
                        // Route bookhealth bisa menerima argumen ID dokter
                        composable("bookhealth/{doctorId}") { backStackEntry ->
                            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                            // Jika BookHealthScreen tidak lagi butuh doctorId dari route, ini bisa disederhanakan
                            BookHealthScreen(navController, LocalContext.current) // Sesuaikan jika BookHealthScreen perlu doctorId
                        }
                        // Jika BookHealthScreen tidak pakai argumen dari route HealthScreen:
                        // composable("bookhealth") {
                        //     BookHealthScreen(navController, LocalContext.current)
                        // }
                        composable("chat") {
                            ChatScreen(navController)
                        }
                        composable("detail") {
                            ProductDetailScreen(navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf("home", "health", "feed", "market", "profile")
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Menggunakan Surface untuk bottom bar agar bisa menerapkan elevation dan warna dari tema
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp, // Memberi sedikit shadow
        color = MaterialTheme.colorScheme.surfaceContainer // Warna latar bottom bar dari tema
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround, // Distribusi merata
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp), // Padding vertikal
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { screen ->
                val isSelected = currentRoute == screen
                val icon = when (screen) {
                    "home" -> Icons.Filled.Home
                    "health" -> Icons.Filled.LocalHospital
                    "feed" -> Icons.Filled.Pets // Ikon hewan peliharaan
                    "market" -> Icons.Filled.ShoppingBasket
                    "profile" -> Icons.Filled.Person
                    else -> Icons.Filled.Home // Fallback
                }
                val iconTint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else androidx.compose.ui.graphics.Color.Transparent


                Box(
                    modifier = Modifier
                        .size(56.dp) // Ukuran area klik
                        .clip(CircleShape)
                        .background(backgroundColor)
                        .clickable {
                            if (!isSelected) {
                                navController.navigate(screen) {
                                    popUpTo(navController.graph.findStartDestination().id) { // Pop up ke start destination dari graph saat ini
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = screen,
                        tint = iconTint, // Warna ikon dari tema
                        modifier = Modifier.size(26.dp) // Ukuran ikon
                    )
                }
            }
        }
    }
}

// Helper untuk NavController.graph.findStartDestination().id (jika tidak ada di versi navigation Anda)
// Anda mungkin perlu import androidx.navigation.NavGraph.Companion.findStartDestination
// import androidx.navigation.NavGraph.Companion.findStartDestination