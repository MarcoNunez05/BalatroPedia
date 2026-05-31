package com.example.balatropedia.models

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.balatropedia.class_models.BlindModel
import com.example.balatropedia.class_models.ItemSelectorModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.round

class BlindViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _blinds = mutableStateOf<List<BlindModel>>(emptyList())
    val blinds: State<List<BlindModel>> = _blinds

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _puntuacionActual = mutableStateOf(0.0)
    val puntuacionActual: State<Double> = _puntuacionActual

    private var vListenerPuntuacion: ListenerRegistration? = null
    private var vListenerBlinds: ListenerRegistration? = null
    private var vListenerJokers: ListenerRegistration? = null

    private val _jokersSelectorList = MutableStateFlow<List<ItemSelectorModel>>(emptyList())
    val jokersSelectorList: StateFlow<List<ItemSelectorModel>> = _jokersSelectorList.asStateFlow()

    init {
        _Obtener_Blinds_De_Firebase()
        _Cargar_Listas_Selector()
    }

    fun _Obtener_Blinds_De_Firebase() {
        _isLoading.value = true
        vListenerBlinds?.remove()

        vListenerBlinds = db.collection("blinds")
            .addSnapshotListener { snapshot, error ->
                _isLoading.value = false

                if (error != null) {
                    println("BlindViewModel - Error en escucha en tiempo real: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _blinds.value = snapshot.documents.mapNotNull { documento ->
                        try {
                            documento.toObject(BlindModel::class.java)
                        } catch (e: Exception) {
                            println("Error al mapear documento ${documento.id}: ${e.message}")
                            null
                        }
                    }
                }
            }
    }

    private fun _Cargar_Listas_Selector() {
        vListenerJokers?.remove()
        vListenerJokers = db.collection("jokers")
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    _jokersSelectorList.value = snapshot.documents.mapNotNull { doc ->
                        val nombre = doc.getString("nombre") ?: return@mapNotNull null
                        ItemSelectorModel(
                            id = doc.id,
                            nombre = nombre,
                            imagenUrl = doc.getString("imagen_url") ?: ""
                        )
                    }
                }
            }
    }

    fun _Obtener_Blind_Por_ID(idBusqueda: String): BlindModel? {
        return _blinds.value.find { it.id.equals(idBusqueda, ignoreCase = true) }
    }

    // SISTEMA DE PUNTUACIÓN EN TIEMPO REAL
    fun _Iniciar_Listener_Puntuacion(blindId: String, scoreBase: Double) {
        _puntuacionActual.value = scoreBase
        _Detener_Listener_Puntuacion()

        vListenerPuntuacion = db.collection("blinds").document(blindId)
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null && snapshot.exists()) {
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

    fun _Guardar_Recalcular(
        blindId: String,
        nombreBlind: String,
        userId: String,
        calificacion: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val blindDocRef = db.collection("blinds").document(blindId)
        val votosCollectionRef = blindDocRef.collection("votos")

        val votoData = hashMapOf(
            "usuarioId" to userId,
            "blindNombre" to nombreBlind,
            "puntuacion" to calificacion
        )

        votosCollectionRef.document(userId).set(votoData)
            .addOnSuccessListener {
                votosCollectionRef.get().addOnSuccessListener { snapshot ->
                    if (snapshot != null && !snapshot.isEmpty) {
                        var vSumaPuntuaciones = 0.0
                        for (document in snapshot.documents) {
                            vSumaPuntuaciones += (document.getDouble("puntuacion") ?: 0.0)
                        }
                        val promedioRedondeado = round((vSumaPuntuaciones / snapshot.size()) * 10) / 10.0

                        blindDocRef.update("puntuacion_usuarios", promedioRedondeado)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e -> onError("Error al actualizar la media: ${e.message}") }
                    }
                }
            }
            .addOnFailureListener { e -> onError("Error al guardar tu voto: ${e.message}") }
    }

    // OPCIONES DE ADMINISTRADOR
    fun _Añadir_Nueva_Blind(
        nombre: String,
        modificador: String,
        imagenUrl: String,
        recompensa: String,
        jokersRecomendados: List<ItemSelectorModel>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val documentId = "blind_${nombre.lowercase().replace(" ", "_")}"
        val jokersMap = jokersRecomendados.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }

        val docRef = db.collection("blinds").document(documentId)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onError("Ya existe una Blind registrada con el nombre '$nombre'.")
                } else {
                    val nuevaBlind = hashMapOf(
                        "id" to documentId,
                        "nombre" to nombre,
                        "modificador" to modificador,
                        "imagen_url" to imagenUrl,
                        "recompensa" to recompensa,
                        "puntuacion_usuarios" to 0.0,
                        "jokersRecomendados" to jokersMap
                    )

                    docRef.set(nuevaBlind)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { exception -> onError(exception.message ?: "Error al guardar") }
                }
            }
            .addOnFailureListener { exception ->
                onError(exception.message ?: "Error al conectar con la base de datos")
            }
    }

    fun _Actualizar_Blind(
        id: String,
        nombre: String,
        modificador: String,
        imagenUrl: String,
        recompensa: String,
        jokersRecomendados: List<ItemSelectorModel>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val jokersMap = jokersRecomendados.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }

        val datosActualizados = hashMapOf(
            "nombre" to nombre,
            "modificador" to modificador,
            "imagen_url" to imagenUrl,
            "recompensa" to recompensa,
            "jokersRecomendados" to jokersMap
        )

        db.collection("blinds").document(id)
            .update(datosActualizados as Map<String, Any>)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception.message ?: "Error al actualizar") }
    }

    fun _Eliminar_Blind(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("blinds").document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception.message ?: "Error al eliminar") }
    }

    override fun onCleared() {
        super.onCleared()
        vListenerBlinds?.remove()
        vListenerPuntuacion?.remove()
        vListenerJokers?.remove()
    }
}