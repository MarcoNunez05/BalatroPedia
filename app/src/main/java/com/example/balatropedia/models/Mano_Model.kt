// Última modificación: 01/06/2026
// Autor: Marco Núñez

package com.example.balatropedia.models

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balatropedia.class_models.ManoModel
import com.example.balatropedia.class_models.ItemSelectorModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.round

class ManoViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _manos = mutableStateOf<List<ManoModel>>(emptyList())
    val manos: State<List<ManoModel>> = _manos

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _puntuacionActual = mutableStateOf(0.0)
    val puntuacionActual: State<Double> = _puntuacionActual

    private val _jokersValidados = mutableStateOf<List<Map<String, String>>>(emptyList())
    val jokersValidados: State<List<Map<String, String>>> = _jokersValidados

    private val _cartasPlanetaValidadas = mutableStateOf<List<Map<String, String>>>(emptyList())
    val cartasPlanetaValidadas: State<List<Map<String, String>>> = _cartasPlanetaValidadas

    private val _cargandoRelaciones = mutableStateOf(true)
    val cargandoRelaciones: State<Boolean> = _cargandoRelaciones

    private var vListenerPuntuacion: ListenerRegistration? = null
    private var vListenerManos: ListenerRegistration? = null
    private var vListenerJokers: ListenerRegistration? = null
    private var vListenerCartasPlaneta: ListenerRegistration? = null

    private val _jokersSelectorList = MutableStateFlow<List<ItemSelectorModel>>(emptyList())
    val jokersSelectorList: StateFlow<List<ItemSelectorModel>> = _jokersSelectorList.asStateFlow()

    private val _cartasPlanetaSelectorList = MutableStateFlow<List<ItemSelectorModel>>(emptyList())
    val cartasPlanetaSelectorList: StateFlow<List<ItemSelectorModel>> = _cartasPlanetaSelectorList.asStateFlow()

    init {
        _Obtener_Manos_De_Firebase()
        _Cargar_Listas_Selector()
    }

    fun _Validar_Relaciones(mano: ManoModel) {
        viewModelScope.launch {
            _cargandoRelaciones.value = true

            try {
                _jokersValidados.value = mano.jokersAfectados.filter { mapaJoker ->
                    val idJoker = mapaJoker["id"] ?: return@filter false
                    db.collection("jokers").document(idJoker).get().await().exists()
                }

                _cartasPlanetaValidadas.value = mano.cartaPlaneta.filter { mapaPlaneta ->
                    val idPlaneta = mapaPlaneta["id"] ?: return@filter false
                    db.collection("consumibles").document(idPlaneta).get().await().exists()
                }
            } catch (e: Exception) {
                _jokersValidados.value = emptyList()
                _cartasPlanetaValidadas.value = emptyList()
                println("ManoViewModel - Error al validar relaciones: ${e.message}")
            } finally {
                _cargandoRelaciones.value = false
            }
        }
    }

    fun _Obtener_Manos_De_Firebase() {
        _isLoading.value = true
        vListenerManos?.remove()

        vListenerManos = db.collection("manos")
            .addSnapshotListener { snapshot, error ->
                _isLoading.value = false

                if (error != null) {
                    println("ManoViewModel - Error en escucha en tiempo real: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _manos.value = snapshot.documents.mapNotNull { documento ->
                        try {
                            documento.toObject(ManoModel::class.java)
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
                        ItemSelectorModel(id = doc.id, nombre = nombre, imagenUrl = doc.getString("imagen_url") ?: "")
                    }
                }
            }

        vListenerCartasPlaneta?.remove()
        vListenerCartasPlaneta = db.collection("consumibles")
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    _cartasPlanetaSelectorList.value = snapshot.documents.mapNotNull { doc ->
                        val nombre = doc.getString("nombre") ?: return@mapNotNull null
                        ItemSelectorModel(id = doc.id, nombre = nombre, imagenUrl = doc.getString("imagen_url") ?: "")
                    }
                }
            }
    }

    fun _Obtener_Mano_Por_ID(idBusqueda: String): ManoModel? {
        return _manos.value.find { it.id.equals(idBusqueda, ignoreCase = true) }
    }

    // SISTEMA DE PUNTUACIÓN EN TIEMPO REAL
    fun _Iniciar_Listener_Puntuacion(manoId: String, puntuacionBase: Double) {
        _puntuacionActual.value = puntuacionBase

        _Detener_Listener_Puntuacion()

        vListenerPuntuacion = db.collection("manos").document(manoId)
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
        manoId: String,
        nombreMano: String,
        userId: String,
        calificacion: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val manoDocRef = db.collection("manos").document(manoId)
        val votosCollectionRef = manoDocRef.collection("votos")

        val votoData = hashMapOf(
            "usuarioId" to userId,
            "manoNombre" to nombreMano,
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

                        manoDocRef.update("puntuacion_usuarios", promedioRedondeado)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e -> onError("Error al actualizar la media: ${e.message}") }
                    }
                }
            }
            .addOnFailureListener { e -> onError("Error al guardar tu voto: ${e.message}") }
    }

    // OPCIONES DE ADMINISTRADOR
    fun _Añadir_Nueva_Mano(
        nombre: String,
        descripcion: String,
        puntuacionBase: Int,
        multiplicadorBase: Int,
        jokersAfectados: List<ItemSelectorModel>,
        cartaPlaneta: List<ItemSelectorModel>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val documentId = "mano_${nombre.lowercase().replace(" ", "_")}"

        val jokersMap = jokersAfectados.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }
        val cartaPlanetaMap = cartaPlaneta.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }

        val docRef = db.collection("manos").document(documentId)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onError("Ya existe una Mano registrada con el nombre '$nombre'.")
                } else {
                    val nuevaMano = hashMapOf(
                        "id" to documentId,
                        "nombre" to nombre,
                        "descripcion" to descripcion,
                        "puntuacion_base" to puntuacionBase,
                        "multiplicador_base" to multiplicadorBase,
                        "puntuacion_usuarios" to 0.0,
                        "jokersAfectados" to jokersMap,
                        "cartaPlaneta" to cartaPlanetaMap
                    )

                    docRef.set(nuevaMano)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { exception -> onError(exception.message ?: "Error al guardar") }
                }
            }
            .addOnFailureListener { exception ->
                onError(exception.message ?: "Error al conectar con la base de datos")
            }
    }

    fun _Actualizar_Mano(
        id: String,
        nombre: String,
        descripcion: String,
        puntuacionBase: Int,
        multiplicadorBase: Int,
        jokersAfectados: List<ItemSelectorModel>,
        cartaPlaneta: List<ItemSelectorModel>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val jokersMap = jokersAfectados.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }
        val cartaPlanetaMap = cartaPlaneta.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }

        val datosActualizados = hashMapOf(
            "nombre" to nombre,
            "descripcion" to descripcion,
            "puntuacion_base" to puntuacionBase,
            "multiplicador_base" to multiplicadorBase,
            "jokersAfectados" to jokersMap,
            "cartaPlaneta" to cartaPlanetaMap
        )

        db.collection("manos").document(id)
            .update(datosActualizados as Map<String, Any>)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception.message ?: "Error al actualizar") }
    }

    fun _Eliminar_Mano(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("manos").document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception.message ?: "Error al eliminar") }
    }

    override fun onCleared() {
        super.onCleared()
        vListenerManos?.remove()
        vListenerPuntuacion?.remove()
        vListenerJokers?.remove()
        vListenerCartasPlaneta?.remove()
    }
}