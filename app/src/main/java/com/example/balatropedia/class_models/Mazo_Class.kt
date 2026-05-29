package com.example.balatropedia.class_models

data class MazoModel(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val imagen_url: String = "",
    val puntuacion_usuarios: Double = 0.0,
    val consumiblesIncluidos: List<Map<String, String>> = emptyList(),
    val vouchersIncluidos: List<Map<String, String>> = emptyList()
)