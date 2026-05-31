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
import com.example.balatropedia.models.VoucherViewModel
import com.example.balatropedia.ui.theme.COLOR_VOUCHERS_BACKGROUND
import com.example.balatropedia.ui.theme._BALATRO_FONT

@Composable
// Pantalla de visualización de Vouchers
fun _Voucher_Screen(
    isAdmin: Boolean,
    onProfileClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onAddVoucherClick: () -> Unit,
    onEditVoucherClick: (String) -> Unit,
    onVoucherClick: (String) -> Unit,
    viewModel: VoucherViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var vBusqueda by remember { mutableStateOf("") }
    val listaVouchers = viewModel.vouchers.value
    val cargando = viewModel.isLoading.value

    var vVoucherAEliminar by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val listaVouchersFiltrada by remember(vBusqueda, listaVouchers) {
        derivedStateOf {
            listaVouchers.filter { voucher ->
                voucher.nombre.contains(vBusqueda, ignoreCase = true)
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
                    onClick = onAddVoucherClick,
                    containerColor = COLOR_VOUCHERS_BACKGROUND,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Voucher")
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
                CircularProgressIndicator(color = COLOR_VOUCHERS_BACKGROUND)
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
                                text = "Vouchers",
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
                            placeholderText = "Buscar Voucher...",
                            onFilterClick = {
                                Toast.makeText(context, "Filtros próximamente", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (listaVouchersFiltrada.isEmpty()) {
                        item {
                            _Empty_State(
                                mensaje = "No se encontraron Vouchers",
                                subtitulo = if (listaVouchers.isEmpty())
                                    "Aún no hay Vouchers registrados en la base de datos."
                                else
                                    "No hay Vouchers que coincidan con tu búsqueda o filtros."
                            )
                        }
                    } else {
                        items(listaVouchersFiltrada) { voucher ->
                            _Balatro_Row_Item(
                                nombre = voucher.nombre,
                                imageUrl = voucher.imagen_url,
                                backgroundColor = COLOR_VOUCHERS_BACKGROUND,
                                isAdmin = isAdmin,
                                onEditClick = { onEditVoucherClick(voucher.id) },
                                onDeleteClick = { vVoucherAEliminar = voucher.id },
                                onItemClick = { onVoucherClick(voucher.id) }
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

    if (vVoucherAEliminar != null) {
        _Delete_Confirmation_Dialog(
            titulo = "Eliminar Voucher",
            mensaje = "¿Estás seguro de que deseas eliminar este Voucher de la base de datos? Esta acción no se puede deshacer.",
            onDismiss = { vVoucherAEliminar = null },
            onConfirm = {
                val idBorrar = vVoucherAEliminar
                if (idBorrar != null) {
                    viewModel._Eliminar_Voucher(
                        id = idBorrar,
                        onSuccess = {
                            Toast.makeText(context, "Voucher eliminado correctamente", Toast.LENGTH_SHORT).show()
                            vVoucherAEliminar = null
                        },
                        onError = { mensajeError ->
                            Toast.makeText(context, mensajeError, Toast.LENGTH_SHORT).show()
                            vVoucherAEliminar = null
                        }
                    )
                }
            }
        )
    }
}

