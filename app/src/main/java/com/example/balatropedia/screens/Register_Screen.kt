package com.example.balatropedia.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.components._Balatro_Input
import com.example.balatropedia.components._Balatro_Primary_Button
import com.example.balatropedia.components._Balatro_Selector
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.ui.theme._BALATRO_FONT
import com.example.balatropedia.viewmodels.AuthViewModel

@Composable
// Pantalla de registro
fun _Register_Screen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val scrollState = rememberScrollState()

    val listaPaises = listOf("Argentina", "Chile", "Colombia", "España", "México", "Perú", "Otros")
    val listaEdades = (14..99).map { it.toString() }

    // Estados de los campos
    var vUsername by remember { mutableStateOf("") }
    var vEmail by remember { mutableStateOf("") }
    var vPassword by remember { mutableStateOf("") }
    var vConfirmPassword by remember { mutableStateOf("") }
    var vPais by remember { mutableStateOf("Selecciona un país") }
    var vEdad by remember { mutableStateOf("18") }

    // Estados de UI
    var vIsLoading by remember { mutableStateOf(false) }
    var vErrorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize().safeDrawingPadding(),
        topBar = {
            _Balatropedia_Header(
                comeBack = true,
                onBackClick = onNavigateBack,
                onProfileClick = { }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E222B))
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registra una cuenta",
                color = Color.White,
                fontSize = 36.sp,
                fontFamily = _BALATRO_FONT,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            _Balatro_Input(label = "Nombre de usuario", value = vUsername, onValueChange = { vUsername = it })
            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Input(label = "Correo electrónico", value = vEmail, onValueChange = { vEmail = it })
            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Input(label = "Contraseña", value = vPassword, isPassword = true, onValueChange = { vPassword = it })
            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Input(label = "Confirmar contraseña", value = vConfirmPassword, isPassword = true, onValueChange = { vConfirmPassword = it })
            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Selector(
                label = "País",
                value = vPais,
                options = listaPaises,
                onOptionSelected = { vPais = it; vErrorMessage = null }
            )
            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Selector(
                label = "Edad",
                value = vEdad,
                options = listaEdades,
                onOptionSelected = { vEdad = it; vErrorMessage = null },
            )

            vErrorMessage?.let {
                Text(text = it, color = Color(0xFFE57373), fontSize = 20.sp, modifier = Modifier.padding(top = 16.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            _Balatro_Primary_Button(
                text = "Registrarse",
                isLoading = vIsLoading,
                onClick = {
                    viewModel._Registrar_Usuario(
                        vUsername, vEmail, vPassword, vConfirmPassword, vPais, vEdad,
                        onSuccess = onRegisterSuccess
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}