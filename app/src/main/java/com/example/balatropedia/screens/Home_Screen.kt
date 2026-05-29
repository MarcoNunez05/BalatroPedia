package com.example.balatropedia.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.R
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.components._Category_Button
import com.example.balatropedia.ui.theme.COLOR_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_BLINDS_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_BLINDS_TEXT
import com.example.balatropedia.ui.theme.COLOR_BOOSTER_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_BOOSTER_TEXT
import com.example.balatropedia.ui.theme.COLOR_CHALLENGES_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_CHALLENGES_TEXT
import com.example.balatropedia.ui.theme.COLOR_CONSUMIBLES_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_CONSUMIBLES_TEXT
import com.example.balatropedia.ui.theme.COLOR_JOKER_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_JOKER_TEXT
import com.example.balatropedia.ui.theme.COLOR_MANOS_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_MANOS_TEXT
import com.example.balatropedia.ui.theme.COLOR_MAZOS_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_MAZOS_TEXT
import com.example.balatropedia.ui.theme.COLOR_VOUCHERS_BACKGROUND
import com.example.balatropedia.ui.theme.COLOR_VOUCHERS_TEXT
import com.example.balatropedia.ui.theme._BALATRO_FONT

data class CategoryData(
    val TEXT: String,
    @DrawableRes val IMAGE_ID: Int,
    val BACKGROUND_COLOR: Color,
    val TEXT_COLOR: Color,
    val ID: String
)

@Composable
// Pantalla principal
fun _Home_Screen(
    isAdmin: Boolean,
    onProfileClick: () -> Unit,
    onCategoryClick: (String) -> Unit
)
{

    val CATEGORIES = remember {
        listOf<CategoryData>(
            CategoryData("Jokers", R.drawable.main_joker, COLOR_JOKER_BACKGROUND,
                COLOR_JOKER_TEXT, "jokers"),
            CategoryData("Mazos", R.drawable.main_mazos, COLOR_MAZOS_BACKGROUND, COLOR_MAZOS_TEXT, "mazos"),
            CategoryData("Vouchers", R.drawable.main_voucher,
                COLOR_VOUCHERS_BACKGROUND, COLOR_VOUCHERS_TEXT, "vouchers"),
            CategoryData("Consumibles", R.drawable.main_consumibles,
                COLOR_CONSUMIBLES_BACKGROUND, COLOR_CONSUMIBLES_TEXT, "consumibles"),
            CategoryData("Manos", R.drawable.main_manos, COLOR_MANOS_BACKGROUND, COLOR_MANOS_TEXT, "manos"),
            CategoryData("Booster Packs", R.drawable.main_booster,
                COLOR_BOOSTER_BACKGROUND, COLOR_BOOSTER_TEXT, "booster"),
            CategoryData("Blinds", R.drawable.main_blind,
                COLOR_BLINDS_BACKGROUND, COLOR_BLINDS_TEXT, "booster"),
            CategoryData("Challenges", R.drawable.main_challenge,
                COLOR_CHALLENGES_BACKGROUND, COLOR_CHALLENGES_TEXT, "booster")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(COLOR_BACKGROUND)
            .safeDrawingPadding()
    ) {

        _Balatropedia_Header(
            comeBack = false,
            onBackClick = { },
            onProfileClick = onProfileClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Text(
                text = "Explore las categorías",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp).fillMaxSize(),
                fontFamily = _BALATRO_FONT,
                textAlign = TextAlign.Center
            )


            CATEGORIES.forEach { category ->
                _Category_Button(
                    text = category.TEXT,
                    imageResId = category.IMAGE_ID,
                    backgroundColor = category.BACKGROUND_COLOR,
                    textColor = category.TEXT_COLOR,
                    onClick = {
                        onCategoryClick(category.TEXT.lowercase())
                    }
                )
            }
        }
    }
}