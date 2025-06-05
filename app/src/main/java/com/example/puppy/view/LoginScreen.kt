package com.example.puppy.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.puppy.R
import com.example.puppy.data.UserRepository
import com.example.puppy.service.RetrofitInstance
import com.example.puppy.service.TokenManager
import com.example.puppy.view_model.LoginViewModel

@Composable
fun LoginScreen(
    context: Context, // Context masih dibutuhkan untuk TokenManager & UserRepository di sini
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    // Pastikan menggunakan TokenManager & UserRepository dari package puppy
    val tokenManager = remember { TokenManager(context) }
    val userRepository = remember {
        UserRepository(
            api = RetrofitInstance.userService, // Ini akan merujuk ke UserService dari com.example.puppy.service
            tokenManager = tokenManager,
            context = context
        )
    }
    val viewModel = remember { LoginViewModel(userRepository) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = R.drawable.puppy_logo), // Perubahan: Ganti dengan logo Puppy Anda
            contentDescription = "Puppy App Logo",
            modifier = Modifier
                .height(160.dp)
                .padding(vertical = 16.dp)
        )

        OutlinedTextField(
            value = viewModel.email,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary, // Perubahan
                unfocusedBorderColor = MaterialTheme.colorScheme.outline, // Perubahan
                cursorColor = MaterialTheme.colorScheme.primary // Perubahan
            ),
            onValueChange = { viewModel.email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(24.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = viewModel.password,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary, // Perubahan
                unfocusedBorderColor = MaterialTheme.colorScheme.outline, // Perubahan
                cursorColor = MaterialTheme.colorScheme.primary // Perubahan
            ),
            onValueChange = { viewModel.password = it },
            label = { Text("Password") },
            visualTransformation = if (viewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (viewModel.passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { viewModel.passwordVisible = !viewModel.passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle Password Visibility", tint = MaterialTheme.colorScheme.onSurfaceVariant) // Perubahan: tint
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(24.dp),
            singleLine = true
        )

        if (viewModel.errorMessage.isNotEmpty()) {
            Text(
                viewModel.errorMessage,
                color = MaterialTheme.colorScheme.error, // Perubahan
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (viewModel.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(top = 16.dp),
                color = MaterialTheme.colorScheme.primary // Perubahan
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login(onLoginSuccess) },
            enabled = !viewModel.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Perubahan
                contentColor = MaterialTheme.colorScheme.onPrimary // Perubahan
            )
        ) {
            Text("Sign In", color = Color.White) // Warna teks otomatis dari contentColor
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text("Don't have an account? Sign Up", color = MaterialTheme.colorScheme.primary) // Perubahan
        }
    }
}