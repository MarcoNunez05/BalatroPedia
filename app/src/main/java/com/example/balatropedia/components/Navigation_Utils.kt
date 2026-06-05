package com.example.balatropedia.components

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

// Extensión de NavController para que no hayan problemas en las transiciones entre pantallas
fun NavController.navigateSafe(route: String, builder: NavOptionsBuilder.() -> Unit = {}) {
    if (this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        this.navigate(route)
    }
}