package com.example.balatropedia.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.ui.theme._BALATRO_FONT

@Composable
// Barra general donde el usuario puede escribir
fun _Balatro_Input(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    readOnly: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 24.sp,
            fontFamily = _BALATRO_FONT,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF3F4552), RoundedCornerShape(6.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF3F4552), unfocusedContainerColor = Color(0xFF3F4552),
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White, unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(6.dp),
            singleLine = singleLine,
            minLines = minLines,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            readOnly = readOnly
        )
    }
}

@Composable
// Selector donde el usuario puede elegir entre opciones pre-definidas
fun _Balatro_Selector(
    label: String,
    value: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    widthFraction: Float = 1f
) {
    var vExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth(widthFraction)) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 24.sp,
            fontFamily = _BALATRO_FONT,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF242832))
                    .border(1.dp, Color(0xFF3F4552), RoundedCornerShape(6.dp))
                    .clickable() { vExpanded = !vExpanded }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                val isDefault = value == "Selecciona un país" || value.isEmpty()
                Text(
                    text = value,
                    color = if (isDefault) Color.Gray else Color.White,
                    fontSize = 18.sp
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            DropdownMenu(
                expanded = vExpanded,
                onDismissRequest = { vExpanded = false },
                modifier = Modifier
                    .background(Color(0xFF2C323F))
                    .border(1.dp, Color(0xFF3F4552), RoundedCornerShape(4.dp))
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        },
                        onClick = {
                            onOptionSelected(option)
                            vExpanded = false
                        },
                        modifier = Modifier.background(Color(0xFF2C323F))
                    )
                }
            }
        }
    }
}