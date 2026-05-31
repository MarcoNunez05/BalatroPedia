package com.example.balatropedia.class_models

data class BoosterPackModel(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val imagen_url: String = "",
    val costo_base: Int = 0,
    val cartas_disponibles: Int = 0,
    val cartas_elegibles: Int = 0,
    val puntuacion_usuarios: Double = 0.0,
)