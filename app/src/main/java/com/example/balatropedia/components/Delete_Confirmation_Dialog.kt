// Última modificación: 28/05/2026
// Autor: Marco Núñez

package com.example.balatropedia.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.balatropedia.ui.theme._BALATRO_FONT

@Composable
// Dialog para confirmar borrado de documento
fun _Delete_Confirmation_Dialog(
    titulo: String,
    mensaje: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF252932),
        titleContentColor = Color.White,
        textContentColor = Color.LightGray,
        title = {
            Text(text = titulo, fontFamily = _BALATRO_FONT)
        },
        text = {
            Text(
                text = mensaje,
                fontSize = 20.sp,
                fontFamily = _BALATRO_FONT
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Eliminar",
                    color = Color(0xFFC33C3C),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    fontFamily = _BALATRO_FONT
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = _BALATRO_FONT
                )
            }
        }
    )
}