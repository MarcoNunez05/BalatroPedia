// Última modificación: 28/05/2026
// Autor: Marco Núñez

package com.example.balatropedia.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.balatropedia.R
import com.example.balatropedia.class_models.ItemSelectorModel
import com.example.balatropedia.ui.theme.COLOR_BACKGROUND
import com.example.balatropedia.ui.theme._BALATRO_FONT
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Selector de documentos para agregarlos a un documento que se está creando
fun _Item_Selector(
    titulo: String,
    itemsDisponibles: List<ItemSelectorModel>,
    onDismiss: () -> Unit,
    onItemSelected: (ItemSelectorModel) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }

    val itemsFiltrados by remember(searchQuery, itemsDisponibles) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                itemsDisponibles
            } else {
                itemsDisponibles.filter {
                    it.nombre.contains(searchQuery, ignoreCase = true)
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = COLOR_BACKGROUND,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = titulo,
                color = Color.White,
                fontSize = 24.sp,
                fontFamily = _BALATRO_FONT,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF3F4552), RoundedCornerShape(6.dp)),
                placeholder = { Text("Buscar por nombre...", color = Color.Gray) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.Gray)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF14161B),
                    unfocusedContainerColor = Color(0xFF14161B),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(6.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxHeight(0.6f)
            ) {
                if (itemsFiltrados.isEmpty()) {
                    item {
                        Text(
                            text = "No se encontraron resultados",
                            color = Color.Gray,
                            modifier = Modifier
                                .padding(32.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    items(itemsFiltrados) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    coroutineScope.launch {
                                        sheetState.hide()
                                        onItemSelected(item)
                                    }
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = item.imagenUrl,
                                contentDescription = null,
                                placeholder = painterResource(R.drawable.main_joker),
                                error = painterResource(R.drawable.main_joker),
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF3F4552)),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = item.nombre,
                                color = Color.White,
                                fontSize = 18.sp,
                                modifier = Modifier.weight(1f)
                            )

                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Añadir",
                                tint = Color(0xFFC5A53F)
                            )
                        }
                        HorizontalDivider(color = Color(0xFF2C323F))
                    }
                }
            }
        }
    }
}