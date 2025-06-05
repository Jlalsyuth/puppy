package com.example.puppy.view

// import android.content.Context // Tidak dipakai langsung di Composable utama
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // Wildcard
import androidx.compose.foundation.lazy.LazyColumn // Digunakan di VariantSelector
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Menggunakan auto-mirrored
import androidx.compose.material.icons.outlined.ChatBubbleOutline  // Menggunakan auto-mirrored
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.* // Wildcard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
// import androidx.compose.ui.graphics.Color // Dihapus
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.puppy.R // Perubahan
// import com.example.meowspace.model.MidtransRequest // Dihapus
// import com.example.meowspace.service.RetrofitInstance // Dihapus jika tidak ada API call langsung di sini
// kotlinx.coroutines.* Dihapus jika tidak ada API call langsung di sini

// Terima productId dari NavGraph
@Composable
fun ProductDetailScreen(navController: NavController, productId: String? = null) { // Tambah productId
    var isExpanded by remember { mutableStateOf(false) }
    var showPopup by remember { mutableStateOf(false) }

    // Data Produk Statis (Contoh, sesuaikan dengan produk anjing)
    // Idealnya ini diambil berdasarkan productId
    val productName = "Comfy Dog Bed - Large"
    val productPrice = "Rp 289.000"
    val productRating = "4.8"
    val productSoldCount = "3.2k Sold"
    val productReviewsCount = "1.5k Reviews"
    val productImageRes = R.drawable.product_dog_bed // PERHATIAN: Ganti dengan drawable Anda
    val storeName = "Puppy Paradise Store"
    val storeLocation = "Jl. Sahabat Anjing No. 1, Jakarta"
    val storeLogoRes = R.drawable.puppy_logo // PERHATIAN: Ganti dengan drawable Anda
    val productDescription = "Tempat tidur anjing super nyaman dan empuk yang dirancang untuk istirahat maksimal sahabat setia Anda! Terbuat dari bahan premium yang lembut, tahan lama, dan mudah dibersihkan."
    val productFeatures = listOf(
        "Material: Kain Oxford + Isian Dakron Grade A",
        "Ukuran: Large (90cm x 70cm)",
        "Warna: Charcoal Grey, Navy Blue, Forest Green",
        "Cocok untuk: Anjing ras sedang hingga besar",
        "Alas anti-slip",
        "Mudah dicuci dan cepat kering"
    )
    val productBenefits = listOf(
        "Memberikan kenyamanan tidur superior untuk anjing.",
        "Mendukung kesehatan sendi dan tulang.",
        "Desain elegan yang cocok untuk interior rumah Anda."
    )


    Scaffold(
        bottomBar = {
            BottomBar( // BottomBar kustom
                onBuyClicked = { showPopup = true }, // Aksi saat tombol beli utama diklik
                modifier = Modifier.fillMaxWidth()
                // Background sudah diatur di dalam Composable BottomBar
            )
        },
        containerColor = MaterialTheme.colorScheme.background // Warna latar Scaffold
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
            // Background sudah diatur oleh Scaffold
        ) {

            // Gambar & Icon Atas
            Box {
                Image(
                    painter = painterResource(id = productImageRes), // Gambar produk
                    contentDescription = productName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp), // Tinggi gambar produk disesuaikan
                    contentScale = ContentScale.Crop
                )

                Row( // Ikon navigasi dan keranjang di atas gambar
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface // Warna ikon
                        )
                    }
                    IconButton(
                        onClick = { /* TODO: Navigasi ke Keranjang */ },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = MaterialTheme.colorScheme.onSurface // Warna ikon
                        )
                    }
                }
            }

            // Indikator Halaman Gambar (jika ada beberapa gambar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp), // Padding disesuaikan
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(5) { index -> // Contoh 5 indikator
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .padding(2.dp)
                            .background(
                                if (index == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), // Warna indikator
                                shape = CircleShape
                            )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Detail Produk
            Column(modifier = Modifier.padding(horizontal = 20.dp)) { // Padding horizontal disesuaikan
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = productName,
                        style = MaterialTheme.typography.headlineSmall, // Style dari tema
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground, // Warna teks
                        modifier = Modifier.weight(1f)
                    )
                    // Bagian Rating dan Favorit
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(productRating, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = MaterialTheme.colorScheme.tertiary, // Warna bintang
                            modifier = Modifier.size(18.dp) // Ukuran disesuaikan
                        )
                        Spacer(Modifier.width(12.dp))
                        // Icon Favorit (contoh)
                        var isFavorited by remember { mutableStateOf(false) }
                        IconButton(onClick = { isFavorited = !isFavorited }, modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder, // Atau Icons.Filled.Favorite jika isFavorited
                                contentDescription = "Favorite",
                                tint = if(isFavorited) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant // Warna ikon favorit
                            )
                        }
                        // Text("7.5k", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) // Jumlah favorit jika ada
                    }
                }

                Text(
                    text = productPrice,
                    style = MaterialTheme.typography.titleLarge, // Style dari tema
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary, // Warna harga
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp), // Jarak lebih besar
                    modifier = Modifier.padding(top = 8.dp) // Padding atas
                ) {
                    Text(productSoldCount, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                    Text(productReviewsCount, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(Modifier.height(20.dp)) // Jarak lebih besar

            // Informasi Toko
            Surface( // Menggunakan Surface untuk bagian info toko
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceContainerLowest // Warna latar yang sedikit beda
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = storeLogoRes), // Logo Toko
                            contentDescription = "$storeName Logo",
                            modifier = Modifier
                                .size(48.dp) // Ukuran disesuaikan
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant) // Placeholder bg
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(storeName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                storeLocation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }


            Spacer(Modifier.height(20.dp))

            // Deskripsi Produk
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    productDescription,
                    style = MaterialTheme.typography.bodyMedium, // Style dari tema
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 20.sp // Kerapatan baris
                )

                Spacer(Modifier.height(12.dp))

                Text("Fitur Utama:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(4.dp))

                productFeatures.forEach {
                    Text("• $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 8.dp))
                }

                // Bagian yang bisa di-expand
                AnimatedVisibility(visible = isExpanded) {
                    Column {
                        Spacer(Modifier.height(12.dp))
                        Text("Manfaat:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(Modifier.height(4.dp))
                        productBenefits.forEach {
                            Text("• $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 8.dp))
                        }

                        // Informasi tambahan lainnya bisa diletakkan di sini
                        Spacer(Modifier.height(12.dp))
                        Text("Ketersediaan:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Text(
                            "✔ Tersedia – Siap dikirim dalam 1–2 hari kerja",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedButton( // Menggunakan OutlinedButton untuk "Continue Reading"
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline) // Border dari tema
                ) {
                    Text(
                        text = if (isExpanded) "Show Less" else "Continue Reading...",
                        color = MaterialTheme.colorScheme.primary // Warna teks dari tema
                    )
                }
                Spacer(Modifier.height(24.dp)) // Spacer sebelum akhir konten utama
            }
        }

        // Pop-up untuk VariantSelector
        AnimatedVisibility(
            visible = showPopup,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 200))
        ) {
            Box( // Overlay semi-transparan
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)) // Warna scrim dari tema
                    .clickable { showPopup = false } // Menutup pop-up jika diklik di luar
            ) {
                Box( // Kontainer Bottom Sheet
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)) // Bentuk sudut atas
                        .background(MaterialTheme.colorScheme.surfaceContainer) // Warna latar bottom sheet
                        .clickable(enabled = false) {} // Mencegah klik di dalam bottom sheet menutupnya
                        .padding(24.dp)
                ) {
                    VariantSelector(navController, productPrice = productPrice) // Harga produk diteruskan
                }
            }
        }
    }
}

