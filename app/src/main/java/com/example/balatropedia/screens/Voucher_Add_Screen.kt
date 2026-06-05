// Última modificación: 04/06/2026
// Autor: Marco Núñez

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
import com.example.balatropedia.components._Balatro_Primary_Button
import com.example.balatropedia.models.VoucherViewModel
import com.example.balatropedia.ui.theme._BALATRO_FONT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Pantalla para añadir un Voucher a la base de datos
fun _Voucher_Add_Screen(
    viewModel: VoucherViewModel,
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var vNombre by remember { mutableStateOf("") }
    var vEfecto by remember { mutableStateOf("") }
    var vRequisitos by remember { mutableStateOf("") }
    var vImagenUrl by remember { mutableStateOf("") }
    var vIsSaving by remember { mutableStateOf(false) }

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
                text = "Añadir nuevo Voucher",
                color = Color.White,
                fontSize = 32.sp,
                fontFamily = _BALATRO_FONT
            )

            Spacer(modifier = Modifier.height(24.dp))

            _Balatro_Input(
                label = "Nombre del Voucher",
                value = vNombre,
                onValueChange = { vNombre = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Input(
                label = "Efecto del Voucher",
                value = vEfecto,
                onValueChange = { vEfecto = it },
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Input(
                label = "Requisitos para desbloquear",
                value = vRequisitos,
                onValueChange = { vRequisitos = it },
                singleLine = false,
                minLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Imagen del Voucher (URL)",
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
                            .size(70.dp)
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

            Spacer(modifier = Modifier.height(40.dp))

            _Balatro_Primary_Button(
                text = "Registrar nuevo Voucher",
                isLoading = vIsSaving,
                onClick = {
                    if (vNombre.isBlank() || vEfecto.isBlank() || vImagenUrl.isBlank() || vRequisitos.isBlank()) {
                        Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                        return@_Balatro_Primary_Button
                    }

                    vIsSaving = true
                    viewModel._Añadir_Nuevo_Voucher(
                        nombre = vNombre,
                        efecto = vEfecto,
                        requisitos = vRequisitos,
                        imagenUrl = vImagenUrl,
                        onSuccess = {
                            Toast.makeText(context, "¡Voucher añadido!", Toast.LENGTH_SHORT).show()
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
}

