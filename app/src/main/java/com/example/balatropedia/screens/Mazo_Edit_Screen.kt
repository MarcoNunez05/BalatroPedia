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
import com.example.balatropedia.models.MazoViewModel
import com.example.balatropedia.ui.theme._BALATRO_FONT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Pantalla para editar un Mazo existente
fun _Mazo_Edit_Screen(
    mazoId: String,
    viewModel: MazoViewModel,
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var vNombre by remember { mutableStateOf("") }
    var vDescripcion by remember { mutableStateOf("") }
    var vImagenUrl by remember { mutableStateOf("") }
    var vIsSaving by remember { mutableStateOf(false) }

    val VouchersIncluidos = remember { mutableStateListOf<ItemSelectorModel>() }
    val ConsumiblesIncluidos = remember { mutableStateListOf<ItemSelectorModel>() }

    var vShowVoucherSheet by remember { mutableStateOf(false) }
    var vShowConsumableSheet by remember { mutableStateOf(false) }

    val listaVouchersSelector by viewModel.vouchersSelectorList.collectAsState()
    val listaConsumiblesSelector by viewModel.consumiblesSelectorList.collectAsState()

    LaunchedEffect(mazoId) {
        val mazoOriginal = viewModel._Obtener_Mazo_Por_ID(mazoId)
        if (mazoOriginal != null) {
            vNombre = mazoOriginal.nombre
            vDescripcion = mazoOriginal.descripcion
            vImagenUrl = mazoOriginal.imagen_url

            VouchersIncluidos.clear()
            mazoOriginal.vouchersIncluidos.forEach { map ->
                VouchersIncluidos.add(
                    ItemSelectorModel(
                        id = map["id"] ?: "",
                        nombre = map["nombre"] ?: "",
                        imagenUrl = map["imagenUrl"] ?: ""
                    )
                )
            }

            ConsumiblesIncluidos.clear()
            mazoOriginal.consumiblesIncluidos.forEach { map ->
                ConsumiblesIncluidos.add(
                    ItemSelectorModel(
                        id = map["id"] ?: "",
                        nombre = map["nombre"] ?: "",
                        imagenUrl = map["imagenUrl"] ?: ""
                    )
                )
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
                text = "Editar Mazo",
                color = Color.White,
                fontSize = 32.sp,
                fontFamily = _BALATRO_FONT
            )

            Spacer(modifier = Modifier.height(24.dp))

            _Balatro_Input(
                label = "Nombre del Mazo",
                value = vNombre,
                onValueChange = { vNombre = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Input(
                label = "Descripción del Mazo",
                value = vDescripcion,
                onValueChange = { vDescripcion = it },
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Related_Items_Box(
                label = "Vouchers que incluye este mazo",
                instructions = "Selecciona cupones de la base de datos.",
                buttonText = "Añadir Voucher",
                selectedItems = VouchersIncluidos,
                onAddClick = { vShowVoucherSheet = true },
                onRemoveItem = { VouchersIncluidos.remove(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Related_Items_Box(
                label = "Consumibles que incluye este mazo",
                instructions = "Cartas de Tarot o Planeta iniciales.",
                buttonText = "Añadir Consumible",
                selectedItems = ConsumiblesIncluidos,
                onAddClick = { vShowConsumableSheet = true },
                onRemoveItem = { ConsumiblesIncluidos.remove(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Imagen del Mazo (URL)",
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
                text = "Guardar Cambios",
                isLoading = vIsSaving,
                onClick = {
                    if (vNombre.isBlank() || vDescripcion.isBlank() || vImagenUrl.isBlank()) {
                        Toast.makeText(context, "Faltan datos obligatorios", Toast.LENGTH_SHORT).show()
                        return@_Balatro_Primary_Button
                    }

                    vIsSaving = true
                    viewModel._Actualizar_Mazo(
                        id = mazoId,
                        nombre = vNombre,
                        descripcion = vDescripcion,
                        vouchersIncluidos = VouchersIncluidos,
                        consumiblesIncluidos = ConsumiblesIncluidos,
                        imagenUrl = vImagenUrl,
                        onSuccess = {
                            Toast.makeText(context, "¡Mazo actualizado con éxito!", Toast.LENGTH_SHORT).show()
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

    if (vShowVoucherSheet) {
        _Item_Selector(
            titulo = "Añadir Voucher",
            itemsDisponibles = listaVouchersSelector,
            onDismiss = { vShowVoucherSheet = false },
            onItemSelected = { voucherSeleccionado ->
                if (!VouchersIncluidos.any { it.id == voucherSeleccionado.id }) {
                    VouchersIncluidos.add(voucherSeleccionado)
                }
                vShowVoucherSheet = false
            }
        )
    }

    if (vShowConsumableSheet) {
        _Item_Selector(
            titulo = "Añadir Consumible",
            itemsDisponibles = listaConsumiblesSelector,
            onDismiss = { vShowConsumableSheet = false },
            onItemSelected = { consumibleSeleccionado ->
                if (!ConsumiblesIncluidos.any { it.id == consumibleSeleccionado.id }) {
                    ConsumiblesIncluidos.add(consumibleSeleccionado)
                }
                vShowConsumableSheet = false
            }
        )
    }
}

