package com.example.balatropedia.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.components._Balatro_Row_Item
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.models.JokersViewModel
import com.example.balatropedia.ui.theme.COLOR_JOKER_BACKGROUND

import androidx.compose.foundation.lazy.items

@Composable
fun _Jokers_Screen(
    isAdmin: Boolean,
    onProfileClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onAddJokerClick: () -> Unit,
    onJokerClick: (String) -> Unit,
    viewModel: JokersViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val listaJokers = viewModel.jokers.value
    val cargando = viewModel.isLoading.value

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        topBar = {
            _Balatropedia_Header(
                comeBack = true,
                onBackClick = onNavigateBack,
                onProfileClick = onProfileClick
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = onAddJokerClick,
                    containerColor = Color(0xFFC33C3C),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Joker")
                }
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E222B))
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (cargando) {
                CircularProgressIndicator(color = Color(0xFFC33C3C))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Jokers",
                                color = Color.White,
                                fontSize = 30.sp,
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    items(listaJokers) { joker ->
                        _Balatro_Row_Item(
                            nombre = joker.nombre,
                            imageUrl = joker.imagen_url,
                            backgroundColor = COLOR_JOKER_BACKGROUND,
                            isAdmin = isAdmin,
                            onEditClick = {},
                            onDeleteClick = {},
                            onItemClick = {onJokerClick(joker.id)}
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}