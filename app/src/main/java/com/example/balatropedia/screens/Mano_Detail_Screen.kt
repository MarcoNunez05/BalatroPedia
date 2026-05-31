package com.example.balatropedia.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.class_models.ManoModel
import com.example.balatropedia.components.*
import com.example.balatropedia.models.ManoViewModel
import com.example.balatropedia.ui.theme.COLOR_CONSUMIBLES_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_JOKER_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_MANOS_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_STAR
import com.example.balatropedia.ui.theme._BALATRO_FONT
import com.google.firebase.auth.FirebaseAuth

@Composable
// Pantalla de visualización de detalles de una Mano
fun _Mano_Detail_Screen(
    mano: ManoModel,
    viewModel: ManoViewModel,
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit,
    onNavigateToJoker: (String) -> Unit,
    onNavigateToConsumible: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val auth = remember { FirebaseAuth.getInstance() }
    val currentUser = auth.currentUser

    var vShowRatingDialog by remember { mutableStateOf(false) }
    var vShowAuthWarningDialog by remember { mutableStateOf(false) }

    val currentPuntuacion by viewModel.puntuacionActual

    DisposableEffect(mano.id) {
        viewModel._Iniciar_Listener_Puntuacion(mano.id, mano.puntuacion_usuarios)
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

            // TARJETA PRINCIPAL
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(COLOR_MANOS_BACKGROUND)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = mano.nombre,
                    color = Color.White,
                    fontSize = 40.sp,
                    fontFamily = _BALATRO_FONT,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // FICHAS Y MULTIPLICADOR
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0077FF))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${mano.puntuacion_base}",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontFamily = _BALATRO_FONT
                        )
                    }

                    Text(
                        text = "X",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontFamily = _BALATRO_FONT
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFF3333))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${mano.multiplicador_base}",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontFamily = _BALATRO_FONT
                        )
                    }
                }
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
                    text = mano.descripcion,
                    color = Color.White,
                    fontSize = 20.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFF2C323F), thickness = 2.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // JOKERS AFECTADOS
            Column(modifier = Modifier.fillMaxWidth()) {
                if (mano.jokersAfectados.isEmpty()) {
                    Text(
                        text = "Jokers que potencian esta mano:",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontFamily = _BALATRO_FONT
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Aún no hay Jokers vinculados a esta mano.",
                        color = Color.Gray,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                } else {
                    _Related_Section(
                        titulo = "Jokers que potencian esta mano:",
                        sinergias = mano.jokersAfectados,
                        itemBackgroundColor = COLOR_JOKER_BACKGROUND,
                        onItemClick = { idJoker ->
                            onNavigateToJoker(idJoker)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFF2C323F), thickness = 2.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // CARTA PLANETA
            Column(modifier = Modifier.fillMaxWidth()) {
                if (mano.cartaPlaneta.isEmpty()) {
                    Text(
                        text = "Carta Planeta:",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontFamily = _BALATRO_FONT
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Planeta desconocido.",
                        color = Color.Gray,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                } else {
                    _Related_Section(
                        titulo = "Carta Planeta:",
                        sinergias = mano.cartaPlaneta,
                        itemBackgroundColor = COLOR_CONSUMIBLES_BACKGROUND,
                        onItemClick = { idPlaneta ->
                            onNavigateToConsumible(idPlaneta)
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

            _Balatro_Primary_Button(
                text = "Puntuar esta Mano",
                isLoading = false,
                color = COLOR_STAR,
                onClick = {
                    if (currentUser != null) {
                        vShowRatingDialog = true
                    } else {
                        vShowAuthWarningDialog = true
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (vShowRatingDialog && currentUser != null) {
            _Rating_Dialog(
                onDismiss = { vShowRatingDialog = false },
                onSubmitRating = { calificacion ->
                    viewModel._Guardar_Recalcular(
                        manoId = mano.id,
                        nombreMano = mano.nombre,
                        userId = currentUser.uid,
                        calificacion = calificacion.toDouble(),
                        onSuccess = {
                            Toast.makeText(context, "¡Puntuación registrada con éxito!", Toast.LENGTH_SHORT).show()
                            vShowRatingDialog = false
                        },
                        onError = {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            vShowRatingDialog = false
                        }
                    )
                }
            )
        }

        if (vShowAuthWarningDialog) {
            _Auth_Warning_Dialog { vShowAuthWarningDialog = false }
        }
    }
}