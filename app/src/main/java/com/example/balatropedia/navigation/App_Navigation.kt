package com.example.balatropedia.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.balatropedia.models.JokersViewModel
import com.example.balatropedia.screens.JokerDetail
import com.example.balatropedia.screens.JokerDetailScreen
import com.example.balatropedia.screens._Home_Screen
import com.example.balatropedia.screens._Jokers_Screen
import com.example.balatropedia.screens._Login_Screen
import com.example.balatropedia.screens._Profile_Screen
import com.example.balatropedia.screens._Register_Screen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(isAdmin: Boolean, modifier: Modifier){

    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    val vNavegarAlPerfil: () -> Unit = {
        if (auth.currentUser != null) {
            navController.navigate("profile_screen")
        } else {
            navController.navigate("login")
        }
    }

    // 1. Instanciamos el ViewModel aquí para compartirlo entre pantallas
    val jokersViewModel: JokersViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "Home"
    ) {
        composable("Home") {
            _Home_Screen(
                isAdmin = isAdmin,
                onProfileClick = vNavegarAlPerfil,
                onCategoryClick = { categoria ->
                    when (categoria) {
                        "jokers" -> navController.navigate("jokers_screen")
                        "vouchers" -> navController.navigate("vouchers_screen")
                    }
                }
            )
        }

        composable(route = "jokers_screen") {
            _Jokers_Screen(
                isAdmin = isAdmin,
                onProfileClick = vNavegarAlPerfil,
                onNavigateBack = { navController.popBackStack() },
                onAddJokerClick = { navController.navigate("add_joker") },
                onJokerClick = { jokerid ->
                    navController.navigate("joker_detail/$jokerid")
                },
                viewModel = jokersViewModel
            )
        }

        composable(route = "joker_detail/{jokerid}") { backStackEntry ->

            val jokerId = backStackEntry.arguments?.getString("jokerid") ?: ""

            val joker = jokersViewModel._Obtener_Joker_Por_ID(jokerId)

            if (joker != null) {
                val detalleJoker = remember(joker) {
                    JokerDetail(
                        nombre = joker.nombre,
                        descripcion = joker.descripcion ?: "Añade un +4 en el multiplicador a todas las manos jugadas.",
                        rareza = joker.rareza ?: "Común",
                        imagenUrl = joker.imagen_url,
                        puntuacion = joker.puntuacion_usuarios ?: 0.0,
                        sinergiasConsumibles = emptyList(),
                        sinergiasJokers = listOf(

                        )
                    )
                }

                JokerDetailScreen(
                    joker = detalleJoker,
                    isAdmin = isAdmin,
                    onNavigateBack = { navController.popBackStack() },
                    onProfileClick = vNavegarAlPerfil
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Cargando detalles...", color = Color.White)
                }
            }
        }

        composable(route = "login") {
            _Login_Screen(
                onNavigateBack = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.navigate("Home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }

        composable(route = "register") {
            _Register_Screen(
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate("Home") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable(route = "profile_screen") {
            _Profile_Screen(
                onNavigateBack = { navController.popBackStack() },
                onLogoutSuccess = {
                    navController.navigate("Home") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }
    }
}