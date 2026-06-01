package com.example.balatropedia.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balatropedia.components._Balatropedia_Header
import com.example.balatropedia.components._Empty_State
import com.example.balatropedia.components._Filter_Bottom_Sheet
import com.example.balatropedia.components._Rating_Row_Item
import com.example.balatropedia.components._Search_Filter_Bar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class UserRatingItem(
    val id: String = "",
    val itemName: String = "Desconocido",
    val imageUrl: String = "",
    val score: Int = 0,
    val category: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun _User_Ratings_Screen(
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit,
    onItemClick: (categoria: String, id: String) -> Unit
) {
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }

    var vRatingsList by remember { mutableStateOf<List<UserRatingItem>>(emptyList()) }
    var vIsLoading by remember { mutableStateOf(true) }

    var vBusqueda by remember { mutableStateOf("") }
    var vShowFilterSheet by remember { mutableStateOf(false) }
    var vFiltroEstrellas by remember { mutableStateOf("Todas") }

    val listaFiltrada by remember(vBusqueda, vFiltroEstrellas, vRatingsList) {
        derivedStateOf {
            vRatingsList.filter { rating ->
                val cumpleBusqueda = rating.itemName.contains(vBusqueda, ignoreCase = true)

                val cumpleFiltro = when (vFiltroEstrellas) {
                    "Todas" -> true
                    "5 Estrellas" -> rating.score == 5
                    "4 Estrellas" -> rating.score == 4
                    "3 Estrellas" -> rating.score == 3
                    "2 Estrellas" -> rating.score == 2
                    "1 Estrella" -> rating.score == 1
                    else -> true
                }

                cumpleBusqueda && cumpleFiltro
            }
        }
    }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            try {
                val votosSnapshot = db.collectionGroup("votos")
                    .whereEqualTo("usuarioId", uid)
                    .get()
                    .await()

                val ratings = votosSnapshot.documents.mapNotNull { voteDoc ->
                    val parentRef = voteDoc.reference.parent.parent

                    if (parentRef != null) {
                        val parentDoc = parentRef.get().await()

                        if (!parentDoc.exists()) {
                            return@mapNotNull null
                        }

                        val categoryName = parentRef.parent.id

                        val nombre = voteDoc.getString("jokerNombre")
                            ?: parentDoc.getString("nombre")
                            ?: parentDoc.getString("name")
                            ?: "Sin nombre"

                        val score = voteDoc.getLong("puntuacion")?.toInt() ?: 0

                        UserRatingItem(
                            id = parentDoc.id,
                            itemName = nombre,
                            imageUrl = parentDoc.getString("imagen_url") ?: "",
                            score = score,
                            category = categoryName
                        )
                    } else {
                        null
                    }
                }
                vRatingsList = ratings
                vIsLoading = false
            } catch (e: Exception) {
                vIsLoading = false
                e.printStackTrace()
            }
        } else {
            vIsLoading = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().safeDrawingPadding(),
        topBar = {
            _Balatropedia_Header(
                comeBack = true,
                onBackClick = onNavigateBack,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E222B))
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (vIsLoading) {
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
                                text = "Tus puntuaciones",
                                color = Color.White,
                                fontSize = 30.sp
                            )
                        }
                    }

                    item {
                        _Search_Filter_Bar(
                            searchQuery = vBusqueda,
                            onSearchQueryChange = { vBusqueda = it },
                            placeholderText = "Buscar puntuación...",
                            onFilterClick = { vShowFilterSheet = true }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (listaFiltrada.isEmpty()) {
                        item {
                            _Empty_State(
                                mensaje = "No se encontraron puntuaciones",
                                subtitulo = if (vRatingsList.isEmpty())
                                    "Aún no has puntuado ninguna carta. ¡Ve a explorar la wiki!"
                                else
                                    "No hay puntuaciones que coincidan con tu búsqueda o filtros."
                            )
                        }
                    } else {
                        items(listaFiltrada) { rating ->
                            _Rating_Row_Item(
                                ratingItem = rating,
                                onClick = { onItemClick(rating.category, rating.id) }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (vShowFilterSheet) {
        _Filter_Bottom_Sheet(
            titulo = "Filtrar por Estrellas",
            opciones = listOf("Todas", "5 Estrellas", "4 Estrellas", "3 Estrellas", "2 Estrellas", "1 Estrella"),
            opcionSeleccionada = vFiltroEstrellas,
            onOptionSelected = { estrellasSeleccionadas ->
                vFiltroEstrellas = estrellasSeleccionadas
                vShowFilterSheet = false
            },
            onDismiss = { vShowFilterSheet = false }
        )
    }
}