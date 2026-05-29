package com.example.balatropedia.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun _Iniciar_Sesion(email: String, pass: String, onSuccess: () -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            _errorMessage.value = "Por favor, llena todos los campos."
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        auth.signInWithEmailAndPassword(email.trim(), pass.trim())
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    _errorMessage.value = task.exception?.localizedMessage ?: "Error al iniciar sesión"
                }
            }
    }

    fun _Registrar_Usuario(
        username: String,
        email: String,
        pass: String,
        confirmPass: String,
        pais: String,
        edad: String,
        onSuccess: () -> Unit
    ) {
        if (pass != confirmPass) {
            _errorMessage.value = "Las contraseñas no coinciden"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        auth.createUserWithEmailAndPassword(email.trim(), pass.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val userProfile = hashMapOf(
                        "username" to username.trim(),
                        "email" to email.trim(),
                        "pais" to pais,
                        "edad" to edad,
                        "rol" to "user"
                    )

                    if (userId != null) {
                        db.collection("users").document(userId)
                            .set(userProfile)
                            .addOnSuccessListener {
                                _isLoading.value = false
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                _isLoading.value = false
                                _errorMessage.value = "Error al guardar perfil: ${e.message}"
                            }
                    }
                } else {
                    _isLoading.value = false
                    _errorMessage.value = task.exception?.message ?: "Error al registrar"
                }
            }
    }

    fun _Limpiar_Error() {
        _errorMessage.value = null
    }
}

