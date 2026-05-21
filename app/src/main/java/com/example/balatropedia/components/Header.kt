package com.example.balatropedia.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.R
import com.example.balatropedia.ui.theme.COLOR_HEADER


@Composable
fun _Balatropedia_Header(
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
)

{
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(COLOR_HEADER)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {

        Image(
            painter = painterResource(id = R.drawable.balatropedialogo),
            contentDescription = "Balatropedia Logo"
        )

        IconButton(onClick = onProfileClick) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = "Perfil de usuario",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun _Preview()
{
    _Balatropedia_Header(onProfileClick = {})
}