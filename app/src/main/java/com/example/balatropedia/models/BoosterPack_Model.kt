// Última modificación: 30/05/2026
// Autor: Marco Núñez

package com.example.balatropedia.models

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.balatropedia.class_models.BoosterPackModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlin.math.round

class BoosterPackViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _boosterPacks = mutableStateOf<List<BoosterPackModel>>(emptyList())
    val boosterPacks: State<List<BoosterPackModel>> = _boosterPacks

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _puntuacionActual = mutableStateOf(0.0)
    val puntuacionActual: State<Double> = _puntuacionActual

    private var vListenerPuntuacion: ListenerRegistration? = null
    private var vListenerBoosterPacks: ListenerRegistration? = null

    init {
        _Obtener_BoosterPacks_De_Firebase()
    }

    fun _Obtener_BoosterPacks_De_Firebase() {
        _isLoading.value = true
        vListenerBoosterPacks?.remove()

        vListenerBoosterPacks = db.collection("boosterPacks")
            .addSnapshotListener { snapshot, error ->
                _isLoading.value = false

                if (error != null) {
                    println("BoosterPackViewModel - Error en escucha en tiempo real: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _boosterPacks.value = snapshot.documents.mapNotNull { documento ->
                        try {
                            documento.toObject(BoosterPackModel::class.java)
                        } catch (e: Exception) {
                            println("Error al mapear documento ${documento.id}: ${e.message}")
                            null
                        }
                    }
                }
            }
    }

    fun _Obtener_BoosterPack_Por_ID(idBusqueda: String): BoosterPackModel? {
        return _boosterPacks.value.find { it.id.equals(idBusqueda, ignoreCase = true) }
    }

    // SISTEMA DE PUNTUACIÓN EN TIEMPO REAL
    fun _Iniciar_Listener_Puntuacion(boosterPackId: String, puntuacionBase: Double) {
        _puntuacionActual.value = puntuacionBase

        _Detener_Listener_Puntuacion()

        vListenerPuntuacion = db.collection("boosterPacks").document(boosterPackId)
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
        boosterPackId: String,
        nombreBoosterPack: String,
        userId: String,
        calificacion: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val boosterPackDocRef = db.collection("boosterPacks").document(boosterPackId)
        val votosCollectionRef = boosterPackDocRef.collection("votos")

        val votoData = hashMapOf(
            "usuarioId" to userId,
            "boosterPackNombre" to nombreBoosterPack,
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

                        boosterPackDocRef.update("puntuacion_usuarios", promedioRedondeado)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e -> onError("Error al actualizar la media: ${e.message}") }
                    }
                }
            }
            .addOnFailureListener { e -> onError("Error al guardar tu voto: ${e.message}") }
    }

    // OPCIONES DE ADMINISTRADOR
    fun _Añadir_Nuevo_BoosterPack(
        nombre: String,
        descripcion: String,
        imagenUrl: String,
        costoBase: Int,
        cartasDisponibles: Int,
        cartasElegibles: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val documentId = "boosterpack_${nombre.lowercase().replace(" ", "_")}"

        val docRef = db.collection("boosterPacks").document(documentId)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onError("Ya existe un Booster Pack registrado con el nombre '$nombre'.")
                } else {
                    val nuevoBoosterPack = hashMapOf(
                        "id" to documentId,
                        "nombre" to nombre,
                        "descripcion" to descripcion,
                        "imagen_url" to imagenUrl,
                        "costo_base" to costoBase,
                        "cartas_disponibles" to cartasDisponibles,
                        "cartas_elegibles" to cartasElegibles,
                        "puntuacion_usuarios" to 0.0
                    )

                    docRef.set(nuevoBoosterPack)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { exception -> onError(exception.message ?: "Error al guardar") }
                }
            }
            .addOnFailureListener { exception ->
                onError(exception.message ?: "Error al conectar con la base de datos")
            }
    }

    fun _Actualizar_BoosterPack(
        id: String,
        nombre: String,
        descripcion: String,
        imagenUrl: String,
        costoBase: Int,
        cartasDisponibles: Int,
        cartasElegibles: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val datosActualizados = hashMapOf(
            "nombre" to nombre,
            "descripcion" to descripcion,
            "imagen_url" to imagenUrl,
            "costo_base" to costoBase,
            "cartas_disponibles" to cartasDisponibles,
            "cartas_elegibles" to cartasElegibles
        )

        db.collection("boosterPacks").document(id)
            .update(datosActualizados as Map<String, Any>)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception.message ?: "Error al actualizar") }
    }

    fun _Eliminar_BoosterPack(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("boosterPacks").document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception.message ?: "Error al eliminar") }
    }

    override fun onCleared() {
        super.onCleared()
        vListenerBoosterPacks?.remove()
        vListenerPuntuacion?.remove()
    }
}