package com.example.balatropedia.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.balatropedia.ui.theme.COLOR_HEADER
import com.example.balatropedia.R
import com.google.firebase.auth.FirebaseAuth

@Composable
// Header que se muestra por toda la página para que el usuario regrese a la pantalla anterior o revise su perfil
fun _Balatropedia_Header(
    comeBack: Boolean,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val auth = FirebaseAuth.getInstance()
    val isLoggedIn = auth.currentUser != null

    val profileIconColor = if (isLoggedIn) Color(0xFFFFD700) else Color.White

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(COLOR_HEADER)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Si se puede volver a una pantalla anterior, se muestra el botón de "volver"
            if (comeBack) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver atrás",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }

            Image(
                painter = painterResource(id = R.drawable.balatropedialogo),
                contentDescription = "Balatropedia Logo",
            )
        }

        IconButton(onClick = onProfileClick) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = "Perfil de usuario",
                tint = profileIconColor,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}