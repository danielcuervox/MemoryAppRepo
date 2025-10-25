package com.example.memoryapplication.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.memoryapplication.Modelo.Tema
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TemaViewModel : ViewModel (){

    private val TAG = "UsuarioViewModel"
    var db = Firebase.firestore


    private val _nombreTema = MutableStateFlow<String>("")
    val nombreTema = _nombreTema.asStateFlow()


    fun agregarNuevoTema(nuevoTema: Tema, onResult: (Boolean, String) -> Unit){

        val listasRef = db.collection("temas")
        nuevoTema.idTema = listasRef.document().id

        listasRef
            .whereEqualTo("idUsuario", nuevoTema.idUsuario)
            .whereEqualTo("nombreTema", _nombreTema.value)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    onResult(false, "Ya existe un tema con el nombre ${nuevoTema.nombreTema}")
                } else {

                    val nuevoDocRef = listasRef.document()
                    nuevoTema.idTema = nuevoDocRef.id


                    nuevoDocRef.set(nuevoTema)
                        .addOnSuccessListener {
                            onResult(true, "${nuevoTema.nombreTema} agregada correctamente")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error agregando Tema", e)
                            onResult(false, "Error al agregar ${nuevoTema.nombreTema}")
                        }

                }
            }


        //verificar que no haya un tema con el mismo nombre
        //devolver un boleano para confirmar

    }
}