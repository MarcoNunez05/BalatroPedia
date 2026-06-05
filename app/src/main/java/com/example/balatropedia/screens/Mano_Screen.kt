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
import com.example.balatropedia.models.ManoViewModel
import com.example.balatropedia.ui.theme.COLOR_MANOS_BACKGROUND
import com.example.balatropedia.ui.theme._BALATRO_FONT

@Composable
// Pantalla de visualización de Manos
fun _Mano_Screen(
    isAdmin: Boolean,
    onProfileClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onAddManoClick: () -> Unit,
    onEditManoClick: (String) -> Unit,
    onManoClick: (String) -> Unit,
    viewModel: ManoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var vBusqueda by remember { mutableStateOf("") }
    val listaManos = viewModel.manos.value
    val cargando = viewModel.isLoading.value

    var vManoAEliminar by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val listaManosFiltrada by remember(vBusqueda, listaManos) {
        derivedStateOf {
            listaManos.filter { mano ->
                mano.nombre.contains(vBusqueda, ignoreCase = true)
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
                    onClick = onAddManoClick,
                    containerColor = COLOR_MANOS_BACKGROUND,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Mano")
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
                CircularProgressIndicator(color = COLOR_MANOS_BACKGROUND)
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
                                text = "Manos de Póker",
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
                            placeholderText = "Buscar Mano...",
                            showFilterButton = false
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (listaManosFiltrada.isEmpty()) {
                        item {
                            _Empty_State(
                                mensaje = "No se encontraron Manos",
                                subtitulo = if (listaManos.isEmpty())
                                    "Aún no hay Manos registradas en la base de datos."
                                else
                                    "No hay Manos que coincidan con tu búsqueda o filtros."
                            )
                        }
                    } else {
                        items(listaManosFiltrada) { mano ->
                            _Balatro_Row_Item(
                                nombre = mano.nombre,
                                imageUrl = null,
                                backgroundColor = COLOR_MANOS_BACKGROUND,
                                isAdmin = isAdmin,
                                onEditClick = { onEditManoClick(mano.id) },
                                onDeleteClick = { vManoAEliminar = mano.id },
                                onItemClick = { onManoClick(mano.id) }
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

    if (vManoAEliminar != null) {
        _Delete_Confirmation_Dialog(
            titulo = "Eliminar Mano",
            mensaje = "¿Estás seguro de que deseas eliminar esta mano de la base de datos?",
            onDismiss = { vManoAEliminar = null },
            onConfirm = {
                val idBorrar = vManoAEliminar
                vManoAEliminar = null

                if (idBorrar != null) {
                    viewModel._Eliminar_Mano(
                        id = idBorrar,
                        onSuccess = {
                            Toast.makeText(context, "Mano eliminada", Toast.LENGTH_SHORT).show()
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