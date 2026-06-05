// Última modificación: 28/05/2026
// Autor: Marco Núñez

package com.example.balatropedia.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.ui.theme._BALATRO_FONT

@Composable
// Caja donde se muestran los documentos que se relacionan con otro
fun _Related_Section(
    titulo: String,
    sinergias: List<Map<String, String>>?,
    itemBackgroundColor: Color,
    onItemClick: (String) -> Unit
) {
    if (!sinergias.isNullOrEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = titulo,
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = _BALATRO_FONT,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sinergias.forEach { item ->
                    val id = item["id"] ?: ""
                    val nombre = item["nombre"] ?: "Desconocido"
                    val imagenUrl = item["imagenUrl"] ?: ""

                    _Balatro_Row_Item(
                        nombre = nombre,
                        imageUrl = imagenUrl,
                        backgroundColor = itemBackgroundColor,
                        isAdmin = false,
                        onItemClick = { if (id.isNotEmpty()) onItemClick(id) }
                    )
                }
            }
        }
    }
}