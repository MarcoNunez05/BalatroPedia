package com.example.balatropedia.class_models

data class ManoModel(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val puntuacion_base: Int = 0,
    val multiplicador_base: Int = 0,
    val puntuacion_usuarios: Double = 0.0,
    val jokersAfectados: List<Map<String, String>> = emptyList(),
    val cartaPlaneta: List<Map<String, String>> = emptyList()
)