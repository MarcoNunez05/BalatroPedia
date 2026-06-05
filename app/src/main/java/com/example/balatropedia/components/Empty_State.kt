// Última modificación: 01/06/2026
// Autor: Marco Núñez

package com.example.balatropedia.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.ui.theme._BALATRO_FONT

@Composable
// Texto a mostrar cuando no se encuentren documentos
fun _Empty_State(
    mensaje: String,
    subtitulo: String = "Aún no hay información en la base de datos."
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp, horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = mensaje,
            color = Color.White,
            fontSize = 24.sp,
            fontFamily = _BALATRO_FONT,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = subtitulo,
            color = Color.Gray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}