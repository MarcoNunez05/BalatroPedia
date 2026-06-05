package com.example.balatropedia.class_models

data class JokerModel(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val rareza: String = "",
    val imagen_url: String = "",
    val puntuacion_usuarios: Double = 0.0,
    val sinergiasJokers: List<Map<String, String>> = emptyList(),
    val sinergiasConsumibles: List<Map<String, String>> = emptyList()
)