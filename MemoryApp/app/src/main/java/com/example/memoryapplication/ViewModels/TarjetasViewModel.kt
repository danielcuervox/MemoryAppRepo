package com.example.memoryapplication.ViewModels

import android.net.Uri
import android.util.Log
import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memoryapplication.Modelo.Lista
import com.example.memoryapplication.Modelo.Tarjeta
import com.example.memoryapplication.Modelo.Tema
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

import kotlinx.coroutines.tasks.await // <-- AÑADE ESTA IMPORTACIÓN PARA .await()

class TarjetasViewModel: ViewModel() {

    private val TAG = "tarjtasViewModel"
    var db = Firebase.firestore
    private val storageRef = Firebase.storage.reference

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
            .whereEqualTo("idLista", nuevaTarjeta.idLista)
            .whereEqualTo("preguntaFrente", nuevaTarjeta.preguntaFrente)
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

    fun actualizarPuntajes(tarjetaActual: Tarjeta, usuarioId: String){

        //actualizar la tarjeta con el puntaje actual
        db.collection("tarjetas")
            .whereEqualTo("idUsuario", usuarioId)
            .whereEqualTo("idTarjeta", tarjetaActual.idTarjeta)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val tarjetaDoc = querySnapshot.documents[0]
                    val tarjetaId = tarjetaDoc.id
                    db.collection("tarjetas")
                        .document(tarjetaId)
                        .update("puntajeTarjeta", tarjetaActual.puntajeTarjeta)

                }
            }

        Log.e(TAG, "SE ACTUALIZO EL PUNTAJE DE LA TARJETA")
    }

    fun reiniciarTarjetas(listaActual: List<Tarjeta>, idUsuario: String){

        for (tarjeta in listaActual) {
            val tarjetaReiniciada = tarjeta.copy(puntajeTarjeta = 0)
            // Aquí se hace la llamada a Firebase
            actualizarPuntajes(tarjetaReiniciada, idUsuario)
        }
    }



    fun agregarTarjetaConAudio(nuevaTarjeta: Tarjeta, audioFile: File, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val tarjetasRef = db.collection("tarjetas")

            // 1. Primero, verificamos si ya existe una tarjeta duplicada (igual que en la otra función)
            tarjetasRef
                .whereEqualTo("idUsuario", nuevaTarjeta.idUsuario)
                .whereEqualTo("idTema", nuevaTarjeta.idTema)
                .whereEqualTo("idLista", nuevaTarjeta.idLista)
                .whereEqualTo("preguntaFrente", nuevaTarjeta.preguntaFrente)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        // Si ya existe, no hacemos nada y notificamos al usuario.
                        onResult(false, "Ya existe una tarjeta con esa pregunta en esta lista.")
                        return@addOnSuccessListener
                    }

                    // 2. Si no existe, procedemos a subir el audio y guardar la tarjeta.
                    // Usamos un nuevo launch para mantener el contexto de corutinas.
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            // Subida del archivo a Storage (esta parte ya la tenías bien)
                            val audioStorageRef = storageRef.child("audios/${nuevaTarjeta.idUsuario}/${System.currentTimeMillis()}.m4a")
                            val uploadTask = audioStorageRef.putFile(Uri.fromFile(audioFile)).await()
                            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()

                            // Asignamos un ID al nuevo documento y añadimos la URL del audio
                            val nuevoDocRef = tarjetasRef.document()
                            val tarjetaFinal = nuevaTarjeta.copy(
                                idTarjeta = nuevoDocRef.id,
                                audioUrl = downloadUrl
                            )

                            // Guardamos la tarjeta final en Firestore usando .set()
                            nuevoDocRef.set(tarjetaFinal)
                                .addOnSuccessListener {
                                    onResult(true, "Tarjeta con audio guardada.")
                                }
                                .addOnFailureListener { e ->
                                    onResult(false, "Error al guardar la tarjeta: ${e.message}")
                                }

                        } catch (e: Exception) {
                            // Este withContext es para poder mostrar el Toast desde un hilo de fondo
                            withContext(Dispatchers.Main) {
                                onResult(false, "Error al subir el audio: ${e.message}")
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Este error suele ser por falta de índice. El Logcat te dará el enlace para crearlo.
                    Log.e(TAG, "Error al verificar duplicados para tarjeta de audio", e)
                    onResult(false, "Error de conexión o índice faltante.")
                }
        }
    }


}