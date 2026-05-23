package com.example.balatropedia.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.balatropedia.R
import com.example.balatropedia.components._Balatro_Row_Item
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.components._Rating_Stars
import com.example.balatropedia.ui.theme.COLOR_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_JOKER_BACKGROUND
import com.example.balatropedia.ui.theme._BALATRO_FONT

data class JokerDetail(
    val nombre: String,
    val descripcion: String,
    val rareza: String,
    val imagenUrl: String,
    val puntuacion: Double,
    val sinergiasConsumibles: List<String>,
    val sinergiasJokers: List<Map<String, String>>
)

@Composable
fun JokerDetailScreen(
    joker: JokerDetail,
    isAdmin: Boolean,
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        topBar = {
            _Balatropedia_Header(
                comeBack = true,
                onBackClick = onNavigateBack,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E222B))
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // TARJETA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(COLOR_JOKER_BACKGROUND)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = joker.imagenUrl,
                    contentDescription = joker.nombre,
                    placeholder = painterResource(R.drawable.main_joker),
                    error = painterResource(R.drawable.main_joker),
                    modifier = Modifier
                        .size(width = 85.dp, height = 115.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = joker.nombre,
                    color = Color.White,
                    fontSize = 40.sp,
                    fontFamily = _BALATRO_FONT,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DESCRIPCIÓN
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Descripción:",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontFamily = _BALATRO_FONT,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = joker.descripcion,
                    color = Color.White,
                    fontSize = 20.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFF2C323F), thickness = 2.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // RAREZA
            Text(
                text = "Rareza: ${joker.rareza}",
                color = Color.White,
                fontSize = 30.sp,
                fontFamily = _BALATRO_FONT,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFF2C323F), thickness = 2.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // SINERGIAS CONSUMIBLES
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Sinergias en consumibles:",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontFamily = _BALATRO_FONT
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (joker.sinergiasConsumibles.isEmpty()) {
                    Text(
                        text = "Este Joker no tiene sinergias con consumibles",
                        color = Color.Gray,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                } else {

                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFF2C323F), thickness = 2.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // SINERGIAS JOKERS
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Sinergias en Jokers:",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontFamily = _BALATRO_FONT
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (joker.sinergiasJokers.isEmpty()) {
                    Text(
                        text = "Este Joker no tiene sinergias con otros Jokers",
                        color = Color.Gray,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                } else {
                    joker.sinergiasJokers.forEach { sinergia ->
                        _Balatro_Row_Item(
                            nombre = sinergia["nombre"] ?: "",
                            imageUrl = sinergia["imageUrl"] ?: "",
                            backgroundColor = COLOR_JOKER_BACKGROUND,
                            isAdmin = false,
                            onEditClick = {},
                            onDeleteClick = {},
                            onItemClick = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // PUNTUACIÓN
            Text(
                text = "Puntuación de los usuarios: ${joker.puntuacion}/5",
                color = Color.White,
                fontSize = 30.sp,
                fontFamily = _BALATRO_FONT,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            _Rating_Stars(puntuacion = joker.puntuacion)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}