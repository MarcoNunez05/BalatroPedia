// Última modificación: 29/05/2026
// Autor: Marco Núñez

package com.example.balatropedia.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.balatropedia.components._Balatro_Input
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.components._Related_Items_Box
import com.example.balatropedia.class_models.ItemSelectorModel
import com.example.balatropedia.components._Balatro_Primary_Button
import com.example.balatropedia.components._Item_Selector
import com.example.balatropedia.models.JokerViewModel
import com.example.balatropedia.ui.theme._BALATRO_FONT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Pantalla de edición de datos de un Joker
fun _Joker_Edit_Screen(
    jokerId: String,
    viewModel: JokerViewModel,
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var vNombre by remember { mutableStateOf("") }
    var vDescripcion by remember { mutableStateOf("") }
    val ListaRarezas = listOf("Común", "Poco común", "Rara", "Legendaria")
    var vRarezaSeleccionada by remember { mutableStateOf(ListaRarezas[0]) }
    var vRarezaExpanded by remember { mutableStateOf(false) }

    val JokersSinergia = remember { mutableStateListOf<ItemSelectorModel>() }
    val ConsumiblesSinergia = remember { mutableStateListOf<ItemSelectorModel>() }

    var vImagenUrl by remember { mutableStateOf("") }
    var vIsSaving by remember { mutableStateOf(false) }

    var vShowJokerSheet by remember { mutableStateOf(false) }
    var vShowConsumableSheet by remember { mutableStateOf(false) }

    val listaJokersSelector by viewModel.jokersSelectorList.collectAsState()
    val listaConsumiblesSelector by viewModel.consumiblesSelectorList.collectAsState()

    LaunchedEffect(jokerId) {
        val joker = viewModel._Obtener_Joker_Por_ID(jokerId)
        if (joker != null) {
            vNombre = joker.nombre
            vDescripcion = joker.descripcion
            if (joker.rareza in ListaRarezas) vRarezaSeleccionada = joker.rareza
            vImagenUrl = joker.imagen_url

            JokersSinergia.clear()
            joker.sinergiasJokers?.forEach { mapa ->
                val id = mapa["id"] ?: ""
                val nombre = mapa["nombre"] ?: ""
                val imagenUrl = mapa["imagenUrl"] ?: ""

                if (id.isNotEmpty()) {
                    JokersSinergia.add(ItemSelectorModel(id, nombre, imagenUrl))
                }
            }

            ConsumiblesSinergia.clear()
            joker.sinergiasConsumibles?.forEach { mapa ->
                val id = mapa["id"] ?: ""
                val nombre = mapa["nombre"] ?: ""
                val imagenUrl = mapa["imagenUrl"] ?: ""

                if (id.isNotEmpty()) {
                    ConsumiblesSinergia.add(ItemSelectorModel(id, nombre, imagenUrl))
                }
            }
        } else {
            Toast.makeText(context, "No se encontraron datos del Joker", Toast.LENGTH_SHORT).show()
            onNavigateBack()
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
                text = "Editar Joker",
                color = Color.White,
                fontSize = 32.sp,
                fontFamily = _BALATRO_FONT
            )

            Spacer(modifier = Modifier.height(24.dp))

            _Balatro_Input(
                label = "Nombre del Joker",
                value = vNombre,
                onValueChange = { vNombre = it },
                readOnly = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Input(
                label = "Descripción del Joker",
                value = vDescripcion,
                onValueChange = { vDescripcion = it },
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Rareza del Joker",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontFamily = _BALATRO_FONT,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = vRarezaExpanded,
                    onExpandedChange = { vRarezaExpanded = it }
                ) {
                    TextField(
                        value = vRarezaSeleccionada,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = vRarezaExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .border(1.dp, Color(0xFF3F4552), RoundedCornerShape(6.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF3F4552),
                            unfocusedContainerColor = Color(0xFF3F4552),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = vRarezaExpanded,
                        onDismissRequest = { vRarezaExpanded = false }
                    ) {
                        ListaRarezas.forEach { rareza ->
                            DropdownMenuItem(
                                text = { Text(rareza) },
                                onClick = {
                                    vRarezaSeleccionada = rareza
                                    vRarezaExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            _Related_Items_Box(
                label = "Jokers que generan sinergia",
                instructions = "Selecciona Jokers de la base de datos.",
                buttonText = "Añadir Joker",
                selectedItems = JokersSinergia,
                onAddClick = { vShowJokerSheet = true },
                onRemoveItem = { JokersSinergia.remove(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Related_Items_Box(
                label = "Consumibles que generan sinergia",
                instructions = "Selecciona consumibles de la base de datos.",
                buttonText = "Añadir Consumible",
                selectedItems = ConsumiblesSinergia,
                onAddClick = { vShowConsumableSheet = true },
                onRemoveItem = { ConsumiblesSinergia.remove(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Imagen del Joker (URL)",
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
                    if (vNombre.isBlank() || vDescripcion.isBlank() || vImagenUrl.isBlank()) {
                        Toast.makeText(context, "Faltan datos obligatorios", Toast.LENGTH_SHORT).show()
                        return@_Balatro_Primary_Button
                    }
                    vIsSaving = true

                    viewModel._Actualizar_Joker(
                        id = jokerId,
                        nombre = vNombre,
                        descripcion = vDescripcion,
                        rareza = vRarezaSeleccionada,
                        jokersSinergia = JokersSinergia,
                        consumiblesSinergia = ConsumiblesSinergia,
                        imagenUrl = vImagenUrl,
                        onSuccess = {
                            Toast.makeText(context, "¡Joker actualizado con éxito!", Toast.LENGTH_SHORT).show()
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

    if (vShowJokerSheet) {
        _Item_Selector(
            titulo = "Modificar Sinergias",
            itemsDisponibles = listaJokersSelector,
            onDismiss = { vShowJokerSheet = false },
            onItemSelected = { jokerSeleccionado ->
                if (!JokersSinergia.any { it.id == jokerSeleccionado.id }) {
                    JokersSinergia.add(jokerSeleccionado)
                }
                vShowJokerSheet = false
            }
        )
    }

    if (vShowConsumableSheet) {
        _Item_Selector(
            titulo = "Modificar Sinergias",
            itemsDisponibles = listaConsumiblesSelector,
            onDismiss = { vShowConsumableSheet = false },
            onItemSelected = { consumibleSeleccionado ->
                if (!ConsumiblesSinergia.any { it.id == consumibleSeleccionado.id }) {
                    ConsumiblesSinergia.add(consumibleSeleccionado)
                }
                vShowConsumableSheet = false
            }
        )
    }
}