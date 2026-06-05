package com.example.balatropedia.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.components._Balatro_Primary_Button
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.components._Profile_Menu_Button
import com.example.balatropedia.ui.theme._BALATRO_FONT
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
// Pantalla del perfil de usuario
fun _Profile_Screen(
    isAdmin: Boolean,
    onNavigateBack: () -> Unit,
    onLogoutSuccess: () -> Unit,
    onNavigateToRatings: () -> Unit,
    onNavigateToMetrics: () -> Unit,
    onNavigateToConfig: () -> Unit
) {
    val auth = remember { FirebaseAuth.getInstance() }
    val scope = rememberCoroutineScope()
    val db = remember { FirebaseFirestore.getInstance() }

    var vUsername by remember { mutableStateOf("Cargando...") }
    var vIsLoggingOut by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    vUsername = doc.getString("username") ?: if (isAdmin) "Admin" else "Usuario"
                }
                .addOnFailureListener {
                    vUsername = if (isAdmin) "Admin" else "Usuario"
                }
        } else {
            vUsername = "Invitado"
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().safeDrawingPadding(),
        topBar = {
            _Balatropedia_Header(comeBack = true, onBackClick = onNavigateBack, onProfileClick = {})
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF262931))
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2C323F))
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bienvenido,",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontFamily = _BALATRO_FONT,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "\"$vUsername\"",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontFamily = _BALATRO_FONT,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                _Balatro_Primary_Button(
                    text = "Configurar cuenta",
                    isLoading = false,
                    color = Color(0xFF00BCD4),
                    modifier = Modifier.fillMaxWidth(0.9f),
                    onClick = onNavigateToConfig
                )

                Spacer(modifier = Modifier.height(16.dp))

                _Balatro_Primary_Button(
                    text = "Cerrar sesión",
                    isLoading = vIsLoggingOut,
                    color = Color(0xFFCC1D1D),
                    modifier = Modifier.fillMaxWidth(0.9f),
                    onClick = {
                        vIsLoggingOut = true

                        scope.launch {
                            auth.signOut()

                            kotlinx.coroutines.delay(500)

                            vIsLoggingOut = false
                            onLogoutSuccess()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            _Profile_Menu_Button(
                text = "Tus puntuaciones",
                icon = Icons.Default.Star,
                iconTint = Color(0xFFFFD700),
                backgroundColor = Color(0xFF8C7B25),
                textColor = Color(0xFFFFDA46),
                onClick = onNavigateToRatings
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isAdmin) {
                _Profile_Menu_Button(
                    text = "Métricas",
                    icon = Icons.Default.PieChart,
                    iconTint = Color(0xFF75F5D9),
                    backgroundColor = Color(0xFF617973),
                    textColor = Color(0xFF8DF5EF),
                    onClick = onNavigateToMetrics
                )
            }
        }
    }
}