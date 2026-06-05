package com.example.balatropedia.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.components._Balatro_Row_Item
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.components._Search_Filter_Bar
import com.example.balatropedia.components._Delete_Confirmation_Dialog
import com.example.balatropedia.components._Empty_State
import com.example.balatropedia.models.ConsumibleViewModel
import com.example.balatropedia.ui.theme.COLOR_CONSUMIBLES_BACKGROUND
import com.example.balatropedia.ui.theme._BALATRO_FONT

@Composable
// Pantalla de visualización de Consumibles
fun _Consumible_Screen(
    isAdmin: Boolean,
    onProfileClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onAddConsumibleClick: () -> Unit,
    onEditConsumibleClick: (String) -> Unit,
    onConsumibleClick: (String) -> Unit,
    viewModel: ConsumibleViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var vBusqueda by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabs = listOf("Tarot", "Planetas", "Espectrales")
    val tiposDB = listOf("Tarot", "Planeta", "Espectral")

    val listaConsumibles = viewModel.consumibles.value
    val cargando = viewModel.isLoading.value

    var vConsumibleAEliminar by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val listaConsumiblesFiltrada by remember(vBusqueda, selectedTabIndex, listaConsumibles) {
        derivedStateOf {
            val tipoActual = tiposDB[selectedTabIndex]
            listaConsumibles.filter { consumible ->
                consumible.tipo.equals(tipoActual, ignoreCase = true) &&
                        consumible.nombre.contains(vBusqueda, ignoreCase = true)
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
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = onAddConsumibleClick,
                    containerColor = COLOR_CONSUMIBLES_BACKGROUND,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Consumible")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E222B))
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (cargando) {
                CircularProgressIndicator(color = COLOR_CONSUMIBLES_BACKGROUND)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Consumibles",
                                color = Color.White,
                                fontSize = 30.sp,
                                fontFamily = _BALATRO_FONT
                            )
                        }
                    }

                    item {
                        _Search_Filter_Bar(
                            searchQuery = vBusqueda,
                            onSearchQueryChange = { vBusqueda = it },
                            placeholderText = "Buscar en ${tabs[selectedTabIndex]}...",
                            showFilterButton = false
                        )
                    }

                    // TABROW
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                    color = COLOR_CONSUMIBLES_BACKGROUND
                                )
                            },
                            divider = {}
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = {
                                        selectedTabIndex = index
                                        vBusqueda = ""
                                    },
                                    text = {
                                        Text(
                                            text = title,
                                            color = if (selectedTabIndex == index) COLOR_CONSUMIBLES_BACKGROUND else Color.Gray,
                                            fontFamily = _BALATRO_FONT,
                                            fontSize = 14.sp
                                        )
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (listaConsumiblesFiltrada.isEmpty()) {
                        item {
                            _Empty_State(
                                mensaje = "No se encontraron ${tabs[selectedTabIndex]}",
                                subtitulo = if (listaConsumibles.none { it.tipo.equals(tiposDB[selectedTabIndex], ignoreCase = true) })
                                    "Aún no hay cartas tipo ${tabs[selectedTabIndex]} en la base de datos."
                                else
                                    "No hay coincidencias con tu búsqueda actual."
                            )
                        }
                    } else {
                        items(listaConsumiblesFiltrada) { consumible ->
                            _Balatro_Row_Item(
                                nombre = consumible.nombre,
                                imageUrl = consumible.imagen_url,
                                backgroundColor = COLOR_CONSUMIBLES_BACKGROUND,
                                isAdmin = isAdmin,
                                onEditClick = { onEditConsumibleClick(consumible.id) },
                                onDeleteClick = { vConsumibleAEliminar = consumible.id },
                                onItemClick = { onConsumibleClick(consumible.id) }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (vConsumibleAEliminar != null) {
        _Delete_Confirmation_Dialog(
            titulo = "Eliminar Consumible",
            mensaje = "¿Estás seguro de que deseas eliminar este consumible de la base de datos?",
            onDismiss = { vConsumibleAEliminar = null },
            onConfirm = {
                val idBorrar = vConsumibleAEliminar
                vConsumibleAEliminar = null

                if (idBorrar != null) {
                    viewModel._Eliminar_Consumible(
                        id = idBorrar,
                        onSuccess = {
                            Toast.makeText(context, "Consumible eliminado", Toast.LENGTH_SHORT).show()
                        },
                        onError = { error ->
                            Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        )
    }
}

