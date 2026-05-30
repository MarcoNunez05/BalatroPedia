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
import com.example.balatropedia.models.MazoViewModel
import com.example.balatropedia.ui.theme.COLOR_MAZOS_BACKGROUND
import com.example.balatropedia.ui.theme._BALATRO_FONT

@Composable
// Pantalla de visualización de Mazos
fun _Mazos_Screen(
    isAdmin: Boolean,
    onProfileClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onAddMazoClick: () -> Unit,
    onEditMazoClick: (String) -> Unit,
    onMazoClick: (String) -> Unit,
    viewModel: MazoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var vBusqueda by remember { mutableStateOf("") }
    val listaMazos = viewModel.mazos.value
    val cargando = viewModel.isLoading.value

    var vMazoAEliminar by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val listaMazosFiltrada by remember(vBusqueda, listaMazos) {
        derivedStateOf {
            listaMazos.filter { mazo ->
                mazo.nombre.contains(vBusqueda, ignoreCase = true)
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
                    onClick = onAddMazoClick,
                    containerColor = COLOR_MAZOS_BACKGROUND,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Mazo")
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
                CircularProgressIndicator(color = COLOR_MAZOS_BACKGROUND)
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
                                text = "Mazos",
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
                            placeholderText = "Buscar Mazo...",
                            onFilterClick = {
                                Toast.makeText(context, "Filtros próximamente", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (listaMazosFiltrada.isEmpty()) {
                        item {
                            _Empty_State(
                                mensaje = "No se encontraron Mazos",
                                subtitulo = if (listaMazos.isEmpty())
                                    "Aún no hay Mazos registrados en la base de datos."
                                else
                                    "No hay Mazos que coincidan con tu búsqueda o filtros."
                            )
                        }
                    } else {
                        items(listaMazosFiltrada) { mazo ->
                            _Balatro_Row_Item(
                                nombre = mazo.nombre,
                                imageUrl = mazo.imagen_url,
                                backgroundColor = COLOR_MAZOS_BACKGROUND,
                                isAdmin = isAdmin,
                                onEditClick = { onEditMazoClick(mazo.id) },
                                onDeleteClick = { vMazoAEliminar = mazo.id },
                                onItemClick = { onMazoClick(mazo.id) }
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

    if (vMazoAEliminar != null) {
        _Delete_Confirmation_Dialog(
            titulo = "Eliminar Mazo",
            mensaje = "¿Estás seguro de que deseas eliminar este mazo de la base de datos?",
            onDismiss = { vMazoAEliminar = null },
            onConfirm = {
                val idBorrar = vMazoAEliminar
                vMazoAEliminar = null

                if (idBorrar != null) {
                    viewModel._Eliminar_Mazo(
                        id = idBorrar,
                        onSuccess = {
                            Toast.makeText(context, "Mazo eliminado", Toast.LENGTH_SHORT).show()
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