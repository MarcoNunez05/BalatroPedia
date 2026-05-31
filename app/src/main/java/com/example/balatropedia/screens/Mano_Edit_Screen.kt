package com.example.balatropedia.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.components._Balatro_Input
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.components._Related_Items_Box
import com.example.balatropedia.class_models.ItemSelectorModel
import com.example.balatropedia.components._Balatro_Primary_Button
import com.example.balatropedia.components._Item_Selector
import com.example.balatropedia.models.ManoViewModel
import com.example.balatropedia.ui.theme._BALATRO_FONT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Pantalla para editar una Mano existente
fun _Mano_Edit_Screen(
    manoId: String,
    viewModel: ManoViewModel,
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var vNombre by remember { mutableStateOf("") }
    var vDescripcion by remember { mutableStateOf("") }
    var vPuntuacionBase by remember { mutableStateOf("") }
    var vMultiplicadorBase by remember { mutableStateOf("") }
    var vIsSaving by remember { mutableStateOf(false) }

    val JokersAfectados = remember { mutableStateListOf<ItemSelectorModel>() }
    val CartaPlaneta = remember { mutableStateListOf<ItemSelectorModel>() }

    var vShowJokersSheet by remember { mutableStateOf(false) }
    var vShowPlanetaSheet by remember { mutableStateOf(false) }

    val listaJokersSelector by viewModel.jokersSelectorList.collectAsState()
    val listaCartasPlanetaSelector by viewModel.cartasPlanetaSelectorList.collectAsState()

    LaunchedEffect(manoId) {
        val manoOriginal = viewModel._Obtener_Mano_Por_ID(manoId)
        if (manoOriginal != null) {
            vNombre = manoOriginal.nombre
            vDescripcion = manoOriginal.descripcion
            vPuntuacionBase = manoOriginal.puntuacion_base.toString()
            vMultiplicadorBase = manoOriginal.multiplicador_base.toString()

            JokersAfectados.clear()
            manoOriginal.jokersAfectados.forEach { map ->
                JokersAfectados.add(
                    ItemSelectorModel(
                        id = map["id"] ?: "",
                        nombre = map["nombre"] ?: "",
                        imagenUrl = map["imagenUrl"] ?: ""
                    )
                )
            }

            CartaPlaneta.clear()
            manoOriginal.cartaPlaneta.forEach { map ->
                CartaPlaneta.add(
                    ItemSelectorModel(
                        id = map["id"] ?: "",
                        nombre = map["nombre"] ?: "",
                        imagenUrl = map["imagenUrl"] ?: ""
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
                text = "Editar Mano",
                color = Color.White,
                fontSize = 32.sp,
                fontFamily = _BALATRO_FONT
            )

            Spacer(modifier = Modifier.height(24.dp))

            _Balatro_Input(
                label = "Nombre de la Mano",
                value = vNombre,
                onValueChange = { vNombre = it },
                readOnly = true
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    _Balatro_Input(
                        label = "Fichas (Azul)",
                        value = vPuntuacionBase,
                        onValueChange = { vPuntuacionBase = it }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    _Balatro_Input(
                        label = "Mult. (Rojo)",
                        value = vMultiplicadorBase,
                        onValueChange = { vMultiplicadorBase = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            _Related_Items_Box(
                label = "Jokers que potencian esta mano",
                instructions = "Selecciona Jokers que interactúen con esta mano específica.",
                buttonText = "Añadir Joker",
                selectedItems = JokersAfectados,
                onAddClick = { vShowJokersSheet = true },
                onRemoveItem = { JokersAfectados.remove(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Related_Items_Box(
                label = "Carta Planeta",
                instructions = "Planeta que sube el nivel de esta mano.",
                buttonText = "Cambiar Planeta",
                selectedItems = CartaPlaneta,
                onAddClick = { vShowPlanetaSheet = true },
                onRemoveItem = { CartaPlaneta.remove(it) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            _Balatro_Primary_Button(
                text = "Guardar Cambios",
                isLoading = vIsSaving,
                onClick = {
                    val fichas = vPuntuacionBase.toIntOrNull()
                    val multiplicador = vMultiplicadorBase.toIntOrNull()

                    if (vNombre.isBlank() || vDescripcion.isBlank() || vPuntuacionBase.isBlank() || vMultiplicadorBase.isBlank()) {
                        Toast.makeText(context, "Faltan datos obligatorios", Toast.LENGTH_SHORT).show()
                        return@_Balatro_Primary_Button
                    }

                    if (fichas == null || multiplicador == null) {
                        Toast.makeText(context, "Fichas y Multiplicador deben ser números", Toast.LENGTH_SHORT).show()
                        return@_Balatro_Primary_Button
                    }

                    vIsSaving = true
                    viewModel._Actualizar_Mano(
                        id = manoId,
                        nombre = vNombre,
                        descripcion = vDescripcion,
                        puntuacionBase = fichas,
                        multiplicadorBase = multiplicador,
                        jokersAfectados = JokersAfectados,
                        cartaPlaneta = CartaPlaneta,
                        onSuccess = {
                            Toast.makeText(context, "¡Mano actualizada con éxito!", Toast.LENGTH_SHORT).show()
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

    if (vShowJokersSheet) {
        _Item_Selector(
            titulo = "Añadir Joker Afectado",
            itemsDisponibles = listaJokersSelector,
            onDismiss = { vShowJokersSheet = false },
            onItemSelected = { jokerSeleccionado ->
                if (!JokersAfectados.any { it.id == jokerSeleccionado.id }) {
                    JokersAfectados.add(jokerSeleccionado)
                }
                vShowJokersSheet = false
            }
        )
    }

    if (vShowPlanetaSheet) {
        _Item_Selector(
            titulo = "Cambiar Carta Planeta",
            itemsDisponibles = listaCartasPlanetaSelector,
            onDismiss = { vShowPlanetaSheet = false },
            onItemSelected = { planetaSeleccionado ->
                if (!CartaPlaneta.any { it.id == planetaSeleccionado.id }) {
                    CartaPlaneta.add(planetaSeleccionado)
                }
                vShowPlanetaSheet = false
            }
        )
    }
}
