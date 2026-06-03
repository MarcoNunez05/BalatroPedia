package com.example.balatropedia.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.components._Balatro_Input
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.components._Related_Items_Box
import com.example.balatropedia.class_models.ItemSelectorModel
import com.example.balatropedia.components._Balatro_Primary_Button
import com.example.balatropedia.components._Item_Selector
import com.example.balatropedia.models.ChallengeViewModel
import com.example.balatropedia.ui.theme._BALATRO_FONT

@Composable
// Pantalla para añadir un Challenge a la base de datos
fun _Challenge_Add_Screen(
    viewModel: ChallengeViewModel,
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var vNombre by remember { mutableStateOf("") }
    var vDescripcion by remember { mutableStateOf("") }
    var vRequisitos by remember { mutableStateOf("") }
    var vIsSaving by remember { mutableStateOf(false) }

    val jokersIncluidos = remember { mutableStateListOf<ItemSelectorModel>() }
    val consumiblesIncluidos = remember { mutableStateListOf<ItemSelectorModel>() }
    val vouchersIncluidos = remember { mutableStateListOf<ItemSelectorModel>() }

    val jokersProhibidos = remember { mutableStateListOf<ItemSelectorModel>() }
    val consumiblesProhibidos = remember { mutableStateListOf<ItemSelectorModel>() }
    val vouchersProhibidos = remember { mutableStateListOf<ItemSelectorModel>() }

    var vShowJokersIncSheet by remember { mutableStateOf(false) }
    var vShowConsumiblesIncSheet by remember { mutableStateOf(false) }
    var vShowVouchersIncSheet by remember { mutableStateOf(false) }

    var vShowJokersProSheet by remember { mutableStateOf(false) }
    var vShowConsumiblesProSheet by remember { mutableStateOf(false) }
    var vShowVouchersProSheet by remember { mutableStateOf(false) }

    val listaJokersSelector by viewModel.jokersSelectorList.collectAsState()
    val listaConsumiblesSelector by viewModel.consumiblesSelectorList.collectAsState()
    val listaVouchersSelector by viewModel.vouchersSelectorList.collectAsState()

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
                text = "Añadir nuevo Challenge",
                color = Color.White,
                fontSize = 32.sp,
                fontFamily = _BALATRO_FONT
            )

            Spacer(modifier = Modifier.height(24.dp))

            _Balatro_Input(
                label = "Nombre del Challenge",
                value = vNombre,
                onValueChange = { vNombre = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Input(
                label = "Descripción",
                value = vDescripcion,
                onValueChange = { vDescripcion = it },
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Input(
                label = "Requisitos de desbloqueo",
                value = vRequisitos,
                onValueChange = { vRequisitos = it },
                singleLine = false,
                minLines = 2
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Elementos Incluidos",
                color = Color(0xFF2E8B57),
                fontSize = 18.sp,
                fontFamily = _BALATRO_FONT,
                modifier = Modifier.align(Alignment.Start).padding(vertical = 8.dp)
            )

            _Related_Items_Box(
                label = "Jokers Incluidos",
                instructions = "Jokers con los que se inicia el Challenge.",
                buttonText = "Incluir Joker",
                selectedItems = jokersIncluidos,
                onAddClick = { vShowJokersIncSheet = true },
                onRemoveItem = { jokersIncluidos.remove(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Related_Items_Box(
                label = "Consumibles Incluidos",
                instructions = "Consumibles iniciales en el inventario.",
                buttonText = "Incluir Consumible",
                selectedItems = consumiblesIncluidos,
                onAddClick = { vShowConsumiblesIncSheet = true },
                onRemoveItem = { consumiblesIncluidos.remove(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Related_Items_Box(
                label = "Vouchers Incluidos",
                instructions = "Vouchers activos desde el inicio.",
                buttonText = "Incluir Voucher",
                selectedItems = vouchersIncluidos,
                onAddClick = { vShowVouchersIncSheet = true },
                onRemoveItem = { vouchersIncluidos.remove(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Elementos Prohibidos",
                color = Color(0xFFC33C3C),
                fontSize = 18.sp,
                fontFamily = _BALATRO_FONT,
                modifier = Modifier.align(Alignment.Start).padding(vertical = 8.dp)
            )

            _Related_Items_Box(
                label = "Jokers Prohibidos",
                instructions = "Jokers que no aparecerán en la partida.",
                buttonText = "Prohibir Joker",
                selectedItems = jokersProhibidos,
                onAddClick = { vShowJokersProSheet = true },
                onRemoveItem = { jokersProhibidos.remove(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Related_Items_Box(
                label = "Consumibles Prohibidos",
                instructions = "Consumibles vetados de la tienda o Booster Packs.",
                buttonText = "Prohibir Consumible",
                selectedItems = consumiblesProhibidos,
                onAddClick = { vShowConsumiblesProSheet = true },
                onRemoveItem = { consumiblesProhibidos.remove(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Related_Items_Box(
                label = "Vouchers Prohibidos",
                instructions = "Vouchers que no saldrán en la tienda.",
                buttonText = "Prohibir Voucher",
                selectedItems = vouchersProhibidos,
                onAddClick = { vShowVouchersProSheet = true },
                onRemoveItem = { vouchersProhibidos.remove(it) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            _Balatro_Primary_Button(
                text = "Registrar nuevo Challenge",
                isLoading = vIsSaving,
                onClick = {
                    if (vNombre.isBlank() || vDescripcion.isBlank() || vRequisitos.isBlank()) {
                        Toast.makeText(context, "Faltan datos obligatorios", Toast.LENGTH_SHORT).show()
                        return@_Balatro_Primary_Button
                    }

                    vIsSaving = true
                    viewModel._Añadir_Nuevo_Challenge(
                        nombre = vNombre,
                        descripcion = vDescripcion,
                        requisitos = vRequisitos,
                        jokersIncluidos = jokersIncluidos,
                        consumiblesIncluidos = consumiblesIncluidos,
                        vouchersIncluidos = vouchersIncluidos,
                        jokersProhibidos = jokersProhibidos,
                        consumiblesProhibidos = consumiblesProhibidos,
                        vouchersProhibidos = vouchersProhibidos,
                        onSuccess = {
                            Toast.makeText(context, "¡Challenge añadido!", Toast.LENGTH_SHORT).show()
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

    // ELEMENTOS INCLUIDOS
    if (vShowJokersIncSheet) {
        _Item_Selector(
            titulo = "Añadir Joker Incluido",
            itemsDisponibles = listaJokersSelector,
            onDismiss = { vShowJokersIncSheet = false },
            onItemSelected = { item ->
                if (!jokersIncluidos.any { it.id == item.id }) jokersIncluidos.add(item)
                vShowJokersIncSheet = false
            }
        )
    }

    if (vShowConsumiblesIncSheet) {
        _Item_Selector(
            titulo = "Añadir Consumible Incluido",
            itemsDisponibles = listaConsumiblesSelector,
            onDismiss = { vShowConsumiblesIncSheet = false },
            onItemSelected = { item ->
                if (!consumiblesIncluidos.any { it.id == item.id }) consumiblesIncluidos.add(item)
                vShowConsumiblesIncSheet = false
            }
        )
    }

    if (vShowVouchersIncSheet) {
        _Item_Selector(
            titulo = "Añadir Voucher Incluido",
            itemsDisponibles = listaVouchersSelector,
            onDismiss = { vShowVouchersIncSheet = false },
            onItemSelected = { item ->
                if (!vouchersIncluidos.any { it.id == item.id }) vouchersIncluidos.add(item)
                vShowVouchersIncSheet = false
            }
        )
    }

    // ELEMENTOS PROHIBIDOS
    if (vShowJokersProSheet) {
        _Item_Selector(
            titulo = "Añadir Joker Prohibido",
            itemsDisponibles = listaJokersSelector,
            onDismiss = { vShowJokersProSheet = false },
            onItemSelected = { item ->
                if (!jokersProhibidos.any { it.id == item.id }) jokersProhibidos.add(item)
                vShowJokersProSheet = false
            }
        )
    }

    if (vShowConsumiblesProSheet) {
        _Item_Selector(
            titulo = "Añadir Consumible Prohibido",
            itemsDisponibles = listaConsumiblesSelector,
            onDismiss = { vShowConsumiblesProSheet = false },
            onItemSelected = { item ->
                if (!consumiblesProhibidos.any { it.id == item.id }) consumiblesProhibidos.add(item)
                vShowConsumiblesProSheet = false
            }
        )
    }

    if (vShowVouchersProSheet) {
        _Item_Selector(
            titulo = "Añadir Voucher Prohibido",
            itemsDisponibles = listaVouchersSelector,
            onDismiss = { vShowVouchersProSheet = false },
            onItemSelected = { item ->
                if (!vouchersProhibidos.any { it.id == item.id }) vouchersProhibidos.add(item)
                vShowVouchersProSheet = false
            }
        )
    }
}

