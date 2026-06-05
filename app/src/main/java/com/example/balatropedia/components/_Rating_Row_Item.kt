package com.example.balatropedia.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.balatropedia.R
import com.example.balatropedia.screens.UserRatingItem
import com.example.balatropedia.ui.theme.COLOR_BLINDS_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_BOOSTER_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_CHALLENGES_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_CONSUMIBLES_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_JOKER_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_MANOS_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_MAZOS_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_VOUCHERS_BACKGROUND
import kotlin.text.ifEmpty

@Composable
// Caja donde se guardan las puntuaciones del usuario
fun _Rating_Row_Item(
    ratingItem: UserRatingItem,
    onClick: () -> Unit
) {
    val backgroundColor = when (ratingItem.category) {
        "jokers" -> COLOR_JOKER_BACKGROUND
        "mazos" -> COLOR_MAZOS_BACKGROUND
        "vouchers" -> COLOR_VOUCHERS_BACKGROUND
        "consumibles" -> COLOR_CONSUMIBLES_BACKGROUND
        "manos" -> COLOR_MANOS_BACKGROUND
        "boosterPacks" -> COLOR_BOOSTER_BACKGROUND
        "blinds" -> COLOR_BLINDS_BACKGROUND
        "challenges" -> COLOR_CHALLENGES_BACKGROUND
        else -> Color(0xFF4A4A4A)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .background(backgroundColor)
            .border(2.dp, Color.Black)
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ratingItem.imageUrl.ifEmpty { null },
            contentDescription = ratingItem.itemName,
            modifier = Modifier
                .size(50.dp, 72.dp)
                .padding(end = 12.dp)
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = ratingItem.itemName,
                color = Color.White,
                fontSize = 28.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row {
                for (i in 1..5) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Estrella",
                        tint = if (i <= ratingItem.score) Color(0xFFFFD700) else Color(0xFF333333),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}