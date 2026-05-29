package com.example.balatropedia.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Barra de búsqueda de documentos con un botón para filtrar
fun _Search_Filter_Bar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    placeholderText: String = "Buscar...",
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .border(1.dp, Color(0xFF3F4552), RoundedCornerShape(6.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF252932),
                unfocusedContainerColor = Color(0xFF252932),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(6.dp),
            placeholder = { Text(placeholderText, color = Color.Gray, fontSize = 16.sp) },
            singleLine = true
        )

        Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = "Filtrar",
            tint = Color.White,
            modifier = Modifier
                .size(30.dp)
                .clickable { onFilterClick() }
        )
    }
}

