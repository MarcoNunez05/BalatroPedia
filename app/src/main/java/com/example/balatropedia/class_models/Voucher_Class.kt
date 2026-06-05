// Última modificación: 29/05/2026
// Autor: Marco Núñez

package com.example.balatropedia.class_models

data class VoucherModel(
    val id: String = "",
    val nombre: String = "",
    val efecto: String = "",
    val imagen_url: String = "",
    val requisitos: String = "",
    val puntuacion_usuarios: Double = 0.0,
)