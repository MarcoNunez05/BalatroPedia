package com.example.balatropedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.balatropedia.navigation._App_Navigation
import com.example.balatropedia.ui.theme.BalatroPediaTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BalatroPediaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val auth = FirebaseAuth.getInstance()
                    val db = FirebaseFirestore.getInstance()

                    var vIsAdmin by remember { mutableStateOf(false) }
                    var vIsCheckingRole by remember { mutableStateOf(true) }


                    DisposableEffect(auth) {
                        var vFirestoreListener: com.google.firebase.firestore.ListenerRegistration? = null

                        val vAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                            val vUsuarioActual = firebaseAuth.currentUser

                            vFirestoreListener?.remove()

                            if (vUsuarioActual != null) {
                                vIsCheckingRole = true

                                vFirestoreListener = db.collection("users").document(vUsuarioActual.uid)
                                    .addSnapshotListener { documento, error ->
                                        if (error != null) {
                                            vIsAdmin = false
                                            vIsCheckingRole = false
                                            return@addSnapshotListener
                                        }

                                        if (documento != null && documento.exists()) {
                                            val vRol = documento.getString("rol")
                                            vIsAdmin = (vRol == "admin")
                                        } else {
                                            vIsAdmin = false
                                        }
                                        vIsCheckingRole = false
                                    }
                            } else {
                                vIsAdmin = false
                                vIsCheckingRole = false
                            }
                        }

                        auth.addAuthStateListener(vAuthListener)

                        onDispose {
                            auth.removeAuthStateListener(vAuthListener)
                            vFirestoreListener?.remove()
                        }
                    }

                    if (vIsCheckingRole) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(Color(0xFF1E222B)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF00BFFF))
                        }
                    } else {
                        _App_Navigation(
                            isAdmin = vIsAdmin,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}