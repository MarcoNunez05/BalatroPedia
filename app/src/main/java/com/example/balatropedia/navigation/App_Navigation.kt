package com.example.balatropedia.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.balatropedia.models.JokerViewModel
import com.example.balatropedia.models.MazoViewModel
import com.example.balatropedia.models.VoucherViewModel
import com.example.balatropedia.screens._Joker_Detail_Screen
import com.example.balatropedia.screens._Home_Screen
import com.example.balatropedia.screens._Joker_Screen
import com.example.balatropedia.screens._Login_Screen
import com.example.balatropedia.screens._Profile_Screen
import com.example.balatropedia.screens._Register_Screen
import com.example.balatropedia.screens._Joker_Add_Screen
import com.example.balatropedia.screens._Joker_Edit_Screen
import com.example.balatropedia.screens._Mazo_Add_Screen
import com.example.balatropedia.screens._Mazo_Detail_Screen
import com.example.balatropedia.screens._Mazo_Edit_Screen
import com.example.balatropedia.screens._Mazos_Screen
import com.example.balatropedia.screens._Voucher_Add_Screen
import com.example.balatropedia.screens._Voucher_Detail_Screen
import com.example.balatropedia.screens._Voucher_Edit_Screen
import com.example.balatropedia.screens._Vouchers_Screen
import com.google.firebase.auth.FirebaseAuth

// Función que controla la navegación de la app
@Composable
fun _App_Navigation(isAdmin: Boolean, modifier: Modifier){

    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    val vNavegarAlPerfil: () -> Unit = {
        if (auth.currentUser != null) {
            navController.navigate("profile_screen")
        } else {
            navController.navigate("login")
        }
    }

    val jokerViewModel: JokerViewModel = viewModel()
    val mazoViewModel: MazoViewModel = viewModel()
    val voucherViewModel: VoucherViewModel = viewModel()

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
                        "mazos" -> navController.navigate("mazos_screen")
                    }
                }
            )
        }

        // USUARIO
        composable(route = "login") {
            _Login_Screen(
                onNavigateBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                },
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
                onNavigateBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                },
                onRegisterSuccess = {
                    navController.navigate("Home") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable(route = "profile_screen") {
            _Profile_Screen(
                onNavigateBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                },
                onLogoutSuccess = {
                    navController.navigate("Home") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }

        // JOKERS
        composable(route = "jokers_screen") {
            _Joker_Screen(
                isAdmin = isAdmin,
                onProfileClick = vNavegarAlPerfil,
                onNavigateBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                },
                onAddJokerClick = { navController.navigate("add_joker") },
                onJokerClick = { jokerid ->
                    navController.navigate("joker_detail/$jokerid")
                },
                onEditJokerClick = { id ->
                    navController.navigate("edit_joker/$id")
                },
                viewModel = jokerViewModel
            )
        }

        composable(route = "add_joker") {
            _Joker_Add_Screen(
                viewModel = jokerViewModel,
                onNavigateBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                },
                onProfileClick = vNavegarAlPerfil
            )
        }

        composable(
            route = "edit_joker/{jokerId}",
            arguments = listOf(navArgument("jokerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val jokerId = backStackEntry.arguments?.getString("jokerId") ?: ""

            _Joker_Edit_Screen(
                jokerId = jokerId,
                viewModel = jokerViewModel,
                onNavigateBack = { navController.popBackStack() },
                onProfileClick = vNavegarAlPerfil
            )
        }

        composable(route = "joker_detail/{jokerid}") { backStackEntry ->

            val jokerId = backStackEntry.arguments?.getString("jokerid") ?: ""

            val joker = jokerViewModel._Obtener_Joker_Por_ID(jokerId)

            if (joker != null) {
                _Joker_Detail_Screen(
                    joker = joker,
                    isAdmin = isAdmin,
                    viewModel = jokerViewModel,
                    onNavigateBack = {
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        }
                    },
                    onProfileClick = vNavegarAlPerfil,
                    onRelatedJokerClick = { idSinergia ->
                        navController.navigate("joker_detail/$idSinergia")
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFC33C3C))
                }
            }
        }

        // MAZOS
        composable(route = "mazos_screen") {
            _Mazos_Screen(
                isAdmin = isAdmin,
                onProfileClick = vNavegarAlPerfil,
                onNavigateBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                },
                onAddMazoClick = { navController.navigate("add_mazo") },
                onEditMazoClick = { id ->
                    navController.navigate("edit_mazo/$id")
                },
                onMazoClick = { mazoId ->
                    navController.navigate("mazo_detail/$mazoId")
                },
                viewModel = mazoViewModel
            )
        }

        composable(route = "add_mazo") {
            _Mazo_Add_Screen(
                viewModel = mazoViewModel,
                onNavigateBack = { navController.popBackStack() },
                onProfileClick = vNavegarAlPerfil
            )
        }

        composable(
            route = "edit_mazo/{mazoId}",
            arguments = listOf(navArgument("mazoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mazoId = backStackEntry.arguments?.getString("mazoId") ?: ""
            _Mazo_Edit_Screen(
                mazoId = mazoId,
                viewModel = mazoViewModel,
                onNavigateBack = { navController.popBackStack() },
                onProfileClick = vNavegarAlPerfil
            )
        }

        composable(
            route = "mazo_detail/{mazoId}",
            arguments = listOf(navArgument("mazoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mazoId = backStackEntry.arguments?.getString("mazoId") ?: ""

            val mazo = mazoViewModel._Obtener_Mazo_Por_ID(mazoId)

            if (mazo != null) {
                _Mazo_Detail_Screen(
                    mazo = mazo,
                    viewModel = mazoViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onProfileClick = vNavegarAlPerfil
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF00BFFF))
                }
            }
        }

        // VOUCHERS
        composable(route = "vouchers_screen") {
            _Vouchers_Screen(
                isAdmin = isAdmin,
                onProfileClick = vNavegarAlPerfil,
                onNavigateBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                },
                onAddVoucherClick = { navController.navigate("add_voucher") },
                onEditVoucherClick = { id ->
                    navController.navigate("edit_voucher/$id")
                },
                onVoucherClick = { voucherId ->
                    navController.navigate("voucher_detail/$voucherId")
                },
                viewModel = voucherViewModel
            )
        }

        composable(route = "add_voucher") {
            _Voucher_Add_Screen(
                viewModel = voucherViewModel,
                onNavigateBack = { navController.popBackStack() },
                onProfileClick = vNavegarAlPerfil
            )
        }

        composable(
            route = "edit_voucher/{voucherId}",
            arguments = listOf(navArgument("voucherId") { type = NavType.StringType })
        ) { backStackEntry ->
            val voucherId = backStackEntry.arguments?.getString("voucherId") ?: ""
            _Voucher_Edit_Screen(
                voucherId = voucherId,
                viewModel = voucherViewModel,
                onNavigateBack = { navController.popBackStack() },
                onProfileClick = vNavegarAlPerfil
            )
        }

        composable(
            route = "voucher_detail/{voucherId}",
            arguments = listOf(navArgument("voucherId") { type = NavType.StringType })
        ) { backStackEntry ->
            val voucherId = backStackEntry.arguments?.getString("voucherId") ?: ""

            val voucher = voucherViewModel._Obtener_Voucher_Por_ID(voucherId)

            if (voucher != null) {
                _Voucher_Detail_Screen(
                    voucher = voucher,
                    viewModel = voucherViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onProfileClick = vNavegarAlPerfil
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFE99D43))
                }
            }
        }
    }
}