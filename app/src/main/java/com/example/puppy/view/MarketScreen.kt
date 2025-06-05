package com.example.puppy.view // Perubahan: Package

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // Wildcard
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items // Untuk LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed // Untuk LazyRow banner
import androidx.compose.foundation.lazy.items // Untuk LazyRow categories
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.* // Wildcard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
// import androidx.compose.ui.graphics.Color // Dihapus
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.puppy.R // Perubahan

// --- DATA STATIS DIDEFINISIKAN DI SINI ---

// Data class untuk item produk statis
data class ProductItemInfo(
    val id: String,
    val name: String,
    val price: String,
    @DrawableRes val imageResId: Int
)

// Daftar produk statis untuk anjing
val staticPuppyProductList = listOf(
    ProductItemInfo("prod1", "Dog Food - Royal Canine Mini", "Rp 150k", R.drawable.product_dog_food_royal),
    ProductItemInfo("prod2", "Chew Toy - Bone Shape", "Rp 55k", R.drawable.product_chew_toy),
    ProductItemInfo("prod3", "Comfy Dog Bed - Medium", "Rp 250k", R.drawable.product_dog_bed),
    ProductItemInfo("prod4", "Adjustable Dog Leash - Blue", "Rp 75k", R.drawable.product_dog_leash),
    ProductItemInfo("prod5", "Puppy Training Pads (50pcs)", "Rp 90k", R.drawable.product_training_pads),
    ProductItemInfo("prod6", "Stainless Steel Dog Bowl", "Rp 40k", R.drawable.product_dog_bowl),
    ProductItemInfo("prod7", "Dog Grooming Brush", "Rp 65k", R.drawable.product_grooming_brush),
    ProductItemInfo("prod8", "Organic Dog Treats - Salmon", "Rp 85k", R.drawable.product_dog_treats)
)

// Daftar banner statis (gunakan ID drawable yang relevan untuk anjing)
val staticPuppyBanners = listOf(
    R.drawable.banner_dog_promo_1, R.drawable.banner_dog_food_2, R.drawable.banner_dog_toys_3,
    R.drawable.banner_dog_promo_1, R.drawable.banner_dog_food_2 // Contoh duplikasi seperti di kode asli
)

// Data class dan Daftar kategori statis (gunakan ID drawable yang relevan untuk anjing)
data class CategoryItemInfo(@DrawableRes val imageResId: Int, val name: String)
val staticPuppyCategories = listOf(
    CategoryItemInfo(R.drawable.category_dog_food_icon, "Food"),
    CategoryItemInfo(R.drawable.category_dog_toys_icon, "Toys"),
    CategoryItemInfo(R.drawable.category_dog_accessories_icon, "Accessories"),
    CategoryItemInfo(R.drawable.category_dog_grooming_icon, "Grooming"),
    CategoryItemInfo(R.drawable.category_dog_health_icon, "Health") // Contoh duplikasi seperti di kode asli
)
// --- AKHIR DARI DATA STATIS ---


