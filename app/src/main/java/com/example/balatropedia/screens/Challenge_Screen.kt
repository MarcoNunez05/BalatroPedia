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
import com.example.balatropedia.models.ChallengeViewModel
import com.example.balatropedia.ui.theme.COLOR_CHALLENGES_BACKGROUND
import com.example.balatropedia.ui.theme._BALATRO_FONT

@Composable
// Pantalla de visualización de Challenges
fun _Challenge_Screen(
    isAdmin: Boolean,
    onProfileClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onAddChallengeClick: () -> Unit,
    onEditChallengeClick: (String) -> Unit,
    onChallengeClick: (String) -> Unit,
    viewModel: ChallengeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var vBusqueda by remember { mutableStateOf("") }
    val listaChallenges = viewModel.challenges.value
    val cargando = viewModel.isLoading.value

    var vChallengeAEliminar by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val listaChallengesFiltrada by remember(vBusqueda, listaChallenges) {
        derivedStateOf {
            listaChallenges.filter { challenge ->
                challenge.nombre.contains(vBusqueda, ignoreCase = true)
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
                    onClick = onAddChallengeClick,
                    containerColor = COLOR_CHALLENGES_BACKGROUND,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Challenge")
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
                CircularProgressIndicator(color = COLOR_CHALLENGES_BACKGROUND)
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
                                text = "Challenges",
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
                            placeholderText = "Buscar Challenge...",
                            onFilterClick = {
                                Toast.makeText(context, "Filtros próximamente", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (listaChallengesFiltrada.isEmpty()) {
                        item {
                            _Empty_State(
                                mensaje = "No se encontraron Challenges",
                                subtitulo = if (listaChallenges.isEmpty())
                                    "Aún no hay Challenges registrados en la base de datos."
                                else
                                    "No hay Challenges que coincidan con tu búsqueda o filtros."
                            )
                        }
                    } else {
                        items(listaChallengesFiltrada) { challenge ->
                            _Balatro_Row_Item(
                                nombre = challenge.nombre,
                                imageUrl = null,
                                backgroundColor = COLOR_CHALLENGES_BACKGROUND,
                                isAdmin = isAdmin,
                                onEditClick = { onEditChallengeClick(challenge.id) },
                                onDeleteClick = { vChallengeAEliminar = challenge.id },
                                onItemClick = { onChallengeClick(challenge.id) }
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

    if (vChallengeAEliminar != null) {
        _Delete_Confirmation_Dialog(
            titulo = "Eliminar Challenge",
            mensaje = "¿Estás seguro de que deseas eliminar este Challenge de la base de datos?",
            onDismiss = { vChallengeAEliminar = null },
            onConfirm = {
                val idBorrar = vChallengeAEliminar
                vChallengeAEliminar = null

                if (idBorrar != null) {
                    viewModel._Eliminar_Challenge(
                        id = idBorrar,
                        onSuccess = {
                            Toast.makeText(context, "Challenge eliminado", Toast.LENGTH_SHORT).show()
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