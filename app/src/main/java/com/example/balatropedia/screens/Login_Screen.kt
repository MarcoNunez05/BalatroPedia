// Última modificación: 01/06/2026
// Autor: Marco Núñez

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
import com.example.balatropedia.components._Balatro_Primary_Button
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.ui.theme._BALATRO_FONT
import com.example.balatropedia.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
// Pantalla de Login
fun _Login_Screen(
    onNavigateBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var vIsLoading by remember { mutableStateOf(false) }
    var vErrorMessage by remember { mutableStateOf<String?>(null) }

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
                    onValueChange = { email = it; vErrorMessage = null }
                )

                Spacer(modifier = Modifier.height(20.dp))

                _Balatro_Input(
                    label = "Contraseña",
                    value = password,
                    isPassword = true,
                    onValueChange = { password = it; vErrorMessage = null }
                )

                vErrorMessage?.let { msg ->
                    Text(
                        text = msg,
                        color = Color(0xFFE57373),
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                _Balatro_Primary_Button(
                    text = "Iniciar sesión",
                    isLoading = vIsLoading,
                    color = Color(0xFF1BA345),
                    onClick = {
                        when {
                            email.isBlank() -> {
                                vErrorMessage = "El correo electrónico no puede estar vacío."
                            }
                            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                                vErrorMessage = "Por favor, introduce un correo válido."
                            }
                            password.isBlank() -> {
                                vErrorMessage = "La contraseña no puede estar vacía."
                            }
                            else -> {
                                vIsLoading = true
                                vErrorMessage = null

                                viewModel._Iniciar_Sesion(
                                    email = email,
                                    pass = password,
                                    onSuccess = {
                                        vIsLoading = false
                                        onLoginSuccess()
                                    },
                                    onError = { errorText ->
                                        vIsLoading = false
                                        vErrorMessage = errorText
                                    }
                                )
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                val textoRegistro = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White, fontSize = 20.sp, fontFamily = _BALATRO_FONT)) { append("¿No tienes una cuenta? ") }
                    withStyle(style = SpanStyle(color = Color(0xFF00BFFF),
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = _BALATRO_FONT)) { append("Registrate") }
                }

                Text(
                    text = textoRegistro,
                    modifier = Modifier.clickable { if (!vIsLoading) onRegisterClick() }
                )
            }
        }
    }
}