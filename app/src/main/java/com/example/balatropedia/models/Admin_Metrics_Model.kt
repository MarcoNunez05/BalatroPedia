package com.example.balatropedia.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AdminReport(
    val ultima_actualizacion: Long = System.currentTimeMillis(),
    val total_usuarios: Int = 0,
    val total_votos: Int = 0,
    val promedio_global: Float = 0f,
    val top_paises: Map<String, Int> = emptyMap(),
    val edad_distribucion: Map<String, Int> = emptyMap(),
    val actividad_por_categoria: Map<String, Int> = emptyMap()
)

class AdminMetricsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _reporte = MutableStateFlow<AdminReport?>(null)
    val reporte: StateFlow<AdminReport?> = _reporte.asStateFlow()

    fun cargarEstadisticas() {
        db.collection("admin_stats").document("resumen_global")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.toObject(AdminReport::class.java)
                    _reporte.value = data
                }
            }
    }

    fun recalcularEstadisticas(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val usersSnapshot = db.collection("users").get().await()

                val totalUsuarios = usersSnapshot.size()

                val conteoPaises = mutableMapOf<String, Int>()
                val conteoEdades = mutableMapOf<String, Int>()

                for (doc in usersSnapshot.documents) {
                    val pais = doc.getString("pais") ?: "Desconocido"
                    conteoPaises[pais] = conteoPaises.getOrDefault(pais, 0) + 1

                    val edadString = doc.getString("edad")
                    val edad = edadString?.toIntOrNull()

                    if (edad != null) {
                        val rango = when {
                            edad < 18 -> "13-17"
                            edad in 18..24 -> "18-24"
                            edad in 25..34 -> "25-34"
                            else -> "35+"
                        }
                        conteoEdades[rango] = conteoEdades.getOrDefault(rango, 0) + 1
                    }
                }

                val votosSnapshot = db.collectionGroup("votos").get().await()
                val totalVotos = votosSnapshot.size()

                var sumaTotalRatings = 0.0
                val conteoCategorias = mutableMapOf<String, Int>()

                for (votoDoc in votosSnapshot.documents) {
                    val rating = votoDoc.getDouble("puntuacion") ?: 0.0
                    sumaTotalRatings += rating

                    val parentRef = votoDoc.reference.parent.parent
                    if (parentRef != null) {
                        val categoria = parentRef.parent.id
                        conteoCategorias[categoria] = conteoCategorias.getOrDefault(categoria, 0) + 1
                    }
                }

                val promedioGlobal = if (totalVotos > 0) (sumaTotalRatings / totalVotos).toFloat() else 0f

                val reporte = AdminReport(
                    ultima_actualizacion = System.currentTimeMillis(),
                    total_usuarios = totalUsuarios,
                    total_votos = totalVotos,
                    promedio_global = promedioGlobal,
                    top_paises = conteoPaises,
                    edad_distribucion = conteoEdades,
                    actividad_por_categoria = conteoCategorias
                )

                db.collection("admin_stats")
                    .document("resumen_global")
                    .set(reporte)
                    .await()

                onSuccess()

            } catch (e: Exception) {
                e.printStackTrace()
                onError(e.message ?: "Error desconocido al calcular métricas")
            }
        }
    }
}

