// Última modificación: 31/05/2026
// Autor: Marco Núñez

package com.example.balatropedia.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.components._Balatro_Input
import com.example.balatropedia.components._Balatro_Primary_Button
import com.example.balatropedia.components._Balatro_Selector
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.ui.theme._BALATRO_FONT
import com.example.balatropedia.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
// Pantalla de configuración de cuenta de usuario
fun _Config_Account_Screen(
    onNavigateBack: () -> Unit,
    onAccountDeleted: () -> Unit,
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val scrollState = rememberScrollState()
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }

    val context = androidx.compose.ui.platform.LocalContext.current

    val listaPaises = listOf("Argentina", "Chile", "Colombia", "España", "México", "Perú", "Otros")
    val listaEdades = (14..99).map { it.toString() }

    var vUsername by remember { mutableStateOf("") }
    var vPassword by remember { mutableStateOf("") }
    var vConfirmPassword by remember { mutableStateOf("") }
    var vPais by remember { mutableStateOf("Selecciona un país") }
    var vEdad by remember { mutableStateOf("18") }

    var vIsLoading by remember { mutableStateOf(false) }
    var vErrorMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var vDeletePassword by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                vUsername = doc.getString("username") ?: ""
                vPais = doc.getString("pais") ?: "Selecciona un país"
                vEdad = doc.getString("edad") ?: "18"
            }
        }
    }

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
                text = "Configuración",
                color = Color.White,
                fontSize = 36.sp,
                fontFamily = _BALATRO_FONT,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            _Balatro_Input(label = "Nombre de usuario", value = vUsername, onValueChange = { vUsername = it })
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
                onOptionSelected = { vEdad = it; vErrorMessage = null }
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Cambiar contraseña (Opcional)",
                color = Color.Gray,
                fontSize = 18.sp,
                fontFamily = _BALATRO_FONT,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            _Balatro_Input(label = "Nueva contraseña", value = vPassword, isPassword = true, onValueChange = { vPassword = it })
            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Input(label = "Confirmar contraseña", value = vConfirmPassword, isPassword = true, onValueChange = { vConfirmPassword = it })

            vErrorMessage?.let {
                Text(
                    text = it,
                    color = Color(0xFFE57373),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            _Balatro_Primary_Button(
                text = "Guardar cambios",
                isLoading = vIsLoading,
                onClick = {
                    vErrorMessage = null
                    viewModel._Actualizar_Usuario(
                        username = vUsername,
                        pais = vPais,
                        edad = vEdad,
                        pass = vPassword,
                        confirmPass = vConfirmPassword,
                        onSuccess = {
                            Toast.makeText(context, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        },
                        onError = { error -> vErrorMessage = error }
                    )
                }
            )

            Spacer(modifier = Modifier.height(48.dp))

            HorizontalDivider(color = Color.Gray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E1010)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Eliminar cuenta",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontFamily = _BALATRO_FONT
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    vDeletePassword = ""
                    vErrorMessage = null
                },
                title = {
                    Text(
                        text = "Eliminar Cuenta",
                        fontFamily = _BALATRO_FONT,
                        fontSize = 24.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Esta acción no se puede deshacer. Todos tus datos y puntuaciones se perderán de forma permanente.",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        _Balatro_Input(
                            label = "Ingresa tu contraseña para confirmar",
                            value = vDeletePassword,
                            isPassword = true,
                            onValueChange = { vDeletePassword = it }
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (vDeletePassword.isNotEmpty()) {
                                viewModel._Eliminar_Usuario(
                                    password = vDeletePassword,
                                    onSuccess = {
                                        showDeleteDialog = false
                                        Toast.makeText(context, "Cuenta eliminada de Balatropedia", Toast.LENGTH_LONG).show()
                                        onAccountDeleted()
                                    },
                                    onError = { error ->
                                        vErrorMessage = error
                                    }
                                )
                            }
                        },
                        enabled = vDeletePassword.isNotBlank()
                    ) {
                        Text(
                            text = "Sí, eliminar",
                            color = if (vDeletePassword.isNotBlank()) Color(0xFFCC1D1D) else Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            vDeletePassword = ""
                            vErrorMessage = null
                        }
                    ) {
                        Text("Cancelar", color = Color.Gray)
                    }
                },
                containerColor = Color(0xFF2C323F),
                titleContentColor = Color.White
            )
        }
    }
}
