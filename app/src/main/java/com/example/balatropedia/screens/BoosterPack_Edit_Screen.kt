package com.example.balatropedia.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.balatropedia.R
import com.example.balatropedia.components._Balatro_Input
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.components._Balatro_Primary_Button
import com.example.balatropedia.models.BoosterPackViewModel
import com.example.balatropedia.ui.theme._BALATRO_FONT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Pantalla para editar un Booster Pack existente en la base de datos
fun _BoosterPack_Edit_Screen(
    boosterPackId: String,
    viewModel: BoosterPackViewModel,
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var vNombre by remember { mutableStateOf("") }
    var vDescripcion by remember { mutableStateOf("") }
    var vImagenUrl by remember { mutableStateOf("") }
    var vCostoBase by remember { mutableStateOf("") }
    var vCartasDisponibles by remember { mutableStateOf("") }
    var vCartasElegibles by remember { mutableStateOf("") }
    var vIsSaving by remember { mutableStateOf(false) }
    var vIsLoadingData by remember { mutableStateOf(true) }

    LaunchedEffect(boosterPackId) {
        val boosterPackActual = viewModel._Obtener_BoosterPack_Por_ID(boosterPackId)
        if (boosterPackActual != null) {
            vNombre = boosterPackActual.nombre
            vDescripcion = boosterPackActual.descripcion
            vImagenUrl = boosterPackActual.imagen_url
            vCostoBase = boosterPackActual.costo_base.toString()
            vCartasDisponibles = boosterPackActual.cartas_disponibles.toString()
            vCartasElegibles = boosterPackActual.cartas_elegibles.toString()
        } else {
            Toast.makeText(context, "No se encontró el Booster Pack", Toast.LENGTH_SHORT).show()
            onNavigateBack()
        }
        vIsLoadingData = false
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
        if (vIsLoadingData) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1E222B))
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1E222B))
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Editar Booster Pack",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontFamily = _BALATRO_FONT
                )

                Spacer(modifier = Modifier.height(24.dp))

                _Balatro_Input(
                    label = "Nombre del Booster Pack",
                    value = vNombre,
                    onValueChange = { vNombre = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                _Balatro_Input(
                    label = "Descripción",
                    value = vDescripcion,
                    onValueChange = { vDescripcion = it },
                    singleLine = false,
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                _Balatro_Input(
                    label = "Costo Base ($)",
                    value = vCostoBase,
                    onValueChange = { vCostoBase = it.filter { char -> char.isDigit() } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        _Balatro_Input(
                            label = "C. Disponibles",
                            value = vCartasDisponibles,
                            onValueChange = { vCartasDisponibles = it.filter { char -> char.isDigit() } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        _Balatro_Input(
                            label = "C. Elegibles",
                            value = vCartasElegibles,
                            onValueChange = { vCartasElegibles = it.filter { char -> char.isDigit() } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Imagen del Booster Pack (URL)",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontFamily = _BALATRO_FONT,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = vImagenUrl,
                            contentDescription = null,
                            placeholder = painterResource(R.drawable.main_joker),
                            error = painterResource(R.drawable.main_joker),
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF3F4552)),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        TextField(
                            value = vImagenUrl,
                            onValueChange = { vImagenUrl = it },
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color(0xFF3F4552), RoundedCornerShape(6.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF3F4552),
                                unfocusedContainerColor = Color(0xFF3F4552),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(6.dp),
                            placeholder = { Text("https://...", color = Color.Gray) },
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                _Balatro_Primary_Button(
                    text = "Guardar Cambios",
                    isLoading = vIsSaving,
                    onClick = {
                        val costo = vCostoBase.toIntOrNull()
                        val cDisponibles = vCartasDisponibles.toIntOrNull()
                        val cElegibles = vCartasElegibles.toIntOrNull()

                        if (vNombre.isBlank() || vDescripcion.isBlank() || vImagenUrl.isBlank() ||
                            costo == null || cDisponibles == null || cElegibles == null) {
                            Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                            return@_Balatro_Primary_Button
                        }

                        if (cElegibles > cDisponibles) {
                            Toast.makeText(context, "Las cartas elegibles no pueden ser mayores que las disponibles", Toast.LENGTH_LONG).show()
                            return@_Balatro_Primary_Button
                        }

                        vIsSaving = true
                        viewModel._Actualizar_BoosterPack(
                            id = boosterPackId,
                            nombre = vNombre,
                            descripcion = vDescripcion,
                            imagenUrl = vImagenUrl,
                            costoBase = costo,
                            cartasDisponibles = cDisponibles,
                            cartasElegibles = cElegibles,
                            onSuccess = {
                                Toast.makeText(context, "¡Booster Pack actualizado!", Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            },
                            onError = { error ->
                                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                                vIsSaving = false
                            }
                        )
                    }
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

