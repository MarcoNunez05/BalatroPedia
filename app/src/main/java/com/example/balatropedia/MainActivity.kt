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
import androidx.compose.runtime.LaunchedEffect
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

                    var isAdmin by remember { mutableStateOf(false) }
                    var isCheckingRole by remember { mutableStateOf(true) }

                    val currentUser = auth.currentUser

                    LaunchedEffect(currentUser) {
                        if (currentUser != null) {
                            db.collection("users").document(currentUser.uid).get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        val rol = document.getString("rol")
                                        isAdmin = (rol == "admin")
                                    } else {
                                        isAdmin = false
                                    }
                                    isCheckingRole = false
                                }
                                .addOnFailureListener {
                                    isAdmin = false
                                    isCheckingRole = false
                                }
                        } else {
                            isAdmin = false
                            isCheckingRole = false
                        }
                    }

                    if (isCheckingRole) {
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
                            isAdmin = isAdmin,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}