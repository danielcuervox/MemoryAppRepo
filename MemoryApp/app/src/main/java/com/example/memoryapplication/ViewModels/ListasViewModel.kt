package com.example.memoryapplication.ViewModels

import android.util.Log
import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memoryapplication.Modelo.Lista
import com.example.memoryapplication.Modelo.Tema
import com.example.memoryapplication.Modelo.Usuario
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListasViewModel : ViewModel() {

    private val TAG = "UsuarioViewModel"
    var db = Firebase.firestore

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual = _usuarioActual.asStateFlow()

    private val _temaActual = MutableStateFlow<Tema?>(null)
    val temaActual = _temaActual.asStateFlow()

    private val _listaDeListas = MutableStateFlow<List<Lista>>(emptyList())
    val listaDeListas: StateFlow<List<Lista>> = _listaDeListas

    private val _nivelActualLista = MutableStateFlow<Int>(1)
    val nivelActualLista: StateFlow<Int> = _nivelActualLista.asStateFlow()


    init {
        var usuarioActual = auth.currentUser
        if (usuarioActual != null) {
            cargarListasUsuarioYTema(usuarioActual.uid, temaActual.value?.idTema ?: "")

        }

    }


    fun agregarLista(nuevaLista: Lista, onResult: (Boolean, String) -> Unit) {
        val listasRef = db.collection("listas")

        // Crear referencia del nuevo documento y asignar el ID a la lista
        val nuevoDocRef = listasRef.document()
        nuevaLista.idLista = nuevoDocRef.id

        // Verificar si ya existe una lista con el mismo nombre para ese usuario y tema
        listasRef
            .whereEqualTo("idUsuario", nuevaLista.idUsuario)
            .whereEqualTo("idTema", nuevaLista.idTema)
            .whereEqualTo("nombreLista", nuevaLista.nombreLista)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    onResult(false, "Ya existe una lista con ese nombre para este tema.")
                } else {
                    // Crear la nueva lista en Firestore
                    nuevoDocRef.set(nuevaLista)
                        .addOnSuccessListener {
                            onResult(true, "Lista creada correctamente.")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error creando lista", e)
                            onResult(false, "Error al crear lista.")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error verificando existencia de lista", e)
                onResult(false, "Error de conexión al verificar la lista.")
            }
    }


    fun updateLevel(idLista: String, nivelCompletado:Int){

        val listasRef = db.collection("listas")

        viewModelScope.launch(Dispatchers.IO) {
            listasRef
                .document(idLista)
                .update("puntuacion", nivelCompletado)
                .addOnSuccessListener {
                    // Se ejecutó correctamente
                    Log.d("ListasViewModel", "Nivel de la lista $idLista actualizado a $nivelCompletado.")
                }
                .addOnFailureListener { e ->
                    // Hubo un error durante la actualización
                    Log.e("ListasViewModel", "Error al actualizar el nivel de la lista $idLista", e)
                }
        }

    }

    fun cargarListasUsuarioYTema(idUsuario: String, idTema: String) {
        db.collection("listas")
            .whereEqualTo("idUsuario", idUsuario)
            .whereEqualTo("idTema", idTema)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                val lista = snapshot?.toObjects(Lista::class.java) ?: emptyList()
                _listaDeListas.value = lista
            }
    }

    fun getNivelActual(idLista:String){

        viewModelScope.launch(Dispatchers.IO) {
            db.collection("listas")
                .document(idLista)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val puntuacion = documentSnapshot.getLong("puntuacion")?.toInt() ?: 1
                        _nivelActualLista.value = puntuacion
                        Log.d("ListasViewModel", "Nivel obtenido para la lista $idLista: $puntuacion")
                    } else {
                        // El documento no existe, reseteamos a 1.
                        _nivelActualLista.value = 1
                        Log.w("ListasViewModel", "La lista $idLista no fue encontrada.")
                    }
                }
                .addOnFailureListener { e ->
                    // En caso de error, también reseteamos a 1 para estar seguros.
                    _nivelActualLista.value = 1
                    Log.e("ListasViewModel", "Error al obtener el nivel de la lista $idLista", e)
                }
        }

    }

}