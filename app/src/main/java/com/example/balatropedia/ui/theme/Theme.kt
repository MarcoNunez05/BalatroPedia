package com.example.balatropedia.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val COLORS_BALATROPEDIA = darkColorScheme(
    primary = COLOR_HEADER,
    background = COLOR_BACKGROUND,
    surface = COLOR_BACKGROUND
)

@Composable
fun BalatroPediaTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = COLORS_BALATROPEDIA,
        typography = _TYPOGRAPHY,
        content = content
    )
}