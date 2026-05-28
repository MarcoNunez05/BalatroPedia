package com.example.balatropedia.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
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
import com.example.balatropedia.models.JokersViewModel
import com.example.balatropedia.ui.theme.COLOR_JOKER_BACKGROUND
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.balatropedia.ui.theme._BALATRO_FONT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun _Jokers_Screen(
    isAdmin: Boolean,
    onProfileClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onAddJokerClick: () -> Unit,
    onEditJokerClick: (String) -> Unit,
    onJokerClick: (String) -> Unit,
    viewModel: JokersViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
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
                CircularProgressIndicator(color = Color(0xFFC33C3C))
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TextField(
                                value = vBusqueda,
                                onValueChange = { vBusqueda = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .border(1.dp, Color(0xFF3F4552), RoundedCornerShape(6.dp)),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF252932),
                                    unfocusedContainerColor = Color(0xFF252932),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(6.dp),
                                placeholder = { Text("Buscar Joker...", color = Color.Gray, fontSize = 16.sp) },
                                singleLine = true
                            )

                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filtrar",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable { vShowFilterSheet = true }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

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

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (vShowFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { vShowFilterSheet = false },
            containerColor = Color(0xFF1E222B),
            scrimColor = Color.Black.copy(alpha = 0.5f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Filtrar por Rareza",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = _BALATRO_FONT,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val opcionesFiltro = listOf("Todas", "Común", "Poco común", "Rara", "Legendaria")

                opcionesFiltro.forEach { rareza ->
                    val esSeleccionado = vFiltroRareza == rareza

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (esSeleccionado) Color(0xFF3F4552) else Color.Transparent,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                vFiltroRareza = rareza
                                vShowFilterSheet = false
                            }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = rareza,
                            color = if (esSeleccionado) Color(0xFF00BFFF) else Color.White,
                            fontSize = 18.sp
                        )

                        if (esSeleccionado) {
                            Text(text = "✓", color = Color(0xFF00BFFF), fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }

    if (vJokerAEliminar != null) {
        AlertDialog(
            onDismissRequest = { vJokerAEliminar = null },
            containerColor = Color(0xFF252932),
            titleContentColor = Color.White,
            textContentColor = Color.LightGray,
            title = {
                Text(text = "Eliminar Joker", fontFamily = _BALATRO_FONT)
            },
            text = {
                Text(text = "¿Estás seguro de que deseas eliminar este Joker de la base de datos? Esta acción no se puede deshacer."
                    , fontSize = 20.sp, fontFamily = _BALATRO_FONT)
            },
            confirmButton = {
                TextButton(
                    onClick = {
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
                ) {
                    Text("Eliminar", color = Color(0xFFC33C3C), fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 20.sp, fontFamily = _BALATRO_FONT)
                }
            },
            dismissButton = {
                TextButton(onClick = { vJokerAEliminar = null }) {
                    Text("Cancelar", color = Color.White, fontSize = 20.sp, fontFamily = _BALATRO_FONT)
                }
            }
        )
    }
}