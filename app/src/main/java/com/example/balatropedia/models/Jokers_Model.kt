package com.example.balatropedia.models

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.balatropedia.class_models.JokerModel
import com.google.firebase.firestore.FirebaseFirestore

class JokersViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _jokers = mutableStateOf<List<JokerModel>>(emptyList())
    val jokers: State<List<JokerModel>> = _jokers

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    init {
        _obtener_Jokers_De_Firebase()
    }

    fun _obtener_Jokers_De_Firebase() {
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
}