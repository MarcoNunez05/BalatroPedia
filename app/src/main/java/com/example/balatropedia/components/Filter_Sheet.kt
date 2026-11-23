// Última modificación: 21/11/2026
// Autor: Marco Núñez

package com.example.balatropedia.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.models.ColorBlindMode
import com.example.balatropedia.models.LocalAccessibilityState
import com.example.balatropedia.models.ModalAccessibilityWrapper
import com.example.balatropedia.ui.theme._BALATRO_FONT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Sheet que muestra las opciones de filtros
fun _Filter_Bottom_Sheet(
    titulo: String,
    opciones: List<String>,
    opcionSeleccionada: String,
    onOptionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val accessibilityState = LocalAccessibilityState.current
    val baseDensity = LocalDensity.current
    val sheetDensity = remember(baseDensity) {
        Density(
            density = baseDensity.density,
            fontScale = baseDensity.fontScale
        )
    }
    val colorMatrix = remember(accessibilityState.colorBlindMode) {
        colorMatrixForMode(accessibilityState.colorBlindMode)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        dragHandle = null
    ) {
        ModalAccessibilityWrapper { filterModifier ->
            Box(
                modifier = filterModifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFF1E222B),
                        RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = titulo,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontFamily = _BALATRO_FONT,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    opciones.forEach { opcion ->
                        val esSeleccionado = opcionSeleccionada == opcion

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (esSeleccionado) Color(0xFF3F4552) else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    onOptionSelected(opcion)
                                }
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = opcion,
                                color = if (esSeleccionado) Color(0xFF00BFFF) else Color.White,
                                fontSize = 18.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            )

                            if (esSeleccionado) {
                                Text(text = "✓", color = Color(0xFF00BFFF), fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun colorMatrixForMode(mode: ColorBlindMode): ColorMatrix {
    return when (mode) {
        ColorBlindMode.NONE -> ColorMatrix()
        ColorBlindMode.PROTANOPIA -> ColorMatrix(
            floatArrayOf(
                0.567f, 0.433f, 0f, 0f, 0f,
                0.558f, 0.442f, 0f, 0f, 0f,
                0f,     0.242f, 0.758f, 0f, 0f,
                0f,     0f,     0f,     1f, 0f
            )
        )
        ColorBlindMode.DEUTERANOPIA -> ColorMatrix(
            floatArrayOf(
                0.625f, 0.375f, 0f, 0f, 0f,
                0.7f,   0.3f,   0f, 0f, 0f,
                0f,     0.3f,   0.7f, 0f, 0f,
                0f,     0f,     0f,   1f, 0f
            )
        )
        ColorBlindMode.TRITANOPIA -> ColorMatrix(
            floatArrayOf(
                0.95f, 0.05f,  0f,     0f, 0f,
                0f,    0.433f, 0.567f, 0f, 0f,
                0f,    0.475f, 0.525f, 0f, 0f,
                0f,    0f,     0f,     1f, 0f
            )
        )
        ColorBlindMode.MONOCHROME -> ColorMatrix().apply { setToSaturation(0f) }
    }
}