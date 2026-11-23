// Última modificación: 21/11/2026
// Autor: Marco Núñez

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.components._Balatro_Primary_Button
import com.example.balatropedia.components._Balatro_Selector
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.models.AccessibilityState
import com.example.balatropedia.ui.theme._BALATRO_FONT

@Composable
fun _Accessibility_Screen(
    currentState: AccessibilityState,
    onSavePreferences: (
        fontScaleOption: String,
        colorBlindOption: String,
        animationsOption: String,
        talkBackOption: String
    ) -> Unit,
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Opciones para los selectores
    val listaTamañoTexto = listOf("Normal (100%)", "Grande (150%)", "Muy Grande (200%)")
    val listaModosDaltonismo = listOf(
        "Desactivado",
        "Protanopia (Rojo)",
        "Deuteranopia (Verde)",
        "Tritanopia (Azul)",
        "Alto Contraste / Monocromo"
    )
    val listaAnimaciones = listOf("Activada", "Desactivada")
    val listaLecturaVoz = listOf("Desactivado", "Optimizado para TalkBack")

    // Estados sincronizados con las preferencias actuales guardadas
    var vTamañoTexto by remember(currentState.fontScaleOption) {
        mutableStateOf(currentState.fontScaleOption)
    }
    var vModoDaltonismo by remember(currentState.colorBlindMode) {
        mutableStateOf(currentState.colorBlindMode.label)
    }
    var vTransicion by remember(currentState.transitionToggle) {
        mutableStateOf(if (currentState.transitionToggle) "Desactivada" else "Activada")
    }
    var vLecturaVoz by remember(currentState.talkBackOptimized) {
        mutableStateOf(if (currentState.talkBackOptimized) "Optimizado para TalkBack" else "Desactivado")
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Accesibilidad",
                color = Color.White,
                fontSize = 36.sp,
                fontFamily = _BALATRO_FONT,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 28.dp)
            )

            // --- SECCIÓN 1: VISUAL ---
            Text(
                text = "Ajustes Visuales",
                color = Color(0xFF00BFFF),
                fontSize = 20.sp,
                fontFamily = _BALATRO_FONT,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            _Balatro_Selector(
                label = "Tamaño de texto",
                value = vTamañoTexto,
                options = listaTamañoTexto,
                onOptionSelected = { vTamañoTexto = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Selector(
                label = "Filtro para daltonismo",
                value = vModoDaltonismo,
                options = listaModosDaltonismo,
                onOptionSelected = { vModoDaltonismo = it }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // --- SECCIÓN 2: INTERACCIÓN Y MOVIMIENTO ---
            Text(
                text = "Movimiento y Lectura",
                color = Color(0xFF00BFFF),
                fontSize = 20.sp,
                fontFamily = _BALATRO_FONT,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            _Balatro_Selector(
                label = "Transición entre pantallas",
                value = vTransicion,
                options = listaAnimaciones,
                onOptionSelected = { vTransicion = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            _Balatro_Selector(
                label = "Compatibilidad con Lector de Pantalla",
                value = vLecturaVoz,
                options = listaLecturaVoz,
                onOptionSelected = { vLecturaVoz = it }
            )

            Spacer(modifier = Modifier.height(36.dp))

            _Balatro_Primary_Button(
                isLoading = false,
                text = "Guardar preferencias",
                onClick = {
                    onSavePreferences(
                        vTamañoTexto,
                        vModoDaltonismo,
                        vTransicion,
                        vLecturaVoz
                    )
                    Toast.makeText(
                        context,
                        "Preferencias de accesibilidad guardadas",
                        Toast.LENGTH_SHORT
                    ).show()
                    onNavigateBack()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}