@Composable
fun MarketScreen(navController: NavController, context: Context = LocalContext.current) {

    val banners = remember { staticPuppyBanners }
    // Mengambil 5 kategori pertama untuk dicocokkan dengan jumlah di kode asli Anda jika itu penting
    val categoriesToDisplay = remember { staticPuppyCategories.take(5) }
    val productsToDisplay = remember { staticPuppyProductList.take(8) } // Ambil 8 produk sesuai kode asli

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background) // Perubahan: Warna latar utama
    ) {
        // Search bar & icons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 16.dp) // Padding atas sesuai asli
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search puppy products...") }, // Perubahan: Placeholder
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp), // Sesuai asli
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors( // Theming
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(8.dp)) // Sesuai asli
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorites", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Banner
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp), // Sesuai asli
            modifier = Modifier.padding(vertical = 16.dp) // Sesuai asli
        ) {
            itemsIndexed(banners) { index, bannerResId -> // Menggunakan itemsIndexed sesuai asli
                Image(
                    painter = painterResource(id = bannerResId),
                    contentDescription = "Promotional Banner for Dogs", // Deskripsi diubah
                    modifier = Modifier
                        .padding(end = if (index != banners.lastIndex) 12.dp else 0.dp) // Sesuai asli
                        .height(140.dp) // Tinggi banner disesuaikan agar tidak terlalu besar
                        .width(280.dp)  // Lebar banner agar tidak terlalu besar
                        .clip(RoundedCornerShape(16.dp)), // Bentuk banner lebih modern
                    contentScale = ContentScale.Crop // Crop agar gambar mengisi
                )
            }
        }

        // Top Categories Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp), // Sesuai asli
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically // Ditambahkan untuk alignment teks
        ) {
            Text(
                "Top Categories",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp), // Sesuai asli
                color = MaterialTheme.colorScheme.onBackground // Warna dari tema
            )
            Text(
                "See All",
                color = MaterialTheme.colorScheme.primary, // Perubahan: Warna "See All"
                fontSize = 14.sp, // Sesuai asli
                modifier = Modifier.clickable { /* TODO */ }
            )
        }

        // Categories
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp), // Sesuai asli
            modifier = Modifier.padding(vertical = 16.dp), // Sesuai asli
            horizontalArrangement = Arrangement.spacedBy(12.dp) // Sesuai asli
        ) {
            items(categoriesToDisplay) { categoryItem -> // Menggunakan items dari LazyRow biasa
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp) // Sesuai asli
                            .shadow(2.dp, RoundedCornerShape(24.dp)) // Sesuai asli
                            .clip(RoundedCornerShape(24.dp)) // Sesuai asli
                            .background(MaterialTheme.colorScheme.surfaceContainerLowest) // Perubahan: Warna latar kategori
                            .clickable { /* TODO */ }
                            .padding(12.dp), // Padding dalam disesuaikan agar ikon tidak terlalu besar
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = categoryItem.imageResId),
                            contentDescription = categoryItem.name, // Deskripsi kategori
                            modifier = Modifier.size(40.dp) // Ukuran ikon kategori disesuaikan
                        )
                    }
                    Text(
                        categoryItem.name,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Product Grid Title
        Text(
            "Recommended Puppy Products", // Perubahan: Teks judul
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp), // Sesuai asli
            color = MaterialTheme.colorScheme.onBackground, // Warna dari tema
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp) // Sesuai asli
        )

        // Product Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // Sesuai asli
            verticalArrangement = Arrangement.spacedBy(12.dp), // Sesuai asli
            horizontalArrangement = Arrangement.spacedBy(12.dp), // Sesuai asli
            modifier = Modifier
                .height(900.dp) // Sesuai kode asli, PERHATIAN: tinggi hardcoded bisa menyebabkan masalah layout
                .padding(horizontal = 24.dp), // Sesuai asli
            contentPadding = PaddingValues(bottom = 24.dp) // Tambahkan padding bawah untuk item terakhir
        ) {
            items(productsToDisplay) { product -> // Menggunakan items dari LazyVerticalGrid biasa
                ProductCardItem(navController, product) // Menggunakan Composable ProductCardItem yang diadaptasi
            }
        }
        Spacer(modifier = Modifier.height(24.dp)) // Sesuai asli
    }
}

@Composable
fun ProductCardItem(navController: NavController, product: ProductItemInfo) { // Nama Composable disesuaikan
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("detail/${product.id}") }, // Navigasi dengan product.id
        shape = RoundedCornerShape(12.dp), // Sesuai asli
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Perubahan: Warna kartu
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Sesuai asli
    ) {
        Column {
            Image(
                painter = painterResource(id = product.imageResId), // Dari data produk
                contentDescription = product.name, // Dari data produk
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp), // Sesuai asli
                contentScale = ContentScale.Crop // Sesuai asli
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp) // Sesuai asli
            ) {
                Text(
                    product.name, // Dari data produk
                    style = MaterialTheme.typography.bodyLarge, // Sesuai asli (atau titleSmall dari adaptasi sebelumnya)
                    color = MaterialTheme.colorScheme.onSurface, // Warna dari tema
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    product.price, // Dari data produk
                    color = MaterialTheme.colorScheme.onSurfaceVariant, // Perubahan: Warna harga (sebelumnya Gray)
                    style = MaterialTheme.typography.bodyMedium // Style dari tema
                )
                // Ikon favorit tetap dipertahankan sesuai kode asli
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Add to favorites",
                    modifier = Modifier.align(Alignment.End),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant // Warna ikon
                )
            }
        }
    }
}