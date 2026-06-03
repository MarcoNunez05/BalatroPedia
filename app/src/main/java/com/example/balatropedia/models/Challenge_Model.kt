package com.example.balatropedia.models

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balatropedia.class_models.ChallengeModel
import com.example.balatropedia.class_models.ItemSelectorModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.round

class ChallengeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _challenges = mutableStateOf<List<ChallengeModel>>(emptyList())
    val challenges: State<List<ChallengeModel>> = _challenges

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _puntuacionActual = mutableStateOf(0.0)
    val puntuacionActual: State<Double> = _puntuacionActual

    private val _jokersIncluidosValidados = mutableStateOf<List<Map<String, String>>>(emptyList())
    val jokersIncluidosValidados: State<List<Map<String, String>>> = _jokersIncluidosValidados

    private val _consumiblesIncluidosValidados = mutableStateOf<List<Map<String, String>>>(emptyList())
    val consumiblesIncluidosValidados: State<List<Map<String, String>>> = _consumiblesIncluidosValidados

    private val _vouchersIncluidosValidados = mutableStateOf<List<Map<String, String>>>(emptyList())
    val vouchersIncluidosValidados: State<List<Map<String, String>>> = _vouchersIncluidosValidados

    private val _jokersProhibidosValidados = mutableStateOf<List<Map<String, String>>>(emptyList())
    val jokersProhibidosValidados: State<List<Map<String, String>>> = _jokersProhibidosValidados

    private val _consumiblesProhibidosValidados = mutableStateOf<List<Map<String, String>>>(emptyList())
    val consumiblesProhibidosValidados: State<List<Map<String, String>>> = _consumiblesProhibidosValidados

    private val _vouchersProhibidosValidados = mutableStateOf<List<Map<String, String>>>(emptyList())
    val vouchersProhibidosValidados: State<List<Map<String, String>>> = _vouchersProhibidosValidados

    private val _cargandoRelaciones = mutableStateOf(true)
    val cargandoRelaciones: State<Boolean> = _cargandoRelaciones

    private var vListenerPuntuacion: ListenerRegistration? = null
    private var vListenerChallenges: ListenerRegistration? = null
    private var vListenerJokers: ListenerRegistration? = null
    private var vListenerConsumibles: ListenerRegistration? = null
    private var vListenerVouchers: ListenerRegistration? = null

    private val _jokersSelectorList = MutableStateFlow<List<ItemSelectorModel>>(emptyList())
    val jokersSelectorList: StateFlow<List<ItemSelectorModel>> = _jokersSelectorList.asStateFlow()

    private val _consumiblesSelectorList = MutableStateFlow<List<ItemSelectorModel>>(emptyList())
    val consumiblesSelectorList: StateFlow<List<ItemSelectorModel>> = _consumiblesSelectorList.asStateFlow()

    private val _vouchersSelectorList = MutableStateFlow<List<ItemSelectorModel>>(emptyList())
    val vouchersSelectorList: StateFlow<List<ItemSelectorModel>> = _vouchersSelectorList.asStateFlow()

    init {
        _Obtener_Challenges_De_Firebase()
        _Cargar_Listas_Selector()
    }

    fun _Validar_Relaciones(challenge: ChallengeModel) {
        viewModelScope.launch {
            _cargandoRelaciones.value = true

            try {
                // INCLUIDOS
                _jokersIncluidosValidados.value = challenge.jokersIncluidos.filter { mapa ->
                    val id = mapa["id"] ?: return@filter false
                    db.collection("jokers").document(id).get().await().exists()
                }

                _consumiblesIncluidosValidados.value = challenge.consumiblesIncluidos.filter { mapa ->
                    val id = mapa["id"] ?: return@filter false
                    db.collection("consumibles").document(id).get().await().exists()
                }

                _vouchersIncluidosValidados.value = challenge.vouchersIncluidos.filter { mapa ->
                    val id = mapa["id"] ?: return@filter false
                    db.collection("vouchers").document(id).get().await().exists()
                }

                // PROHIBIDOS
                _jokersProhibidosValidados.value = challenge.jokersProhibidos.filter { mapa ->
                    val id = mapa["id"] ?: return@filter false
                    db.collection("jokers").document(id).get().await().exists()
                }

                _consumiblesProhibidosValidados.value = challenge.consumiblesProhibidos.filter { mapa ->
                    val id = mapa["id"] ?: return@filter false
                    db.collection("consumibles").document(id).get().await().exists()
                }

                _vouchersProhibidosValidados.value = challenge.vouchersProhibidos.filter { mapa ->
                    val id = mapa["id"] ?: return@filter false
                    db.collection("vouchers").document(id).get().await().exists()
                }

            } catch (e: Exception) {
                _jokersIncluidosValidados.value = emptyList()
                _consumiblesIncluidosValidados.value = emptyList()
                _vouchersIncluidosValidados.value = emptyList()
                _jokersProhibidosValidados.value = emptyList()
                _consumiblesProhibidosValidados.value = emptyList()
                _vouchersProhibidosValidados.value = emptyList()
                println("ChallengeViewModel - Error al validar relaciones: ${e.message}")
            } finally {
                _cargandoRelaciones.value = false
            }
        }
    }

    fun _Obtener_Challenges_De_Firebase() {
        _isLoading.value = true
        vListenerChallenges?.remove()

        vListenerChallenges = db.collection("challenges")
            .addSnapshotListener { snapshot, error ->
                _isLoading.value = false

                if (error != null) {
                    println("ChallengeViewModel - Error en escucha en tiempo real: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _challenges.value = snapshot.documents.mapNotNull { documento ->
                        try {
                            documento.toObject(ChallengeModel::class.java)
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

        vListenerConsumibles?.remove()
        vListenerConsumibles = db.collection("consumibles")
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    _consumiblesSelectorList.value = snapshot.documents.mapNotNull { doc ->
                        val nombre = doc.getString("nombre") ?: return@mapNotNull null
                        ItemSelectorModel(id = doc.id, nombre = nombre, imagenUrl = doc.getString("imagen_url") ?: "")
                    }
                }
            }

        vListenerVouchers?.remove()
        vListenerVouchers = db.collection("vouchers")
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    _vouchersSelectorList.value = snapshot.documents.mapNotNull { doc ->
                        val nombre = doc.getString("nombre") ?: return@mapNotNull null
                        ItemSelectorModel(id = doc.id, nombre = nombre, imagenUrl = doc.getString("imagen_url") ?: "")
                    }
                }
            }
    }

    fun _Obtener_Challenge_Por_ID(idBusqueda: String): ChallengeModel? {
        return _challenges.value.find { it.id.equals(idBusqueda, ignoreCase = true) }
    }

    // SISTEMA DE PUNTUACIÓN EN TIEMPO REAL
    fun _Iniciar_Listener_Puntuacion(challengeId: String, puntuacionBase: Double) {
        _puntuacionActual.value = puntuacionBase
        _Detener_Listener_Puntuacion()

        vListenerPuntuacion = db.collection("challenges").document(challengeId)
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
        challengeId: String,
        nombreChallenge: String,
        userId: String,
        calificacion: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val challengeDocRef = db.collection("challenges").document(challengeId)
        val votosCollectionRef = challengeDocRef.collection("votos")

        val votoData = hashMapOf(
            "usuarioId" to userId,
            "challengeNombre" to nombreChallenge,
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

                        challengeDocRef.update("puntuacion_usuarios", promedioRedondeado)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e -> onError("Error al actualizar la media: ${e.message}") }
                    }
                }
            }
            .addOnFailureListener { e -> onError("Error al guardar tu voto: ${e.message}") }
    }

    // OPCIONES DE ADMINISTRADOR
    fun _Añadir_Nuevo_Challenge(
        nombre: String,
        descripcion: String,
        requisitos: String,
        jokersIncluidos: List<ItemSelectorModel>,
        consumiblesIncluidos: List<ItemSelectorModel>,
        vouchersIncluidos: List<ItemSelectorModel>,
        jokersProhibidos: List<ItemSelectorModel>,
        consumiblesProhibidos: List<ItemSelectorModel>,
        vouchersProhibidos: List<ItemSelectorModel>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val documentId = "challenge_${nombre.lowercase().replace(" ", "_")}"

        val jokersIncMap = jokersIncluidos.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }
        val consumiblesIncMap = consumiblesIncluidos.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }
        val vouchersIncMap = vouchersIncluidos.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }

        val jokersProMap = jokersProhibidos.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }
        val consumiblesProMap = consumiblesProhibidos.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }
        val vouchersProMap = vouchersProhibidos.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }

        val docRef = db.collection("challenges").document(documentId)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onError("Ya existe un Desafío registrado con el nombre '$nombre'.")
                } else {
                    val nuevoChallenge = hashMapOf(
                        "id" to documentId,
                        "nombre" to nombre,
                        "descripcion" to descripcion,
                        "requisitos" to requisitos,
                        "puntuacion_usuarios" to 0.0,
                        "jokersIncluidos" to jokersIncMap,
                        "consumiblesIncluidos" to consumiblesIncMap,
                        "vouchersIncluidos" to vouchersIncMap,
                        "jokersProhibidos" to jokersProMap,
                        "consumiblesProhibidos" to consumiblesProMap,
                        "vouchersProhibidos" to vouchersProMap
                    )

                    docRef.set(nuevoChallenge)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { exception -> onError(exception.message ?: "Error al guardar") }
                }
            }
            .addOnFailureListener { exception ->
                onError(exception.message ?: "Error al conectar con la base de datos")
            }
    }

    fun _Actualizar_Challenge(
        id: String,
        nombre: String,
        descripcion: String,
        requisitos: String,
        jokersIncluidos: List<ItemSelectorModel>,
        consumiblesIncluidos: List<ItemSelectorModel>,
        vouchersIncluidos: List<ItemSelectorModel>,
        jokersProhibidos: List<ItemSelectorModel>,
        consumiblesProhibidos: List<ItemSelectorModel>,
        vouchersProhibidos: List<ItemSelectorModel>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val jokersIncMap = jokersIncluidos.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }
        val consumiblesIncMap = consumiblesIncluidos.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }
        val vouchersIncMap = vouchersIncluidos.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }

        val jokersProMap = jokersProhibidos.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }
        val consumiblesProMap = consumiblesProhibidos.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }
        val vouchersProMap = vouchersProhibidos.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }

        val datosActualizados = hashMapOf(
            "nombre" to nombre,
            "descripcion" to descripcion,
            "requisitos" to requisitos,
            "jokersIncluidos" to jokersIncMap,
            "consumiblesIncluidos" to consumiblesIncMap,
            "vouchersIncluidos" to vouchersIncMap,
            "jokersProhibidos" to jokersProMap,
            "consumiblesProhibidos" to consumiblesProMap,
            "vouchersProhibidos" to vouchersProMap
        )

        db.collection("challenges").document(id)
            .update(datosActualizados as Map<String, Any>)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception.message ?: "Error al actualizar") }
    }

    fun _Eliminar_Challenge(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("challenges").document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception.message ?: "Error al eliminar") }
    }

    override fun onCleared() {
        super.onCleared()
        vListenerChallenges?.remove()
        vListenerPuntuacion?.remove()
        vListenerJokers?.remove()
        vListenerConsumibles?.remove()
        vListenerVouchers?.remove()
    }
}

