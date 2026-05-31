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
import com.example.balatropedia.class_models.ChallengeModel
import com.example.balatropedia.components.*
import com.example.balatropedia.models.ChallengeViewModel
import com.example.balatropedia.ui.theme.COLOR_CHALLENGES_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_CONSUMIBLES_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_JOKER_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_STAR
import com.example.balatropedia.ui.theme.COLOR_VOUCHERS_BACKGROUND
import com.example.balatropedia.ui.theme._BALATRO_FONT
import com.google.firebase.auth.FirebaseAuth

@Composable
// Pantalla de visualización de detalles de un Challenge
fun _Challenge_Detail_Screen(
    challenge: ChallengeModel,
    viewModel: ChallengeViewModel,
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit,
    onNavigateToJoker: (String) -> Unit,
    onNavigateToConsumible: (String) -> Unit,
    onNavigateToVoucher: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val auth = remember { FirebaseAuth.getInstance() }
    val currentUser = auth.currentUser

    var vShowRatingDialog by remember { mutableStateOf(false) }
    var vShowAuthWarningDialog by remember { mutableStateOf(false) }

    val currentPuntuacion by viewModel.puntuacionActual

    DisposableEffect(challenge.id) {
        viewModel._Iniciar_Listener_Puntuacion(challenge.id, challenge.puntuacion_usuarios)
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(COLOR_CHALLENGES_BACKGROUND)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = challenge.nombre,
                    color = Color.White,
                    fontSize = 40.sp,
                    fontFamily = _BALATRO_FONT,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                    text = challenge.descripcion,
                    color = Color.White,
                    fontSize = 20.sp,
                    lineHeight = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // REQUISITOS
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Requisitos:",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontFamily = _BALATRO_FONT,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = challenge.requisitos,
                    color = Color.White,
                    fontSize = 20.sp,
                    lineHeight = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFF2E8B57), thickness = 2.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Elementos Incluidos",
                color = Color(0xFF2E8B57),
                fontSize = 32.sp,
                fontFamily = _BALATRO_FONT,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            // JOKERS INCLUIDOS
            Column(modifier = Modifier.fillMaxWidth()) {
                if (challenge.jokersIncluidos.isNotEmpty()) {
                    _Related_Section(
                        titulo = "Jokers:",
                        sinergias = challenge.jokersIncluidos,
                        itemBackgroundColor = COLOR_JOKER_BACKGROUND,
                        onItemClick = { id -> onNavigateToJoker(id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // CONSUMIBLES INCLUIDOS
            Column(modifier = Modifier.fillMaxWidth()) {
                if (challenge.consumiblesIncluidos.isNotEmpty()) {
                    _Related_Section(
                        titulo = "Consumibles:",
                        sinergias = challenge.consumiblesIncluidos,
                        itemBackgroundColor = COLOR_CONSUMIBLES_BACKGROUND,
                        onItemClick = { id -> onNavigateToConsumible(id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // VOUCHERS INCLUIDOS
            Column(modifier = Modifier.fillMaxWidth()) {
                if (challenge.vouchersIncluidos.isNotEmpty()) {
                    _Related_Section(
                        titulo = "Vouchers:",
                        sinergias = challenge.vouchersIncluidos,
                        itemBackgroundColor = COLOR_VOUCHERS_BACKGROUND,
                        onItemClick = { id -> onNavigateToVoucher(id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (challenge.jokersIncluidos.isEmpty() && challenge.consumiblesIncluidos.isEmpty() && challenge.vouchersIncluidos.isEmpty()) {
                Text(
                    text = "Este Challenge no incluye elementos iniciales adicionales.",
                    color = Color.Gray,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFC33C3C), thickness = 2.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Elementos Prohibidos",
                color = Color(0xFFC33C3C),
                fontSize = 32.sp,
                fontFamily = _BALATRO_FONT,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            // JOKERS PROHIBIDOS
            Column(modifier = Modifier.fillMaxWidth()) {
                if (challenge.jokersProhibidos.isNotEmpty()) {
                    _Related_Section(
                        titulo = "Jokers baneados:",
                        sinergias = challenge.jokersProhibidos,
                        itemBackgroundColor = COLOR_JOKER_BACKGROUND,
                        onItemClick = { id -> onNavigateToJoker(id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // CONSUMIBLES PROHIBIDOS
            Column(modifier = Modifier.fillMaxWidth()) {
                if (challenge.consumiblesProhibidos.isNotEmpty()) {
                    _Related_Section(
                        titulo = "Consumibles baneados:",
                        sinergias = challenge.consumiblesProhibidos,
                        itemBackgroundColor = COLOR_CONSUMIBLES_BACKGROUND,
                        onItemClick = { id -> onNavigateToConsumible(id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // VOUCHERS PROHIBIDOS
            Column(modifier = Modifier.fillMaxWidth()) {
                if (challenge.vouchersProhibidos.isNotEmpty()) {
                    _Related_Section(
                        titulo = "Vouchers baneados:",
                        sinergias = challenge.vouchersProhibidos,
                        itemBackgroundColor = COLOR_VOUCHERS_BACKGROUND,
                        onItemClick = { id -> onNavigateToVoucher(id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (challenge.jokersProhibidos.isEmpty() && challenge.consumiblesProhibidos.isEmpty() && challenge.vouchersProhibidos.isEmpty()) {
                Text(
                    text = "No hay elementos prohibidos en este Challenge.",
                    color = Color.Gray,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )
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
                text = "Puntuar este Challenge",
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
                        challengeId = challenge.id,
                        nombreChallenge = challenge.nombre,
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

