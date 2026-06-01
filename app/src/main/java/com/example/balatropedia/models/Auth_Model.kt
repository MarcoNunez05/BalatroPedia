package com.example.balatropedia.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun _Iniciar_Sesion(
        email: String,
        pass: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isBlank() || pass.isBlank()) {
            val errorCampos = "Por favor, llena todos los campos."
            _errorMessage.value = errorCampos
            onError(errorCampos)
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email.trim(), pass.trim()).await()

                _isLoading.value = false
                onSuccess()

            } catch (e: Exception) {
                _isLoading.value = false
                val errorFirebase = e.localizedMessage ?: "Error al iniciar sesión"

                _errorMessage.value = errorFirebase
                onError(errorFirebase)
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
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (pass != confirmPass) {
            val errorMsg = "Las contraseñas no coinciden"
            _errorMessage.value = errorMsg
            onError(errorMsg)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val authResult = auth.createUserWithEmailAndPassword(email.trim(), pass.trim()).await()
                val userId = authResult.user?.uid

                if (userId != null) {
                    val userProfile = hashMapOf(
                        "username" to username.trim(),
                        "email" to email.trim(),
                        "pais" to pais,
                        "edad" to edad,
                        "rol" to "user"
                    )

                    db.collection("users").document(userId)
                        .set(userProfile)
                        .await()

                    _isLoading.value = false
                    onSuccess()
                } else {
                    throw Exception("No se pudo obtener el ID del usuario creado.")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _isLoading.value = false

                val errorMsg = e.localizedMessage ?: "Error al registrar el usuario"
                _errorMessage.value = errorMsg
                onError(errorMsg)
            }
        }
    }

    fun _Actualizar_Usuario(
        username: String,
        pais: String,
        edad: String,
        pass: String,
        confirmPass: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            onError("Usuario no autenticado.")
            return
        }

        if (username.isEmpty())
        {
            onError("El nombre no debe estar vacío.")
            return
        }

        if (pass.isNotEmpty() || confirmPass.isNotEmpty()) {
            if (pass != confirmPass) {
                onError("Las contraseñas nuevas no coinciden.")
                return
            }
            if (pass.length < 6) {
                onError("La contraseña debe tener al menos 6 caracteres.")
                return
            }
        }

        _isLoading.value = true

        val updates = hashMapOf<String, Any>(
            "username" to username.trim(),
            "pais" to pais,
            "edad" to edad
        )

        db.collection("users").document(user.uid)
            .update(updates)
            .addOnSuccessListener {
                if (pass.isNotEmpty()) {
                    user.updatePassword(pass)
                        .addOnCompleteListener { task ->
                            _isLoading.value = false
                            if (task.isSuccessful) {
                                onSuccess()
                            } else {
                                onError(task.exception?.localizedMessage ?: "Por seguridad, cierra sesión y vuelve a entrar para cambiar tu contraseña.")
                            }
                        }
                } else {
                    _isLoading.value = false
                    onSuccess()
                }
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                onError(e.localizedMessage ?: "Error al actualizar los datos del perfil.")
            }
    }

    fun _Eliminar_Usuario(password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = auth.currentUser
        if (user == null || user.email == null) {
            onError("Usuario no encontrado.")
            return
        }

        _isLoading.value = true

        val credential = EmailAuthProvider.getCredential(user.email!!, password)

        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
            if (reauthTask.isSuccessful) {
                db.collection("users").document(user.uid).delete()
                    .addOnCompleteListener {
                        user.delete().addOnCompleteListener { deleteTask ->
                            _isLoading.value = false
                            if (deleteTask.isSuccessful) {
                                onSuccess()
                            } else {
                                onError(deleteTask.exception?.localizedMessage ?: "Error al eliminar la cuenta de Auth.")
                            }
                        }
                    }
            } else {
                _isLoading.value = false
                onError("La contraseña es incorrecta.")
            }
        }
    }

    fun _Limpiar_Error() {
        _errorMessage.value = null
    }
}