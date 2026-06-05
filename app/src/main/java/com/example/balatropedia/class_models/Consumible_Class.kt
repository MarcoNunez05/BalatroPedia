package com.example.balatropedia.class_models

data class ConsumibleModel(
    val id: String = "",
    val nombre: String = "",
    val efecto: String = "",
    val tipo: String = "",
    val imagen_url: String = "",
    val puntuacion_usuarios: Double = 0.0,
    val boosterPack: List<Map<String, String>> = emptyList(),
)