@Composable
fun VariantSelector(navController: NavController, productPrice: String) { // Menerima harga produk
    var selectedVariant by remember { mutableStateOf("Charcoal Grey") } // Varian default disesuaikan
    var quantity by remember { mutableStateOf(1) }
    // Varian produk disesuaikan untuk anjing
    val variants = listOf("Charcoal Grey", "Navy Blue", "Forest Green", "Sandy Beige")

    // Menggunakan Column biasa karena LazyColumn di dalam Box dengan tinggi terbatas bisa kompleks
    Column(
        modifier = Modifier
            // .fillMaxHeight(0.85f) // Dihilangkan, biarkan Column menyesuaikan kontennya
            .fillMaxWidth()
    ) {
        Text(
            "Pilih Varian",
            style = MaterialTheme.typography.titleLarge, // Style dari tema
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(16.dp))

        // Varian dalam baris, bisa di-wrap jika banyak
        val rows = variants.chunked(2)
        rows.forEach { rowVariants ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                rowVariants.forEach { variant ->
                    val isSelected = variant == selectedVariant
                    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceBright
                    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                            .background(containerColor)
                            .clickable { selectedVariant = variant }
                            .padding(horizontal = 12.dp, vertical = 10.dp), // Padding disesuaikan
                        contentAlignment = Alignment.Center
                    ) {
                        Text(variant, style = MaterialTheme.typography.bodyMedium, color = contentColor)
                    }
                }
                // Jika ada sisa 1 item di baris terakhir, tambahkan Spacer untuk mengisi ruang
                if (rowVariants.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text(
            "Quantity",
            style = MaterialTheme.typography.titleMedium, // Style dari tema
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceBright) // Warna latar quantity selector
        ) {
            IconButton(onClick = { if (quantity > 1) quantity-- }) {
                Text("-", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Text(
                quantity.toString(),
                modifier = Modifier.padding(horizontal = 20.dp), // Padding lebih besar
                style = MaterialTheme.typography.titleMedium, // Style dari tema
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = { quantity++ }) {
                Text("+", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(Modifier.height(32.dp)) // Jarak lebih besar
        Button(
            onClick = {
                // Logika Pembayaran Midtrans Dihapus
                Log.d("ProductDetail", "Confirm Purchases clicked. Variant: $selectedVariant, Quantity: $quantity. Payment logic removed/mocked.")
                // Navigasi ke layar konfirmasi tiruan atau tampilkan pesan Toast/Snackbar
                // Contoh:
                navController.navigate("#") {
                    // Opsi popUpTo
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp), // Tinggi tombol disesuaikan
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Warna dari tema
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Confirm Purchases (Mock)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold) // Teks disesuaikan
        }
    }
}

@Composable
fun BottomBar(modifier: Modifier = Modifier, onBuyClicked: () -> Unit = {}) {
    Surface( // Menggunakan Surface untuk BottomBar agar bisa diberi shadow/tonalElevation
        tonalElevation = 3.dp, // Memberi sedikit elevasi
        // shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp), // Opsional: bentuk sudut atas
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer // Warna latar bottom bar
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp), // Padding disesuaikan
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row( // Ikon-ikon aksi
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp), // Jarak antar ikon
                modifier = Modifier.weight(1f) // Agar ikon mengisi ruang tersedia sebelum tombol "Buy Now"
            ) {
                val iconColor = MaterialTheme.colorScheme.onSurfaceVariant
                Icon(imageVector = Icons.Outlined.ChatBubbleOutline, contentDescription = "Chat Seller", tint = iconColor, modifier = Modifier.clickable { /*TODO*/ }.padding(4.dp))
                Divider(modifier = Modifier.height(24.dp).width(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Add to Cart", tint = iconColor, modifier = Modifier.clickable { /*TODO*/ }.padding(4.dp))
                // Icon Share bisa diletakkan di sini atau di atas (dekat tombol back)
                // Divider(modifier = Modifier.height(24.dp).width(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
                // Icon(imageVector = Icons.Default.Share, contentDescription = "Share Product", tint = iconColor, modifier = Modifier.clickable { /*TODO*/ }.padding(4.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button( // Tombol Beli Sekarang
                onClick = { onBuyClicked() }, // Memanggil lambda untuk menampilkan pop-up
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Warna dari tema
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp) // Padding tombol
            ) {
                Text("Buy Now!", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}