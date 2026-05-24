package com.example.balatropedia.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.R
import coil.compose.AsyncImage


@Composable
fun _Balatro_Row_Item(
    nombre: String,
    imageUrl: String,
    backgroundColor: Color,
    isAdmin: Boolean,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .background(backgroundColor)
            .border(2.dp, Color.Black)
            .clickable { onItemClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = nombre,
            modifier = Modifier
                .size(50.dp, 72.dp)
                .padding(end = 12.dp),
            placeholder = painterResource(R.drawable.main_joker),
            error = painterResource(R.drawable.main_joker)
        )

        Text(
            text = nombre,
            color = Color.White,
            fontSize = 28.sp,
            modifier = Modifier.weight(1f)
        )

        if (isAdmin) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFFFFD700))
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFFF4D4D))
                }
            }
        }
    }
}