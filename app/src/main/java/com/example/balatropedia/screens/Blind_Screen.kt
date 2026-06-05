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
import com.example.balatropedia.models.BlindViewModel
import com.example.balatropedia.ui.theme.COLOR_BLINDS_BACKGROUND
import com.example.balatropedia.ui.theme._BALATRO_FONT

@Composable
// Pantalla de visualización de Blinds
fun _Blind_Screen(
    isAdmin: Boolean,
    onProfileClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onAddBlindClick: () -> Unit,
    onEditBlindClick: (String) -> Unit,
    onBlindClick: (String) -> Unit,
    viewModel: BlindViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var vBusqueda by remember { mutableStateOf("") }
    val listaBlinds = viewModel.blinds.value
    val cargando = viewModel.isLoading.value

    var vBlindAEliminar by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val listaBlindsFiltrada by remember(vBusqueda, listaBlinds) {
        derivedStateOf {
            listaBlinds.filter { blind ->
                blind.nombre.contains(vBusqueda, ignoreCase = true)
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
                    onClick = onAddBlindClick,
                    containerColor = COLOR_BLINDS_BACKGROUND,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Blind")
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
                CircularProgressIndicator(color = COLOR_BLINDS_BACKGROUND)
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
                                text = "Blinds",
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
                            placeholderText = "Buscar Blind...",
                            showFilterButton = false
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (listaBlindsFiltrada.isEmpty()) {
                        item {
                            _Empty_State(
                                mensaje = "No se encontraron Blinds",
                                subtitulo = if (listaBlinds.isEmpty())
                                    "Aún no hay Blinds registradas en la base de datos."
                                else
                                    "No hay Blinds que coincidan con tu búsqueda o filtros."
                            )
                        }
                    } else {
                        items(listaBlindsFiltrada) { blind ->
                            _Balatro_Row_Item(
                                nombre = blind.nombre,
                                imageUrl = blind.imagen_url,
                                backgroundColor = COLOR_BLINDS_BACKGROUND,
                                isAdmin = isAdmin,
                                onEditClick = { onEditBlindClick(blind.id) },
                                onDeleteClick = { vBlindAEliminar = blind.id },
                                onItemClick = { onBlindClick(blind.id) }
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

    if (vBlindAEliminar != null) {
        _Delete_Confirmation_Dialog(
            titulo = "Eliminar Blind",
            mensaje = "¿Estás seguro de que deseas eliminar esta Blind de la base de datos?",
            onDismiss = { vBlindAEliminar = null },
            onConfirm = {
                val idBorrar = vBlindAEliminar
                vBlindAEliminar = null

                if (idBorrar != null) {
                    viewModel._Eliminar_Blind(
                        id = idBorrar,
                        onSuccess = {
                            Toast.makeText(context, "Blind eliminada", Toast.LENGTH_SHORT).show()
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

