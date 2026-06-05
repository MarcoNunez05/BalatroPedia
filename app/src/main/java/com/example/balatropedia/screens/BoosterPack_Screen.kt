// Última modificación: 01/06/2026
// Autor: Marco Núñez

package com.example.balatropedia.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
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
import com.example.balatropedia.models.BoosterPackViewModel
import com.example.balatropedia.ui.theme._BALATRO_FONT
import com.example.balatropedia.ui.theme.COLOR_BOOSTER_BACKGROUND

@Composable
// Pantalla de visualización de Booster Packs
fun _BoosterPack_Screen(
    isAdmin: Boolean,
    onProfileClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onAddBoosterPackClick: () -> Unit,
    onEditBoosterPackClick: (String) -> Unit,
    onBoosterPackClick: (String) -> Unit,
    viewModel: BoosterPackViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var vBusqueda by remember { mutableStateOf("") }
    val listaBoosterPacks = viewModel.boosterPacks.value
    val cargando = viewModel.isLoading.value

    var vBoosterPackAEliminar by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val listaBoosterPacksFiltrada by remember(vBusqueda, listaBoosterPacks) {
        derivedStateOf {
            listaBoosterPacks.filter { boosterPack ->
                boosterPack.nombre.contains(vBusqueda, ignoreCase = true)
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
                    onClick = onAddBoosterPackClick,
                    containerColor = COLOR_BOOSTER_BACKGROUND,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Booster Pack")
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
                CircularProgressIndicator(color = COLOR_BOOSTER_BACKGROUND)
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
                                text = "Booster Packs",
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
                            placeholderText = "Buscar Booster Pack...",
                            showFilterButton = false
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (listaBoosterPacksFiltrada.isEmpty()) {
                        item {
                            _Empty_State(
                                mensaje = "No se encontraron Booster Packs",
                                subtitulo = if (listaBoosterPacks.isEmpty())
                                    "Aún no hay Booster Packs registrados en la base de datos."
                                else
                                    "No hay Booster Packs que coincidan con tu búsqueda."
                            )
                        }
                    } else {
                        items(listaBoosterPacksFiltrada) { boosterPack ->
                            _Balatro_Row_Item(
                                nombre = boosterPack.nombre,
                                imageUrl = boosterPack.imagen_url,
                                backgroundColor = COLOR_BOOSTER_BACKGROUND,
                                isAdmin = isAdmin,
                                onEditClick = { onEditBoosterPackClick(boosterPack.id) },
                                onDeleteClick = { vBoosterPackAEliminar = boosterPack.id },
                                onItemClick = { onBoosterPackClick(boosterPack.id) }
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

    if (vBoosterPackAEliminar != null) {
        _Delete_Confirmation_Dialog(
            titulo = "Eliminar Booster Pack",
            mensaje = "¿Estás seguro de que deseas eliminar este Booster Pack de la base de datos?",
            onDismiss = { vBoosterPackAEliminar = null },
            onConfirm = {
                val idBorrar = vBoosterPackAEliminar
                vBoosterPackAEliminar = null

                if (idBorrar != null) {
                    viewModel._Eliminar_BoosterPack(
                        id = idBorrar,
                        onSuccess = {
                            Toast.makeText(context, "Booster Pack eliminado", Toast.LENGTH_SHORT).show()
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