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
import com.example.balatropedia.class_models.MazoModel
import com.example.balatropedia.components.*
import com.example.balatropedia.models.MazoViewModel
import com.example.balatropedia.ui.theme.COLOR_CONSUMIBLES_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_MAZOS_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_STAR
import com.example.balatropedia.ui.theme.COLOR_VOUCHERS_BACKGROUND
import com.example.balatropedia.ui.theme._BALATRO_FONT
import com.google.firebase.auth.FirebaseAuth

@Composable
// Pantalla de visualización de detalles de un Mazo
fun _Mazo_Detail_Screen(
    mazo: MazoModel,
    viewModel: MazoViewModel,
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit,
    onNavigateToVoucher: (String) -> Unit,
    onNavigateToConsumible: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val auth = remember { FirebaseAuth.getInstance() }
    val currentUser = auth.currentUser

    var vShowRatingDialog by remember { mutableStateOf(false) }
    var vShowAuthWarningDialog by remember { mutableStateOf(false) }

    val currentPuntuacion by viewModel.puntuacionActual
    val consumiblesValidados by viewModel.consumiblesValidados
    val vouchersValidados by viewModel.vouchersValidados
    val cargandoRelaciones by viewModel.cargandoRelaciones

    DisposableEffect(mazo.id) {
        viewModel._Iniciar_Listener_Puntuacion(mazo.id, mazo.puntuacion_usuarios)
        viewModel._Validar_Relaciones(mazo)

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
                    .background(COLOR_MAZOS_BACKGROUND)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = mazo.imagen_url,
                    contentDescription = mazo.nombre,
                    placeholder = painterResource(R.drawable.main_joker),
                    error = painterResource(R.drawable.main_joker),
                    modifier = Modifier
                        .size(width = 85.dp, height = 115.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = mazo.nombre,
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
                    text = mazo.descripcion,
                    color = Color.White,
                    fontSize = 20.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFF2C323F), thickness = 2.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // VOUCHERS INCLUIDOS
            Column(modifier = Modifier.fillMaxWidth()) {
                if (cargandoRelaciones) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
                    )
                } else {
                    if (vouchersValidados.isEmpty()) {
                        Text(
                            text = "Vouchers incluidos:",
                            color = Color.White,
                            fontSize = 30.sp,
                            fontFamily = _BALATRO_FONT
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Este Mazo no incluye Vouchers iniciales",
                            color = Color.Gray,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    } else {
                        _Related_Section(
                            titulo = "Vouchers incluidos:",
                            sinergias = vouchersValidados,
                            itemBackgroundColor = COLOR_VOUCHERS_BACKGROUND,
                            onItemClick = { idVoucher ->
                                onNavigateToVoucher(idVoucher)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFF2C323F), thickness = 2.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // CONSUMIBLES INCLUIDOS
            Column(modifier = Modifier.fillMaxWidth()) {
                if (cargandoRelaciones) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
                    )
                } else {
                    if (consumiblesValidados.isEmpty()) {
                        Text(
                            text = "Consumibles incluidos:",
                            color = Color.White,
                            fontSize = 30.sp,
                            fontFamily = _BALATRO_FONT
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Este Mazo no incluye Consumibles iniciales",
                            color = Color.Gray,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    } else {
                        _Related_Section(
                            titulo = "Consumibles incluidos:",
                            sinergias = consumiblesValidados,
                            itemBackgroundColor = COLOR_CONSUMIBLES_BACKGROUND,
                            onItemClick = { idConsumible ->
                                onNavigateToConsumible(idConsumible)
                            }
                        )
                    }
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
                text = "Puntuar este Mazo",
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
                        mazoId = mazo.id,
                        nombreMazo = mazo.nombre,
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