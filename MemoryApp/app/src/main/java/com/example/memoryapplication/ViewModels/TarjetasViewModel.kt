package com.example.memoryapplication.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.memoryapplication.Modelo.Lista
import com.example.memoryapplication.Modelo.Tarjeta
import com.example.memoryapplication.Modelo.Tema
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TarjetasViewModel: ViewModel() {

    private val TAG = "tarjtasViewModel"
    var db = Firebase.firestore

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _temaActual = MutableStateFlow<Tema?>(null)
    val temaActual = _temaActual.asStateFlow()

    private val _listaActual = MutableStateFlow<Lista?>(null)
    val listaActual = _listaActual.asStateFlow()

    private val _listaTarjetas = MutableStateFlow<List<Tarjeta>>(emptyList())
    val listaTarjetas: StateFlow<List<Tarjeta>> = _listaTarjetas

    init {
        var usuarioActual = auth.currentUser
        if (usuarioActual != null) {
            cargarListasTarjetas(
                usuarioActual.uid,
                temaActual.value?.idTema ?: "",
                listaActual.value?.idLista ?: ""
            )
        }
    }



    fun agergarTarjetaFlashcard(nuevaTarjeta: Tarjeta, onResult: (Boolean, String) -> Unit) {
        val listasRef = db.collection("tarjetas")

        // Crear referencia del nuevo documento y asignar el ID a la lista
        val nuevoDocRef = listasRef.document()
        nuevaTarjeta.idTarjeta = nuevoDocRef.id

        // Verificar si ya existe una lista con el mismo nombre para ese usuario y tema
        listasRef
            .whereEqualTo("idUsuario", nuevaTarjeta.idUsuario)
            .whereEqualTo("idTema", nuevaTarjeta.idTema)
            .whereEqualTo("nombreLista", nuevaTarjeta.preguntaFrente)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    onResult(false, "Ya hay una tarjeta con esa pregunta")
                } else {
                    nuevoDocRef.set(nuevaTarjeta)
                        .addOnSuccessListener {
                            onResult(true, "Tarjeta agregada correctamente.")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error creando lista", e)
                            onResult(false, "Error al agregar tarjeta")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error verificando existencia de la tarjeta", e)
                onResult(false, "Error de conexión al verificar la tarjeta.")
            }
    }

    fun cargarListasTarjetas(idUsuario: String, idTemaActual: String, idListaActual: String) {

        Log.d(TAG, "cargarListasTarjetas: $idUsuario $idTemaActual $idListaActual")

        db.collection("tarjetas")
            //.whereEqualTo("idUsuario", idUsuario)
            .whereEqualTo("idTema", idTemaActual)
            .whereEqualTo("idLista", idListaActual)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                val lista = snapshot?.toObjects(Tarjeta::class.java) ?: emptyList()
                _listaTarjetas.value = lista
            }

    }


}