package com.example.balatropedia.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.ui.theme._BALATRO_FONT
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class UserRating(
    val id: String,
    val nombre: String,
    val imagenUrl: String,
    val puntuacion: Int
)

@Composable
// Pantalla de visualización de perfil de usuario
fun _Profile_Screen(
    onNavigateBack: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var username by remember { mutableStateOf("Cargando...") }

    val ratedItems = remember {
        listOf(
            UserRating("1", "Gluttonous Joker", "url_gluttonous", 3),
            UserRating("2", "Lusty Joker", "url_lusty", 2),
            UserRating("3", "Ectoplasm", "url_ecto", 5)
        )
    }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                username = doc.getString("username") ?: "Usuario"
            }
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
                .background(Color(0xFF1E222B))
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bienvenido,\n$username",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontFamily = _BALATRO_FONT,
                    textAlign = TextAlign.Center,
                    lineHeight = 40.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        auth.signOut()
                        onLogoutSuccess()
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cerrar sesión", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = _BALATRO_FONT)
                }
            }

            // Puntuaciones
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF4A4528))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Tus puntuaciones", color = Color(0xFFFFD700), fontSize = 30.sp, fontFamily = _BALATRO_FONT)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ratedItems) { rating ->
                    RatingCard(rating)
                }
            }
        }
    }
}

@Composable
fun RatingCard(rating: UserRating) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF2C323F))
            .border(1.dp, Color(0xFF3F4552), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = rating.imagenUrl,
            contentDescription = null,
            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = rating.nombre, color = Color.White, fontSize = 24.sp, fontFamily = _BALATRO_FONT)
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < rating.puntuacion) Color(0xFFFFD700) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}