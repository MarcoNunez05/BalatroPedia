package com.example.balatropedia.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.components._Balatro_Input
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.ui.theme._BALATRO_FONT
import com.google.firebase.auth.FirebaseAuth

@Composable
fun _Login_Screen(
    onNavigateBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        topBar = {
            _Balatropedia_Header(
                comeBack = true,
                onBackClick = onNavigateBack,
                onProfileClick = { }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E222B))
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2C323F))
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Iniciar sesión",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontFamily = _BALATRO_FONT,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                _Balatro_Input(
                    label = "Correo electrónico",
                    value = email,
                    onValueChange = { email = it; errorMessage = null }
                )

                Spacer(modifier = Modifier.height(20.dp))

                _Balatro_Input(
                    label = "Contraseña",
                    value = password,
                    isPassword = true,
                    onValueChange = { password = it; errorMessage = null }
                )

                errorMessage?.let { msg ->
                    Text(
                        text = msg,
                        color = Color(0xFFE57373),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            isLoading = true
                            errorMessage = null

                            // Petición a Firebase
                            auth.signInWithEmailAndPassword(email.trim(), password.trim())
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        onLoginSuccess()
                                    } else {
                                        errorMessage = task.exception?.localizedMessage ?: "Error al iniciar sesión"
                                    }
                                }
                        } else {
                            errorMessage = "Por favor, llena todos los campos."
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1BA345)),
                    shape = RoundedCornerShape(6.dp),
                    enabled = !isLoading // Desactiva el botón si está cargando
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(text = "Iniciar sesión", color = Color.White, fontSize = 30.sp, fontFamily = _BALATRO_FONT)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))


                val textoRegistro = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White, fontSize = 20.sp, fontFamily = _BALATRO_FONT)) { append("¿No tienes una cuenta? ") }
                    withStyle(style = SpanStyle(color = Color(0xFF00BFFF),
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = _BALATRO_FONT)) { append("Registrate") }
                }

                Text(
                    text = textoRegistro,
                    modifier = Modifier.clickable { if (!isLoading) onRegisterClick() }
                )
            }
        }
    }
}