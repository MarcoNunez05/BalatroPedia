package com.example.balatropedia.models

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balatropedia.class_models.JokerModel
import com.example.balatropedia.class_models.JokerSelectorModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    private val _jokersSelectorList = MutableStateFlow<List<JokerSelectorModel>>(emptyList())
    val jokersSelectorList: StateFlow<List<JokerSelectorModel>> = _jokersSelectorList.asStateFlow()

    private val _consumiblesSelectorList = MutableStateFlow<List<JokerSelectorModel>>(emptyList())
    val consumiblesSelectorList: StateFlow<List<JokerSelectorModel>> = _consumiblesSelectorList.asStateFlow()

    init {
        _Obtener_Jokers_De_Firebase()
        _Cargar_Listas_Selector()
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

    private fun _Cargar_Listas_Selector() {
        viewModelScope.launch {
            try {
                val jokersSnapshot = db.collection("jokers").get().await()
                val jokersList = jokersSnapshot.documents.mapNotNull { doc ->
                    val nombre = doc.getString("nombre") ?: return@mapNotNull null
                    val imagenUrl = doc.getString("imagen_url") ?: ""

                    JokerSelectorModel(
                        id = doc.id,
                        nombre = nombre,
                        imagenUrl = imagenUrl
                    )
                }
                _jokersSelectorList.value = jokersList

                val consumiblesSnapshot = db.collection("consumibles").get().await()
                val consumiblesList = consumiblesSnapshot.documents.mapNotNull { doc ->
                    val nombre = doc.getString("nombre") ?: return@mapNotNull null
                    val imagenUrl = doc.getString("imagen_url") ?: ""

                    JokerSelectorModel(
                        id = doc.id,
                        nombre = nombre,
                        imagenUrl = imagenUrl
                    )
                }
                _consumiblesSelectorList.value = consumiblesList

            } catch (e: Exception) {
                println("ViewModel - Error al cargar listas del selector: ${e.message}")
            }
        }
    }

    fun _Obtener_Joker_Por_ID(idBusqueda: String): JokerModel? {
        return _jokers.value.find { it.id.equals(idBusqueda, ignoreCase = true) }
    }

    // Puntuación
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

    // Opciones de Admin
    fun _Añadir_Nuevo_Joker(
        nombre: String,
        descripcion: String,
        rareza: String,
        jokersSinergia: List<JokerSelectorModel>,
        consumiblesSinergia: List<JokerSelectorModel>,
        imagenUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val documentId = "joker_${nombre.lowercase().replace(" ", "_")}"

        val sinergiasJokersMap = jokersSinergia.map {
            mapOf("id" to it.id, "nombre" to it.nombre)
        }

        val sinergiasConsumiblesMap = consumiblesSinergia.map {
            mapOf("id" to it.id, "nombre" to it.nombre)
        }

        val nuevoJoker = hashMapOf(
            "nombre" to nombre,
            "descripcion" to descripcion,
            "rareza" to rareza,
            "imagenUrl" to imagenUrl,
            "puntuacion" to 0.0,
            "sinergiasJokers" to sinergiasJokersMap,
            "sinergiasConsumibles" to sinergiasConsumiblesMap
        )

        db.collection("jokers").document(documentId)
            .set(nuevoJoker)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onError(exception.message ?: "Error desconocido al guardar")
            }
    }
}