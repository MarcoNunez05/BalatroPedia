package com.example.balatropedia.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.balatropedia.R
import com.example.balatropedia.components._Auth_Warning_Dialog
import com.example.balatropedia.components._Balatro_Row_Item
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.components._Rating_Dialog
import com.example.balatropedia.components._Rating_Stars
import com.example.balatropedia.components._Related_Section
import com.example.balatropedia.models.JokersViewModel
import com.example.balatropedia.ui.theme.COLOR_JOKER_BACKGROUND
import com.example.balatropedia.ui.theme._BALATRO_FONT
import com.google.firebase.auth.FirebaseAuth

data class JokerDetail(
    val nombre: String,
    val descripcion: String,
    val rareza: String,
    val imagenUrl: String,
    val puntuacion: Double,
    val sinergiasConsumibles: List<Map<String, String>>,
    val sinergiasJokers: List<Map<String, String>>
)

@Composable
fun _Joker_Detail_Screen(
    joker: JokerDetail,
    isAdmin: Boolean,
    viewModel: JokersViewModel,
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit,
    onRelatedJokerClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val auth = remember { FirebaseAuth.getInstance() }
    val currentUser = auth.currentUser

    var vShowRatingDialog by remember { mutableStateOf(false) }
    var vShowAuthWarningDialog by remember { mutableStateOf(false) }

    val currentPuntuacion by viewModel.puntuacionActual

    DisposableEffect(joker.nombre) {
        viewModel._Iniciar_Listener_Puntuacion(joker.nombre, joker.puntuacion)

        onDispose {
            viewModel._Detener_Listener_Puntuacion()
        }
    }

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
                if (joker.sinergiasConsumibles.isEmpty()) {
                    Text(
                        text = "Sinergias en consumibles:",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontFamily = _BALATRO_FONT
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Este Joker no tiene sinergias con consumibles",
                        color = Color.Gray,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                } else {

                    _Related_Section(
                        titulo = "Sinergias en consumibles:",
                        sinergias = joker.sinergiasConsumibles,
                        viewModel = viewModel,
                        itemBackgroundColor = Color(0xFF2B3A4A),
                        onItemClick = { idConsumible ->

                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFF2C323F), thickness = 2.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // SINERGIAS JOKERS
            Column(modifier = Modifier.fillMaxWidth()) {
                if (joker.sinergiasJokers.isEmpty()) {
                    Text(
                        text = "Sinergias en Jokers:",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontFamily = _BALATRO_FONT
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Este Joker no tiene sinergias con otros Jokers",
                        color = Color.Gray,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                } else {
                    _Related_Section(
                        titulo = "Sinergias en Jokers:",
                        sinergias = joker.sinergiasJokers,
                        viewModel = viewModel,
                        itemBackgroundColor = COLOR_JOKER_BACKGROUND,
                        onItemClick = { idJoker ->
                            onRelatedJokerClick(idJoker)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            // PUNTUACIÓN
            Text(
                text = "Puntuación $currentPuntuacion/5",
                color = Color.White,
                fontSize = 30.sp,
                fontFamily = _BALATRO_FONT,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            _Rating_Stars(puntuacion = currentPuntuacion)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (currentUser != null) {
                        vShowRatingDialog = true
                    } else {
                        vShowAuthWarningDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC5A53F)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Puntuar este Joker",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontFamily = _BALATRO_FONT
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (vShowRatingDialog && currentUser != null) {
            _Rating_Dialog(
                onDismiss = { vShowRatingDialog = false },
                onSubmitRating = { calificacion ->
                    viewModel._Guardar_Recalcular(
                        nombreJoker = joker.nombre,
                        userId = currentUser.uid,
                        calificacion = calificacion.toDouble(),
                        onSuccess = {
                            Toast.makeText(
                                context,
                                "¡Puntuación registrada con éxito!",
                                Toast.LENGTH_SHORT
                            ).show()
                            vShowRatingDialog = false
                        },
                        onError = { mensajeError ->
                            Toast.makeText(context, mensajeError, Toast.LENGTH_SHORT).show()
                            vShowRatingDialog = false
                        }
                    )
                }
            )
        }

        if (vShowAuthWarningDialog) {
            _Auth_Warning_Dialog(onDismiss = { vShowAuthWarningDialog = false })
        }
    }
}