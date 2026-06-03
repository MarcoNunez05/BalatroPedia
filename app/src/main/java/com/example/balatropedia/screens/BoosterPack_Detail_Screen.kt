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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.balatropedia.R
import com.example.balatropedia.class_models.BoosterPackModel
import com.example.balatropedia.components.*
import com.example.balatropedia.models.BoosterPackViewModel
import com.example.balatropedia.ui.theme.COLOR_BOOSTER_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_STAR
import com.example.balatropedia.ui.theme._BALATRO_FONT
import com.google.firebase.auth.FirebaseAuth

@Composable
fun _BoosterPack_Detail_Screen(
    boosterPack: BoosterPackModel,
    viewModel: BoosterPackViewModel,
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val auth = remember { FirebaseAuth.getInstance() }
    val currentUser = auth.currentUser

    var vShowRatingDialog by remember { mutableStateOf(false) }
    var vShowAuthWarningDialog by remember { mutableStateOf(false) }

    val currentPuntuacion by viewModel.puntuacionActual

    DisposableEffect(boosterPack.id) {
        viewModel._Iniciar_Listener_Puntuacion(boosterPack.id, boosterPack.puntuacion_usuarios)
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
                    .background(COLOR_BOOSTER_BACKGROUND)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = boosterPack.imagen_url,
                    contentDescription = boosterPack.nombre,
                    placeholder = painterResource(R.drawable.main_joker),
                    error = painterResource(R.drawable.main_joker),
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = boosterPack.nombre,
                    color = Color.White,
                    fontSize = 36.sp,
                    fontFamily = _BALATRO_FONT,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // DATOS DEL PACK
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Costo", color = Color.White.copy(0.7f), fontFamily = _BALATRO_FONT)
                        Text("$${boosterPack.costo_base}", color = Color.Green, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Selección", color = Color.White.copy(0.7f), fontFamily = _BALATRO_FONT)
                        Text("${boosterPack.cartas_elegibles}/${boosterPack.cartas_disponibles}", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // DESCRIPCIÓN
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Descripción:",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontFamily = _BALATRO_FONT
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = boosterPack.descripcion,
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // PUNTUACIÓN
            Text(
                text = "Puntuación $currentPuntuacion/5",
                color = Color.White,
                fontSize = 26.sp,
                fontFamily = _BALATRO_FONT,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            _Rating_Stars(puntuacion = currentPuntuacion)

            Spacer(modifier = Modifier.height(24.dp))

            _Balatro_Primary_Button(
                text = "Puntuar este Pack",
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
                        boosterPackId = boosterPack.id,
                        nombreBoosterPack = boosterPack.nombre,
                        userId = currentUser.uid,
                        calificacion = calificacion.toDouble(),
                        onSuccess = {
                            Toast.makeText(context, "¡Puntuación registrada!", Toast.LENGTH_SHORT).show()
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