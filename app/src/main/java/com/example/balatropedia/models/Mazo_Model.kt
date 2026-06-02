package com.example.balatropedia.models

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balatropedia.class_models.MazoModel
import com.example.balatropedia.class_models.ItemSelectorModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.round

class MazoViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _mazos = mutableStateOf<List<MazoModel>>(emptyList())
    val mazos: State<List<MazoModel>> = _mazos

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _puntuacionActual = mutableStateOf(0.0)
    val puntuacionActual: State<Double> = _puntuacionActual

    private val _consumiblesValidados = mutableStateOf<List<Map<String, String>>>(emptyList())
    val consumiblesValidados: State<List<Map<String, String>>> = _consumiblesValidados

    private val _vouchersValidados = mutableStateOf<List<Map<String, String>>>(emptyList())
    val vouchersValidados: State<List<Map<String, String>>> = _vouchersValidados

    private val _cargandoRelaciones = mutableStateOf(true)
    val cargandoRelaciones: State<Boolean> = _cargandoRelaciones

    private var vListenerPuntuacion: ListenerRegistration? = null
    private var vListenerMazos: ListenerRegistration? = null
    private var vListenerConsumibles: ListenerRegistration? = null
    private var vListenerVouchers: ListenerRegistration? = null

    private val _vouchersSelectorList = MutableStateFlow<List<ItemSelectorModel>>(emptyList())
    val vouchersSelectorList: StateFlow<List<ItemSelectorModel>> = _vouchersSelectorList.asStateFlow()

    private val _consumiblesSelectorList = MutableStateFlow<List<ItemSelectorModel>>(emptyList())
    val consumiblesSelectorList: StateFlow<List<ItemSelectorModel>> = _consumiblesSelectorList.asStateFlow()

    init {
        _Obtener_Mazos_De_Firebase()
        _Cargar_Listas_Selector()
    }

    fun _Validar_Relaciones(mazo: MazoModel) {
        viewModelScope.launch {
            _cargandoRelaciones.value = true

            try {
                _consumiblesValidados.value = mazo.consumiblesIncluidos.filter { mapaConsumible ->
                    val idConsumible = mapaConsumible["id"] ?: return@filter false
                    db.collection("consumibles").document(idConsumible).get().await().exists()
                }

                _vouchersValidados.value = mazo.vouchersIncluidos.filter { mapaVoucher ->
                    val idVoucher = mapaVoucher["id"] ?: return@filter false
                    db.collection("vouchers").document(idVoucher).get().await().exists()
                }

            } catch (e: Exception) {
                _consumiblesValidados.value = emptyList()
                _vouchersValidados.value = emptyList()
                println("MazosViewModel - Error al validar relaciones: ${e.message}")
            } finally {
                _cargandoRelaciones.value = false
            }
        }
    }

    fun _Obtener_Mazos_De_Firebase() {
        _isLoading.value = true
        vListenerMazos?.remove()

        vListenerMazos = db.collection("mazos")
            .addSnapshotListener { snapshot, error ->
                _isLoading.value = false

                if (error != null) {
                    println("MazosViewModel - Error en escucha en tiempo real: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _mazos.value = snapshot.documents.mapNotNull { documento ->
                        try {
                            documento.toObject(MazoModel::class.java)
                        } catch (e: Exception) {
                            println("Error al mapear documento ${documento.id}: ${e.message}")
                            null
                        }
                    }
                }
            }
    }

    private fun _Cargar_Listas_Selector() {
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

    fun _Obtener_Mazo_Por_ID(idBusqueda: String): MazoModel? {
        return _mazos.value.find { it.id.equals(idBusqueda, ignoreCase = true) }
    }

    // SISTEMA DE PUNTUACIÓN EN TIEMPO REAL
    fun _Iniciar_Listener_Puntuacion(mazoId: String, puntuacionBase: Double) {
        _puntuacionActual.value = puntuacionBase

        _Detener_Listener_Puntuacion()

        vListenerPuntuacion = db.collection("mazos").document(mazoId)
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
        mazoId: String,
        nombreMazo: String,
        userId: String,
        calificacion: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val mazoDocRef = db.collection("mazos").document(mazoId)
        val votosCollectionRef = mazoDocRef.collection("votos")

        val votoData = hashMapOf(
            "usuarioId" to userId,
            "mazoNombre" to nombreMazo,
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

                        mazoDocRef.update("puntuacion_usuarios", promedioRedondeado)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e -> onError("Error al actualizar la media: ${e.message}") }
                    }
                }
            }
            .addOnFailureListener { e -> onError("Error al guardar tu voto: ${e.message}") }
    }

    // OPCIONES DE ADMINISTRADOR
    fun _Añadir_Nuevo_Mazo(
        nombre: String,
        descripcion: String,
        vouchersIncluidos: List<ItemSelectorModel>,
        consumiblesIncluidos: List<ItemSelectorModel>,
        imagenUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val documentId = "mazo_${nombre.lowercase().replace(" ", "_")}"

        val vouchersMap = vouchersIncluidos.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }
        val consumiblesMap = consumiblesIncluidos.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }

        val docRef = db.collection("mazos").document(documentId)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onError("Ya existe un Mazo registrado con el nombre '$nombre'.")
                } else {
                    val nuevoMazo = hashMapOf(
                        "id" to documentId,
                        "nombre" to nombre,
                        "descripcion" to descripcion,
                        "imagen_url" to imagenUrl,
                        "puntuacion_usuarios" to 0.0,
                        "vouchersIncluidos" to vouchersMap,
                        "consumiblesIncluidos" to consumiblesMap
                    )

                    docRef.set(nuevoMazo)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { exception -> onError(exception.message ?: "Error al guardar") }
                }
            }
            .addOnFailureListener { exception ->
                onError(exception.message ?: "Error al conectar con la base de datos")
            }
    }

    fun _Actualizar_Mazo(
        id: String,
        nombre: String,
        descripcion: String,
        vouchersIncluidos: List<ItemSelectorModel>,
        consumiblesIncluidos: List<ItemSelectorModel>,
        imagenUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val vouchersMap = vouchersIncluidos.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }
        val consumiblesMap = consumiblesIncluidos.map { mapOf("id" to it.id, "nombre" to it.nombre, "imagenUrl" to it.imagenUrl) }

        val datosActualizados = hashMapOf(
            "nombre" to nombre,
            "descripcion" to descripcion,
            "imagen_url" to imagenUrl,
            "vouchersIncluidos" to vouchersMap,
            "consumiblesIncluidos" to consumiblesMap
        )

        db.collection("mazos").document(id)
            .update(datosActualizados as Map<String, Any>)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception.message ?: "Error al actualizar") }
    }

    fun _Eliminar_Mazo(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("mazos").document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception.message ?: "Error al eliminar") }
    }

    override fun onCleared() {
        super.onCleared()
        vListenerMazos?.remove()
        vListenerPuntuacion?.remove()
        vListenerConsumibles?.remove()
        vListenerVouchers?.remove()
    }
}