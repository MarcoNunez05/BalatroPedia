package com.example.balatropedia.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.components._Balatro_Input
import com.example.balatropedia.components._Balatro_Selector
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.ui.theme._BALATRO_FONT
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun _Register_Screen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
) {
    val auth = FirebaseAuth.getInstance()
    val scrollState = rememberScrollState()

    val listaPaises = listOf("Argentina", "Chile", "Colombia", "España", "México", "Perú", "Otros")
    val listaEdades = (14..99).map { it.toString() }

    // Estados de los campos
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var pais by remember { mutableStateOf("Selecciona un país") }
    var edad by remember { mutableStateOf("18") }

    // Estados de UI
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize().safeDrawingPadding(),
        topBar = {
            _Balatropedia_Header(
                comeBack = true,
                onBackClick = onNavigateBack,
                onProfileClick = { }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E222B))
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registra una cuenta",
                color = Color.White,
                fontSize = 36.sp,
                fontFamily = _BALATRO_FONT,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            _Balatro_Input(label = "Nombre de usuario", value = username, onValueChange = { username = it })
            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Input(label = "Correo electrónico", value = email, onValueChange = { email = it })
            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Input(label = "Contraseña", value = password, isPassword = true, onValueChange = { password = it })
            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Input(label = "Confirmar contraseña", value = confirmPassword, isPassword = true, onValueChange = { confirmPassword = it })
            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Selector(
                label = "País",
                value = pais,
                options = listaPaises,
                onOptionSelected = { pais = it; errorMessage = null }
            )
            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Selector(
                label = "Edad",
                value = edad,
                options = listaEdades,
                onOptionSelected = { edad = it; errorMessage = null },
            )

            errorMessage?.let {
                Text(text = it, color = Color(0xFFE57373), fontSize = 20.sp, modifier = Modifier.padding(top = 16.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (email.isNotBlank() && password == confirmPassword && username.isNotBlank() && pais != "Selecciona un país") {
                        isLoading = true
                        errorMessage = null

                        auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val db = FirebaseFirestore.getInstance()
                                    val userId = auth.currentUser?.uid

                                    val userProfile = hashMapOf(
                                        "username" to username.trim(),
                                        "email" to email.trim(),
                                        "pais" to pais,
                                        "edad" to edad,
                                        "rol" to "user"
                                    )

                                    if (userId != null) {
                                        db.collection("users").document(userId)
                                            .set(userProfile)
                                            .addOnSuccessListener {
                                                isLoading = false
                                                onRegisterSuccess()
                                            }
                                            .addOnFailureListener { e ->
                                                isLoading = false
                                                errorMessage = "Cuenta creada, pero error al guardar perfil: ${e.localizedMessage}"
                                            }
                                    }
                                } else {
                                    isLoading = false
                                    errorMessage = task.exception?.localizedMessage ?: "Error al registrar"
                                }
                            }
                    } else if (username.isBlank()) {
                        errorMessage = "Por favor, introduce un nombre de usuario"
                    } else if (pais == "Selecciona un país") {
                        errorMessage = "Por favor, selecciona tu país"
                    } else if (password != confirmPassword) {
                        errorMessage = "Las contraseñas no coinciden"
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFFF)),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Registrarse", color = Color.White, fontSize = 20.sp, fontFamily = _BALATRO_FONT)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}