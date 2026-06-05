// Última modificación: 31/05/2026
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
import com.example.balatropedia.models.BlindViewModel
import com.example.balatropedia.ui.theme._BALATRO_FONT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Pantalla para editar una Blind existente en la base de datos
fun _Blind_Edit_Screen(
    blindId: String,
    viewModel: BlindViewModel,
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var vNombre by remember { mutableStateOf("") }
    var vModificador by remember { mutableStateOf("") }
    var vRecompensa by remember { mutableStateOf("") }
    var vImagenUrl by remember { mutableStateOf("") }
    var vIsSaving by remember { mutableStateOf(false) }

    val JokersRecomendados = remember { mutableStateListOf<ItemSelectorModel>() }

    var vShowJokerSheet by remember { mutableStateOf(false) }

    val listaJokersSelector by viewModel.jokersSelectorList.collectAsState()

    LaunchedEffect(blindId) {
        val blindActual = viewModel._Obtener_Blind_Por_ID(blindId)
        if (blindActual != null) {
            vNombre = blindActual.nombre
            vModificador = blindActual.modificador
            vRecompensa = blindActual.recompensa
            vImagenUrl = blindActual.imagen_url

            JokersRecomendados.clear()
            blindActual.jokersRecomendados.forEach { mapaJoker ->
                val id = mapaJoker["id"] ?: ""
                val nombre = mapaJoker["nombre"] ?: ""
                val imagenUrl = mapaJoker["imagenUrl"] ?: ""

                if (id.isNotEmpty()) {
                    JokersRecomendados.add(
                        ItemSelectorModel(id = id, nombre = nombre, imagenUrl = imagenUrl)
                    )
                }
            }
        } else {
            Toast.makeText(context, "Error: Blind no encontrada", Toast.LENGTH_SHORT).show()
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
                text = "Editar Blind",
                color = Color.White,
                fontSize = 32.sp,
                fontFamily = _BALATRO_FONT
            )

            Spacer(modifier = Modifier.height(24.dp))

            _Balatro_Input(
                label = "Nombre de la Blind",
                value = vNombre,
                onValueChange = { vNombre = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Input(
                label = "Modificador (Efecto)",
                value = vModificador,
                onValueChange = { vModificador = it },
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Input(
                label = "Recompensa (Ej: 5$)",
                value = vRecompensa,
                onValueChange = { vRecompensa = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Related_Items_Box(
                label = "Jokers Recomendados",
                instructions = "Selecciona Jokers útiles contra esta Blind.",
                buttonText = "Añadir Joker",
                selectedItems = JokersRecomendados,
                onAddClick = { vShowJokerSheet = true },
                onRemoveItem = { JokersRecomendados.remove(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Imagen de la Blind (URL)",
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
                text = "Actualizar Blind",
                isLoading = vIsSaving,
                onClick = {
                    if (vNombre.isBlank() || vModificador.isBlank() || vRecompensa.isBlank() || vImagenUrl.isBlank()) {
                        Toast.makeText(context, "Faltan datos obligatorios", Toast.LENGTH_SHORT).show()
                        return@_Balatro_Primary_Button
                    }

                    vIsSaving = true
                    viewModel._Actualizar_Blind(
                        id = blindId,
                        nombre = vNombre,
                        modificador = vModificador,
                        recompensa = vRecompensa,
                        imagenUrl = vImagenUrl,
                        jokersRecomendados = JokersRecomendados,
                        onSuccess = {
                            Toast.makeText(context, "¡Blind actualizada!", Toast.LENGTH_SHORT).show()
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
            titulo = "Añadir Joker Recomendado",
            itemsDisponibles = listaJokersSelector,
            onDismiss = { vShowJokerSheet = false },
            onItemSelected = { jokerSeleccionado ->
                if (!JokersRecomendados.any { it.id == jokerSeleccionado.id }) {
                    JokersRecomendados.add(jokerSeleccionado)
                }
                vShowJokerSheet = false
            }
        )
    }
}