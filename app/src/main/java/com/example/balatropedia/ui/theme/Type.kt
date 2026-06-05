// Última modificación: 20/05/2026
// Autor: Marco Núñez

package com.example.balatropedia.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.balatropedia.R

val _BALATRO_FONT = FontFamily(
    Font(R.font.balatro_font, FontWeight.Normal),
    Font(R.font.balatro_font, FontWeight.Bold)
)

val _TYPOGRAPHY = Typography(
    titleLarge = TextStyle(
        fontFamily = _BALATRO_FONT,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = _BALATRO_FONT,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    )
)