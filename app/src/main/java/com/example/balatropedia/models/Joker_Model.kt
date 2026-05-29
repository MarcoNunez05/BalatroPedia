package com.example.balatropedia.models

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.balatropedia.class_models.JokerModel
import com.example.balatropedia.class_models.ItemSelectorModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.round

class JokerViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _jokers = mutableStateOf<List<JokerModel>>(emptyList())
    val jokers: State<List<JokerModel>> = _jokers

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _puntuacionActual = mutableStateOf(0.0)
    val puntuacionActual: State<Double> = _puntuacionActual

    private var vListenerPuntuacion: ListenerRegistration? = null
    private var vListenerJokers: ListenerRegistration? = null
    private var vListenerConsumibles: ListenerRegistration? = null

    private val _jokersSelectorList = MutableStateFlow<List<ItemSelectorModel>>(emptyList())
    val jokersSelectorList: StateFlow<List<ItemSelectorModel>> = _jokersSelectorList.asStateFlow()

    private val _consumiblesSelectorList = MutableStateFlow<List<ItemSelectorModel>>(emptyList())
    val consumiblesSelectorList: StateFlow<List<ItemSelectorModel>> = _consumiblesSelectorList.asStateFlow()

    init {
        _Obtener_Jokers_De_Firebase()
        _Cargar_Listas_Selector()
    }


    fun _Obtener_Jokers_De_Firebase() {
        _isLoading.value = true

        vListenerJokers?.remove()

        vListenerJokers = db.collection("jokers")
            .addSnapshotListener { snapshot, error ->
                _isLoading.value = false

                if (error != null) {
                    println("ViewModel - Error en escucha en tiempo real: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val listaTemporal = snapshot.documents.mapNotNull { documento ->
                        try {
                            documento.toObject(JokerModel::class.java)
                        } catch (e: Exception) {
                            println("Error al mapear documento ${documento.id}: ${e.message}")
                            null
                        }
                    }
                    _jokers.value = listaTemporal

                    _jokersSelectorList.value = listaTemporal.map { joker ->
                        ItemSelectorModel(
                            id = joker.id,
                            nombre = joker.nombre,
                            imagenUrl = joker.imagen_url
                        )
                    }
                }
            }
    }

    private fun _Cargar_Listas_Selector() {
        vListenerConsumibles?.remove()

        vListenerConsumibles = db.collection("consumibles")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("ViewModel - Error en escucha de consumibles: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val consumiblesList = snapshot.documents.mapNotNull { doc ->
                        val nombre = doc.getString("nombre") ?: return@mapNotNull null
                        val imagenUrl = doc.getString("imagen_url") ?: ""

                        ItemSelectorModel(
                            id = doc.id,
                            nombre = nombre,
                            imagenUrl = imagenUrl
                        )
                    }
                    _consumiblesSelectorList.value = consumiblesList
                }
            }
    }

    fun _Obtener_Joker_Por_ID(idBusqueda: String): JokerModel? {
        return _jokers.value.find { it.id.equals(idBusqueda, ignoreCase = true) }
    }

    // Puntuación
    fun _Iniciar_Listener_Puntuacion(jokerId: String, puntuacionBase: Double) {
        _puntuacionActual.value = puntuacionBase

        _Detener_Listener_Puntuacion()

        val docRef = db.collection("jokers").document(jokerId)

        vListenerPuntuacion = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("ViewModel - Error en escucha: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val nuevaPuntuacion = snapshot.getDouble("puntuacion_usuarios")
                if (nuevaPuntuacion != null) {
                    _puntuacionActual.value = nuevaPuntuacion
                }
            }
        }
    }

    fun _Detener_Listener_Puntuacion() {
        vListenerPuntuacion?.remove()
        vListenerPuntuacion = null
    }

    // Función para guardar el voto dado y recalcular en tiempo real
    fun _Guardar_Recalcular(
        jokerId: String,
        nombreJoker: String,
        userId: String,
        calificacion: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val jokerDocRef = db.collection("jokers").document(jokerId)
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
                            var vSumaPuntuaciones = 0.0
                            val totalVotos = snapshot.size()

                            for (document in snapshot.documents) {
                                vSumaPuntuaciones += (document.getDouble("puntuacion") ?: 0.0)
                            }

                            val promedioRedondeado = round((vSumaPuntuaciones / totalVotos) * 10) / 10.0

                            jokerDocRef.update("puntuacion_usuarios", promedioRedondeado)
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
        jokersSinergia: List<ItemSelectorModel>,
        consumiblesSinergia: List<ItemSelectorModel>,
        imagenUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val documentId = "joker_${nombre.lowercase().replace(" ", "_")}"

        val sinergiasJokersMap = jokersSinergia.map {
            mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl)
        }

        val sinergiasConsumiblesMap = consumiblesSinergia.map {
            mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl)
        }

        val nuevoJoker = hashMapOf(
            "id" to documentId,
            "nombre" to nombre,
            "descripcion" to descripcion,
            "rareza" to rareza,
            "imagen_url" to imagenUrl,
            "puntuacion_usuarios" to 0.0,
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

    fun _Actualizar_Joker(
        id: String,
        nombre: String,
        descripcion: String,
        rareza: String,
        jokersSinergia: List<ItemSelectorModel>,
        consumiblesSinergia: List<ItemSelectorModel>,
        imagenUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val sinergiasJokersMap = jokersSinergia.map {
            mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl)
        }

        val sinergiasConsumiblesMap = consumiblesSinergia.map {
            mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl)
        }

        val datosActualizados = hashMapOf(
            "nombre" to nombre,
            "descripcion" to descripcion,
            "rareza" to rareza,
            "imagen_url" to imagenUrl,
            "sinergiasJokers" to sinergiasJokersMap,
            "sinergiasConsumibles" to sinergiasConsumiblesMap
        )

        db.collection("jokers").document(id)
            .update(datosActualizados as Map<String, Any>)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                onError(exception.message ?: "Error al actualizar el Joker")
            }
    }

    fun _Eliminar_Joker(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("jokers").document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                onError(exception.message ?: "Error al eliminar el Joker")
            }
    }

    override fun onCleared() {
        super.onCleared()
        vListenerJokers?.remove()
        vListenerPuntuacion?.remove()
        vListenerConsumibles?.remove()
    }
}