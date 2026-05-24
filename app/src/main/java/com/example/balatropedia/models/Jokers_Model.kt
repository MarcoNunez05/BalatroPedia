package com.example.balatropedia.models

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.balatropedia.class_models.JokerModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlin.math.round

class JokersViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _jokers = mutableStateOf<List<JokerModel>>(emptyList())
    val jokers: State<List<JokerModel>> = _jokers

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _puntuacionActual = mutableStateOf(0.0)
    val puntuacionActual: State<Double> = _puntuacionActual

    private var _listenerPuntuacion: ListenerRegistration? = null

    init {
        _Obtener_Jokers_De_Firebase()
    }

    fun _Obtener_Jokers_De_Firebase() {
        _isLoading.value = true

        db.collection("jokers")
            .get()
            .addOnSuccessListener { resultado ->
                val listaTemporal = resultado.map { documento ->
                    documento.toObject(JokerModel::class.java)
                }
                _jokers.value = listaTemporal
                _isLoading.value = false
            }
            .addOnFailureListener { exception ->
                _isLoading.value = false
            }
    }

    fun _Obtener_Joker_Por_ID(idBusqueda: String): JokerModel? {
        return _jokers.value.find { it.id.equals(idBusqueda, ignoreCase = true) }
    }

    fun _Iniciar_Listener_Puntuacion(nombreJoker: String, puntuacionBase: Double) {
        _puntuacionActual.value = puntuacionBase
        val jokerDocumentId = "joker_${nombreJoker.lowercase().trim().replace(" ", "_")}"

        _Detener_Listener_Puntuacion()

        val docRef = db.collection("jokers").document(jokerDocumentId)

        _listenerPuntuacion = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("ViewModel - Error en escucha: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val nuevaPuntuacion = snapshot.getDouble("puntuacion")
                if (nuevaPuntuacion != null) {
                    _puntuacionActual.value = nuevaPuntuacion
                }
            }
        }
    }

    fun _Detener_Listener_Puntuacion() {
        _listenerPuntuacion?.remove()
        _listenerPuntuacion = null
    }

    fun _Guardar_Recalcular(
        nombreJoker: String,
        userId: String,
        calificacion: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val jokerDocumentId = "joker_${nombreJoker.lowercase().trim().replace(" ", "_")}"
        val jokerDocRef = db.collection("jokers").document(jokerDocumentId)
        val votosCollectionRef = jokerDocRef.collection("votos")

        val votoData = hashMapOf(
            "usuarioId" to userId,
            "jokerNombre" to nombreJoker,
            "puntuacion" to calificacion
        )

        votosCollectionRef.document(userId).set(votoData)
            .addOnSuccessListener {
                votosCollectionRef.get()
                    .addOnSuccessListener { snapshot ->
                        if (snapshot != null && !snapshot.isEmpty) {
                            var sumaPuntuaciones = 0.0
                            val totalVotos = snapshot.size()

                            for (document in snapshot.documents) {
                                sumaPuntuaciones += (document.getDouble("puntuacion") ?: 0.0)
                            }

                            val promedioRedondeado = round((sumaPuntuaciones / totalVotos) * 10) / 10.0

                            jokerDocRef.update("puntuacion", promedioRedondeado)
                                .addOnSuccessListener { onSuccess() }
                                .addOnFailureListener { e -> onError("Error al actualizar la media: ${e.message}") }
                        }
                    }
                    .addOnFailureListener { e -> onError("Error al leer los votos: ${e.message}") }
            }
            .addOnFailureListener { e -> onError("Error al guardar tu voto: ${e.message}") }
    }
}