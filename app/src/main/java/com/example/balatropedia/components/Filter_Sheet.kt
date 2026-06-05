// Última modificación: 28/05/2026
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E222B),
        scrimColor = Color.Black.copy(alpha = 0.5f)
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
                        fontSize = 18.sp
                    )

                    if (esSeleccionado) {
                        Text(text = "✓", color = Color(0xFF00BFFF), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

