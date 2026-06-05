// Última modificación: 30/05/2026
// Autor: Marco Núñez

package com.example.balatropedia.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.balatropedia.R
import com.example.balatropedia.class_models.ItemSelectorModel
import com.example.balatropedia.components.*
import com.example.balatropedia.models.ConsumibleViewModel
import com.example.balatropedia.ui.theme.COLOR_CONSUMIBLES_BACKGROUND
import com.example.balatropedia.ui.theme._BALATRO_FONT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Pantalla para editar un Consumible existente
fun _Consumible_Edit_Screen(
    consumibleId: String,
    viewModel: ConsumibleViewModel,
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var vNombre by remember { mutableStateOf("") }
    var vEfecto by remember { mutableStateOf("") }
    var vTipo by remember { mutableStateOf("Tarot") }
    var vImagenUrl by remember { mutableStateOf("") }
    var vIsSaving by remember { mutableStateOf(false) }

    val BoosterPacksIncluidos = remember { mutableStateListOf<ItemSelectorModel>() }
    var vShowBoosterSheet by remember { mutableStateOf(false) }

    val listaBoostersSelector by viewModel.boostersSelectorList.collectAsState()

    val tiposDisponibles = listOf("Tarot", "Planeta", "Espectral")

    LaunchedEffect(consumibleId) {
        val consumibleOriginal = viewModel._Obtener_Consumible_Por_ID(consumibleId)
        if (consumibleOriginal != null) {
            vNombre = consumibleOriginal.nombre
            vEfecto = consumibleOriginal.efecto

            if (tiposDisponibles.contains(consumibleOriginal.tipo)) {
                vTipo = consumibleOriginal.tipo
            }

            vImagenUrl = consumibleOriginal.imagen_url

            BoosterPacksIncluidos.clear()
            consumibleOriginal.boosterPack.forEach { packMap ->
                BoosterPacksIncluidos.add(
                    ItemSelectorModel(
                        id = packMap["id"] ?: "",
                        nombre = packMap["nombre"] ?: "",
                        imagenUrl = packMap["imagenUrl"] ?: ""
                    )
                )
            }
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
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Editar Consumible",
                color = Color.White,
                fontSize = 32.sp,
                fontFamily = _BALATRO_FONT
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tipo de Consumible",
                color = Color.White,
                fontSize = 24.sp,
                fontFamily = _BALATRO_FONT,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tiposDisponibles.forEach { tipo ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (vTipo == tipo) COLOR_CONSUMIBLES_BACKGROUND else Color(0xFF3F4552))
                            .clickable { vTipo = tipo }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tipo,
                            color = Color.White,
                            fontFamily = _BALATRO_FONT,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Input(
                label = "Nombre de la Carta",
                value = vNombre,
                onValueChange = { vNombre = it },
                readOnly = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Input(
                label = "Efecto de la Carta",
                value = vEfecto,
                onValueChange = { vEfecto = it },
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            _Related_Items_Box(
                label = "Sobres (Booster Packs)",
                instructions = "Selecciona en qué sobres puede aparecer esta carta.",
                buttonText = "Añadir Sobre",
                selectedItems = BoosterPacksIncluidos,
                onAddClick = { vShowBoosterSheet = true },
                onRemoveItem = { BoosterPacksIncluidos.remove(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Imagen del Consumible (URL)",
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
                    if (vNombre.isBlank() || vEfecto.isBlank() || vImagenUrl.isBlank()) {
                        Toast.makeText(context, "Faltan datos obligatorios", Toast.LENGTH_SHORT).show()
                        return@_Balatro_Primary_Button
                    }

                    vIsSaving = true

                    val listaSobresParaFirebase = BoosterPacksIncluidos.map { item ->
                        mapOf(
                            "id" to item.id,
                            "nombre" to item.nombre,
                            "imagenUrl" to item.imagenUrl
                        )
                    }

                    viewModel._Actualizar_Consumible(
                        id = consumibleId,
                        nombre = vNombre,
                        efecto = vEfecto,
                        tipo = vTipo,
                        imagenUrl = vImagenUrl,
                        boosterPack = listaSobresParaFirebase,
                        onSuccess = {
                            Toast.makeText(context, "¡Consumible actualizado con éxito!", Toast.LENGTH_SHORT).show()
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

    if (vShowBoosterSheet) {
        _Item_Selector(
            titulo = "Añadir Sobre",
            itemsDisponibles = listaBoostersSelector,
            onDismiss = { vShowBoosterSheet = false },
            onItemSelected = { boosterSeleccionado ->
                if (!BoosterPacksIncluidos.any { it.id == boosterSeleccionado.id }) {
                    BoosterPacksIncluidos.add(boosterSeleccionado)
                }
                vShowBoosterSheet = false
            }
        )
    }
}