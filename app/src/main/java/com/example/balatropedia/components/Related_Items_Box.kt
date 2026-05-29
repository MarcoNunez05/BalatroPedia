package com.example.balatropedia.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.class_models.ItemSelectorModel
import com.example.balatropedia.ui.theme._BALATRO_FONT

@OptIn(ExperimentalLayoutApi::class)
@Composable
// Diseño de la caja donde se guardan los documentos seleccionados de _Item_Selector
fun _Related_Items_Box(
    label: String,
    instructions: String,
    buttonText: String,
    selectedItems: SnapshotStateList<ItemSelectorModel>,
    onAddClick: () -> Unit,
    onRemoveItem: (ItemSelectorModel) -> Unit
) {
    Text(text = label, color = Color.White, fontSize = 24.sp, fontFamily = _BALATRO_FONT, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(4.dp))
    Text(text = instructions, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedCard(
        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF3F4552), RoundedCornerShape(6.dp)),
        colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFF1E222B)),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            if (selectedItems.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedItems.forEach { item ->
                        InputChip(
                            selected = true,
                            onClick = {},
                            label = { Text(text = item.nombre, fontSize = 16.sp) },
                            trailingIcon = {
                                Icon(Icons.Default.Close, contentDescription = "Borrar", modifier = Modifier.clickable { onRemoveItem(item) })
                            },
                            colors = InputChipDefaults.inputChipColors(
                                selectedContainerColor = Color(0xFFC5A53F),
                                selectedLabelColor = Color(0xFF1E222B),
                                selectedTrailingIconColor = Color(0xFF1E222B)
                            )
                        )
                    }
                }
            }

            Button(
                onClick = onAddClick,
                modifier = Modifier.fillMaxWidth().height(45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F4552)),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(text = buttonText, color = Color.White, fontSize = 18.sp, fontFamily = _BALATRO_FONT)
            }
        }
    }
}