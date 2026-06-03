package com.example.balatropedia.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
// Gráfico de las estrellas para mostrar como representante de la puntuación
fun _Rating_Stars(puntuacion: Double) {

    val estrellasActivas = puntuacion.toInt()

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
    ) {
        // For para pintar las estrellas dependiendo de la puntuación
        for (i in 1..5) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Estrella $i",

                tint = if (i <= estrellasActivas) Color(0xFFFFC107) else Color(0xFF3F4552),
                modifier = Modifier
                    .size(40.dp)
                    .padding(horizontal = 4.dp)
            )
        }
    }
}