package com.example.balatropedia.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.balatropedia.components._Balatro_Row_Item
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.models.JokerViewModel
import com.example.balatropedia.ui.theme.COLOR_JOKER_BACKGROUND
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.balatropedia.components._Delete_Confirmation_Dialog
import com.example.balatropedia.components._Empty_State
import com.example.balatropedia.components._Filter_Bottom_Sheet
import com.example.balatropedia.components._Search_Filter_Bar
import com.example.balatropedia.ui.theme.COLOR_JOKER_TEXT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Pantalla de visualización de Jokers
fun _Joker_Screen(
    isAdmin: Boolean,
    onProfileClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onAddJokerClick: () -> Unit,
    onEditJokerClick: (String) -> Unit,
    onJokerClick: (String) -> Unit,
    viewModel: JokerViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var vBusqueda by remember { mutableStateOf("") }
    var vShowFilterSheet by remember { mutableStateOf(false) }
    var vFiltroRareza by remember { mutableStateOf("Todas") }

    val listaJokers = viewModel.jokers.value
    val cargando = viewModel.isLoading.value

    var vJokerAEliminar by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val listaJokersFiltrada by remember(vBusqueda, vFiltroRareza, listaJokers) {
        derivedStateOf {
            listaJokers.filter { joker ->
                val cumpleBusqueda = joker.nombre.contains(vBusqueda, ignoreCase = true)

                val cumpleRareza = vFiltroRareza == "Todas" || joker.rareza.equals(vFiltroRareza, ignoreCase = true)

                cumpleBusqueda && cumpleRareza
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
                    onClick = onAddJokerClick,
                    containerColor = Color(0xFFC33C3C),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Joker")
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
                CircularProgressIndicator(color = COLOR_JOKER_BACKGROUND)
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
                                text = "Jokers",
                                color = Color.White,
                                fontSize = 30.sp,
                            )
                        }
                    }

                    item {
                        _Search_Filter_Bar(
                            searchQuery = vBusqueda,
                            onSearchQueryChange = { vBusqueda = it },
                            placeholderText = "Buscar Joker...",
                            onFilterClick = { vShowFilterSheet = true }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (listaJokersFiltrada.isEmpty()) {
                        item {
                            _Empty_State(
                                mensaje = "No se encontraron Jokers",
                                subtitulo = if (listaJokers.isEmpty())
                                    "Aún no hay Jokers registrados en la base de datos."
                                else
                                    "No hay Jokers que coincidan con tu búsqueda o filtros."
                            )
                        }
                    } else {
                        items(listaJokersFiltrada) { joker ->
                            _Balatro_Row_Item(
                                nombre = joker.nombre,
                                imageUrl = joker.imagen_url,
                                backgroundColor = COLOR_JOKER_BACKGROUND,
                                isAdmin = isAdmin,
                                onEditClick = { onEditJokerClick(joker.id) },
                                onDeleteClick = { vJokerAEliminar = joker.id},
                                onItemClick = {onJokerClick(joker.id)}
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

    if (vShowFilterSheet) {
        _Filter_Bottom_Sheet(
            titulo = "Filtrar por Rareza",
            opciones = listOf("Todas", "Común", "Poco común", "Rara", "Legendaria"),
            opcionSeleccionada = vFiltroRareza,
            onOptionSelected = { rarezaSeleccionada ->
                vFiltroRareza = rarezaSeleccionada
                vShowFilterSheet = false
            },
            onDismiss = { vShowFilterSheet = false }
        )
    }

    if (vJokerAEliminar != null) {
        _Delete_Confirmation_Dialog(
            titulo = "Eliminar Joker",
            mensaje = "¿Estás seguro de que deseas eliminar este Joker de la base de datos? Esta acción no se puede deshacer.",
            onDismiss = { vJokerAEliminar = null },
            onConfirm = {
                val idBorrar = vJokerAEliminar
                vJokerAEliminar = null

                if (idBorrar != null) {
                    viewModel._Eliminar_Joker(
                        id = idBorrar,
                        onSuccess = {
                            Toast.makeText(context, "Joker eliminado", Toast.LENGTH_SHORT).show()
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