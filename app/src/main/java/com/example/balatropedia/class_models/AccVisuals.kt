// Última modificación: 21/11/2026
// Autor: Marco Núñez

package com.example.balatropedia.models

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

fun colorMatrixForMode(mode: ColorBlindMode): ColorMatrix {
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

fun Modifier.colorBlindFilter(mode: ColorBlindMode): Modifier = composed {
    val colorMatrix = remember(mode) { colorMatrixForMode(mode) }
    this.graphicsLayer {
        compositingStrategy = CompositingStrategy.Offscreen
        colorFilter = ColorFilter.colorMatrix(colorMatrix)
    }
}

@Composable
fun ModalAccessibilityWrapper(
    content: @Composable (Modifier) -> Unit
) {
    val accessibilityState = LocalAccessibilityState.current
    val currentDensity = LocalDensity.current

    val effectiveFontScale = remember(currentDensity.fontScale, accessibilityState.fontScale) {
        if (currentDensity.fontScale > 1.01f) {
            currentDensity.fontScale
        } else {
            currentDensity.fontScale * accessibilityState.fontScale
        }
    }

    val resolvedDensity = remember(currentDensity.density, effectiveFontScale) {
        Density(
            density = currentDensity.density,
            fontScale = effectiveFontScale
        )
    }

    CompositionLocalProvider(LocalDensity provides resolvedDensity) {
        content(Modifier.colorBlindFilter(accessibilityState.colorBlindMode))
    }
}