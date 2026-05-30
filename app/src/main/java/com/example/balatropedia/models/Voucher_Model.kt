package com.example.balatropedia.models

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.balatropedia.class_models.VoucherModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlin.math.round

class VoucherViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _vouchers = mutableStateOf<List<VoucherModel>>(emptyList())
    val vouchers: State<List<VoucherModel>> = _vouchers

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _puntuacionActual = mutableStateOf(0.0)
    val puntuacionActual: State<Double> = _puntuacionActual

    private var vListenerVouchers: ListenerRegistration? = null
    private var vListenerPuntuacion: ListenerRegistration? = null

    init {
        _Obtener_Vouchers_De_Firebase()
    }

    fun _Obtener_Vouchers_De_Firebase() {
        _isLoading.value = true
        vListenerVouchers?.remove()

        vListenerVouchers = db.collection("vouchers")
            .addSnapshotListener { snapshot, error ->
                _isLoading.value = false

                if (error != null) {
                    println("VoucherViewModel - Error en escucha en tiempo real: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _vouchers.value = snapshot.documents.mapNotNull { documento ->
                        try {
                            documento.toObject(VoucherModel::class.java)
                        } catch (e: Exception) {
                            println("Error al mapear documento ${documento.id}: ${e.message}")
                            null
                        }
                    }
                }
            }
    }

    fun _Obtener_Voucher_Por_ID(idBusqueda: String): VoucherModel? {
        return _vouchers.value.find { it.id.equals(idBusqueda, ignoreCase = true) }
    }

    // SISTEMA DE PUNTUACIÓN EN TIEMPO REAL
    fun _Iniciar_Listener_Puntuacion(voucherId: String, puntuacionBase: Double) {
        _puntuacionActual.value = puntuacionBase
        _Detener_Listener_Puntuacion()

        vListenerPuntuacion = db.collection("vouchers").document(voucherId)
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
        voucherId: String,
        nombreVoucher: String,
        userId: String,
        calificacion: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val voucherDocRef = db.collection("vouchers").document(voucherId)
        val votosCollectionRef = voucherDocRef.collection("votos")

        val votoData = hashMapOf(
            "usuarioId" to userId,
            "voucherNombre" to nombreVoucher,
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

                        voucherDocRef.update("puntuacion_usuarios", promedioRedondeado)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e -> onError("Error al actualizar la media: ${e.message}") }
                    }
                }
            }
            .addOnFailureListener { e -> onError("Error al guardar tu voto: ${e.message}") }
    }

    // OPCIONES DE ADMINISTRADOR
    fun _Añadir_Nuevo_Voucher(
        nombre: String,
        efecto: String,
        requisitos: String,
        imagenUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val documentId = "voucher_${nombre.lowercase().trim().replace(" ", "_")}"

        val docRef = db.collection("vouchers").document(documentId)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onError("Ya existe un Voucher registrado con el nombre '$nombre'.")
                } else {
                    val nuevoVoucher = hashMapOf(
                        "id" to documentId,
                        "nombre" to nombre,
                        "efecto" to efecto,
                        "requisitos" to requisitos,
                        "imagen_url" to imagenUrl,
                        "puntuacion_usuarios" to 0.0
                    )

                    docRef.set(nuevoVoucher)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { exception ->
                            onError(
                                exception.message ?: "Error al guardar"
                            )
                        }
                }
            }
            .addOnFailureListener { exception ->
                onError(exception.message ?: "Error al conectar con la base de datos")
            }
    }

    fun _Actualizar_Voucher(
        id: String,
        nombre: String,
        efecto: String,
        requisitos: String,
        imagenUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val datosActualizados = hashMapOf(
            "nombre" to nombre,
            "efecto" to efecto,
            "requisitos" to requisitos,
            "imagen_url" to imagenUrl
        )

        db.collection("vouchers").document(id)
            .update(datosActualizados as Map<String, Any>)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception.message ?: "Error al actualizar") }
    }

    fun _Eliminar_Voucher(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("vouchers").document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception.message ?: "Error al eliminar") }
    }

    override fun onCleared() {
        super.onCleared()
        vListenerVouchers?.remove()
        vListenerPuntuacion?.remove()
    }
}

