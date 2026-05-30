package com.example.balatropedia.models

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.balatropedia.class_models.ConsumibleModel
import com.example.balatropedia.class_models.ItemSelectorModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.round

class ConsumibleViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _consumibles = mutableStateOf<List<ConsumibleModel>>(emptyList())
    val consumibles: State<List<ConsumibleModel>> = _consumibles

    private val _boostersSelectorList = MutableStateFlow<List<ItemSelectorModel>>(emptyList())
    val boostersSelectorList: StateFlow<List<ItemSelectorModel>> = _boostersSelectorList.asStateFlow()

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _puntuacionActual = mutableStateOf(0.0)
    val puntuacionActual: State<Double> = _puntuacionActual

    private var vListenerPuntuacion: ListenerRegistration? = null
    private var vListenerConsumibles: ListenerRegistration? = null
    private var vListenerSobres: ListenerRegistration? = null

    init {
        _Obtener_Consumibles_De_Firebase()
        _Obtener_Sobres_Para_Selector()
    }

    fun _Obtener_Consumibles_De_Firebase() {
        _isLoading.value = true
        vListenerConsumibles?.remove()

        vListenerConsumibles = db.collection("consumibles")
            .addSnapshotListener { snapshot, error ->
                _isLoading.value = false

                if (error != null) {
                    println("ConsumibleViewModel - Error en escucha en tiempo real: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _consumibles.value = snapshot.documents.mapNotNull { documento ->
                        try {
                            documento.toObject(ConsumibleModel::class.java)
                        } catch (e: Exception) {
                            println("Error al mapear documento ${documento.id}: ${e.message}")
                            null
                        }
                    }
                }
            }
    }

    private fun _Obtener_Sobres_Para_Selector() {
        vListenerSobres?.remove()

        vListenerSobres = db.collection("booster_packs")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error al obtener sobres para el selector: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val listaSobres = snapshot.documents.map { doc ->
                        ItemSelectorModel(
                            id = doc.getString("id") ?: doc.id,
                            nombre = doc.getString("nombre") ?: "Sin nombre",
                            imagenUrl = doc.getString("imagen_url") ?: ""
                        )
                    }
                    _boostersSelectorList.value = listaSobres
                }
            }
    }

    fun _Obtener_Consumible_Por_ID(idBusqueda: String): ConsumibleModel? {
        return _consumibles.value.find { it.id.equals(idBusqueda, ignoreCase = true) }
    }

    // SISTEMA DE PUNTUACIÓN EN TIEMPO REAL
    fun _Iniciar_Listener_Puntuacion(consumibleId: String, puntuacionBase: Double) {
        _puntuacionActual.value = puntuacionBase

        _Detener_Listener_Puntuacion()

        vListenerPuntuacion = db.collection("consumibles").document(consumibleId)
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
        consumibleId: String,
        nombreConsumible: String,
        userId: String,
        calificacion: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val consumibleDocRef = db.collection("consumibles").document(consumibleId)
        val votosCollectionRef = consumibleDocRef.collection("votos")

        val votoData = hashMapOf(
            "usuarioId" to userId,
            "consumibleNombre" to nombreConsumible,
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

                        consumibleDocRef.update("puntuacion_usuarios", promedioRedondeado)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e -> onError("Error al actualizar la media: ${e.message}") }
                    }
                }
            }
            .addOnFailureListener { e -> onError("Error al guardar tu voto: ${e.message}") }
    }

    // OPCIONES DE ADMINISTRADOR
    fun _Añadir_Nuevo_Consumible(
        nombre: String,
        efecto: String,
        tipo: String,
        imagenUrl: String,
        boosterPack: List<Map<String, String>>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val documentId = "consumible_${nombre.lowercase().replace(" ", "_")}"

        val docRef = db.collection("consumibles").document(documentId)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onError("Ya existe un Consumible registrado con el nombre '$nombre'.")
                } else {
                    val nuevoConsumible = hashMapOf(
                        "id" to documentId,
                        "nombre" to nombre,
                        "efecto" to efecto,
                        "tipo" to tipo,
                        "imagen_url" to imagenUrl,
                        "puntuacion_usuarios" to 0.0,
                        "boosterPack" to boosterPack
                    )

                    docRef.set(nuevoConsumible)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { exception -> onError(exception.message ?: "Error al guardar") }
                }
            }
            .addOnFailureListener { exception ->
                onError(exception.message ?: "Error al conectar con la base de datos")
            }
    }

    fun _Actualizar_Consumible(
        id: String,
        nombre: String,
        efecto: String,
        tipo: String,
        imagenUrl: String,
        boosterPack: List<Map<String, String>>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val datosActualizados = hashMapOf(
            "nombre" to nombre,
            "efecto" to efecto,
            "tipo" to tipo,
            "imagen_url" to imagenUrl,
            "boosterPack" to boosterPack
        )

        db.collection("consumibles").document(id)
            .update(datosActualizados as Map<String, Any>)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception.message ?: "Error al actualizar") }
    }

    fun _Eliminar_Consumible(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("consumibles").document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception.message ?: "Error al eliminar") }
    }

    override fun onCleared() {
        super.onCleared()
        vListenerConsumibles?.remove()
        vListenerPuntuacion?.remove()
        vListenerSobres?.remove()
    }
}