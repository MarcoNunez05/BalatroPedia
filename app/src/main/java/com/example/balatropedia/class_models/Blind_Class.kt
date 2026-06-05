package com.example.balatropedia.class_models

data class BlindModel(
    val id: String = "",
    val nombre: String = "",
    val modificador: String = "",
    val imagen_url: String = "",
    val recompensa: String = "",
    val puntuacion_usuarios: Double = 0.0,
    val jokersRecomendados: List<Map<String, String>> = emptyList(),
)