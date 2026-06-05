// Última modificación: 31/05/2026
// Autor: Marco Núñez

package com.example.balatropedia.class_models

data class ChallengeModel(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val requisitos: String = "",
    val puntuacion_usuarios: Double = 0.0,
    val jokersIncluidos: List<Map<String, String>> = emptyList(),
    val consumiblesIncluidos: List<Map<String, String>> = emptyList(),
    val vouchersIncluidos: List<Map<String, String>> = emptyList(),
    val jokersProhibidos: List<Map<String, String>> = emptyList(),
    val consumiblesProhibidos: List<Map<String, String>> = emptyList(),
    val vouchersProhibidos: List<Map<String, String>> = emptyList(),
)