// Última modificación: 21/11/2026
// Autor: Marco Núñez

package com.example.balatropedia.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.balatropedia.models.ColorBlindMode
import com.example.balatropedia.models.LocalAccessibilityState
import com.example.balatropedia.models.ModalAccessibilityWrapper
import com.example.balatropedia.ui.theme._BALATRO_FONT

@Composable
// Dialog para confirmar borrado de documento
fun _Delete_Confirmation_Dialog(
    titulo: String,
    mensaje: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val accessibilityState = LocalAccessibilityState.current
    val baseDensity = LocalDensity.current
    val dialogDensity = remember(baseDensity) {
        Density(
            density = baseDensity.density,
            fontScale = baseDensity.fontScale
        )
    }
    val colorMatrix = remember(accessibilityState.colorBlindMode) {
        colorMatrixForMode(accessibilityState.colorBlindMode)
    }

    Dialog(onDismissRequest = onDismiss) {
        ModalAccessibilityWrapper { filterModifier ->
            Box(
                modifier = filterModifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        compositingStrategy = CompositingStrategy.Offscreen
                        colorFilter = ColorFilter.colorMatrix(colorMatrix)
                    }
                    .background(Color(0xFF252932), RoundedCornerShape(16.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = titulo,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = _BALATRO_FONT,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = mensaje,
                        color = Color.LightGray,
                        fontSize = 20.sp,
                        fontFamily = _BALATRO_FONT
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = "Cancelar",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontFamily = _BALATRO_FONT,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(onClick = onConfirm) {
                            Text(
                                text = "Eliminar",
                                color = Color(0xFFC33C3C),
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                fontFamily = _BALATRO_FONT,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
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