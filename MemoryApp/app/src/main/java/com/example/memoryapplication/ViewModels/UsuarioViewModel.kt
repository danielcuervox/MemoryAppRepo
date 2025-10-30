package com.example.memoryapplication.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.memoryapplication.Modelo.Lista
import com.example.memoryapplication.Modelo.Tarjeta
import com.example.memoryapplication.Modelo.Tema
import com.example.memoryapplication.Modelo.Usuario
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UsuarioViewModel : ViewModel() {

    private val TAG = "UsuarioViewModel"
    var db = Firebase.firestore

    // Variables de estado
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _usuarioPendiente = MutableStateFlow<Usuario?>(null)
    val usuarioPendiente = _usuarioPendiente.asStateFlow()

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual = _usuarioActual.asStateFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _nombreUsuario = MutableStateFlow<Usuario?>(null)
    val nombreUsuario = _nombreUsuario.asStateFlow()

    private val _contrasenia = MutableStateFlow<String>("")
    val contrasenia = _contrasenia.asStateFlow()

    private val _temas = MutableStateFlow<List<Tema>>(emptyList())
    val temas: StateFlow<List<Tema>> = _temas


    private val _nombreLista = MutableStateFlow<String>("")
    val nombreLista = _nombreLista.asStateFlow()

    init {
        var usuarioActual = auth.currentUser
        if (usuarioActual != null) {
            cargarTemasUsuario(usuarioActual.uid)
        }

    }


    fun loginWithGoogleCredential(credential: AuthCredential, onResult: (Boolean) -> Unit) {
        _isLoading.value = true
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            _isLoading.value = false
            onResult(task.isSuccessful)
        }
    }

    fun loginWithUsername(nombreUsuario: String, contrasenia: String, onResult: (Boolean, String) -> Unit){
        db.collection("usuarios")
            .whereEqualTo("nombreUsuario", nombreUsuario)
            .whereEqualTo("contrasenia", contrasenia)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documento = querySnapshot.documents.first()
                    val usuario = documento.toObject(Usuario::class.java)?.copy(id = documento.id)
                    _usuarioActual.value = usuario

                    onResult(true, "Bienvenido ${nombreUsuario}")
                }
                else{
                    onResult(false, "Nombre de usuario o contraseña incorrectos")

                }
            }


    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }


    fun registrarUsuario(nuevoUsuario:Usuario, onResult: (Boolean, String) -> Unit){

        val usuariosRef = db.collection("usuarios")

        usuariosRef
            .whereEqualTo("email", nuevoUsuario.email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    onResult(false, "El correo ya está registrado.")
                } else {
                    // si el email no existe, se revisa el nombre de usuario
                    usuariosRef
                        .whereEqualTo("nombreUsuario", nuevoUsuario.nombreUsuario)
                        .get()
                        .addOnSuccessListener { nameSnapshot ->
                            if (!nameSnapshot.isEmpty) {
                                onResult(false, "El nombre de usuario ya está en uso.")
                            } else {
                                // No hay duplicados se puede registrar
                                usuariosRef.add(nuevoUsuario)
                                    .addOnSuccessListener {
                                        onResult(true, "Usuario registrado correctamente.")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("Firestore", "Error registrando usuario", e)
                                        onResult(false, "Error al registrar usuario.")
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error verificando nombreUsuario", e)
                            onResult(false, "Error de conexión al verificar usuario.")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error verificando email", e)
                onResult(false, "Error de conexión al verificar email.")
            }


    }


    fun cargarTemasUsuario(idUsuario: String) {
        db.collection("temas")
            .whereEqualTo("idUsuario", idUsuario)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                val lista = snapshot?.toObjects(Tema::class.java) ?: emptyList()
                _temas.value = lista
            }
    }


    private val _temasPrueba = MutableStateFlow(
        listOf(
            Tema(idTema = "1", nombreTema = "Historia Universal", icono = "\uD83C\uDFDB", idUsuario = "user1"), // 🏛️
            Tema(idTema = "2", nombreTema = "Matemáticas Avanzadas", icono = "\u2795", idUsuario = "user1"), // ➕
            Tema(idTema = "3", nombreTema = "Física Cuántica", icono = "\uD83D\uDD2C", idUsuario = "user1"), // 🔬
            Tema(idTema = "4", nombreTema = "Programación en Kotlin", icono = "\uD83D\uDCBB", idUsuario = "user1"), // 💻
            Tema(idTema = "5", nombreTema = "Diseño de Interfaces", icono = "\uD83C\uDFA8", idUsuario = "user1"), // 🎨
            Tema(idTema = "6", nombreTema = "Psicología del Aprendizaje", icono = "\uD83E\uDDD0", idUsuario = "user1"), // 🧠
            Tema(idTema = "7", nombreTema = "Economía Internacional", icono = "\uD83D\uDCB0", idUsuario = "user1"), // 💰
            Tema(idTema = "8", nombreTema = "Biología Marina", icono = "\uD83D\uDC20", idUsuario = "user1"), // 🐠
            Tema(idTema = "9", nombreTema = "Arte Moderno", icono = "\uD83C\uDFA8", idUsuario = "user1"), // 🎨
            Tema(idTema = "10", nombreTema = "Filosofía Antigua", icono = "\uD83D\uDCDC", idUsuario = "user1") // 📜
        )
    )
    val temasPrueba = _temasPrueba.asStateFlow()


    private val _listasPrueba = MutableStateFlow(
        listOf(
            Lista(idLista = "1", nombreLista = "Saludos y Presentaciones", idUsuario = "user1", idTema = "1", puntuacion = -5),
            Lista(idLista = "2", nombreLista = "Colores y Números", idUsuario = "user1", idTema = "1", puntuacion = -10),
            Lista(idLista = "3", nombreLista = "La Familia", idUsuario = "user1", idTema = "1", puntuacion = 0),
            Lista(idLista = "4", nombreLista = "Comida y Bebidas", idUsuario = "user1", idTema = "2", puntuacion = 1),
            Lista(idLista = "5", nombreLista = "Ropa y Accesorios", idUsuario = "user1", idTema = "2", puntuacion = 3),
            Lista(idLista = "6", nombreLista = "La Casa y el Hogar", idUsuario = "user1", idTema = "3", puntuacion = 5),
            Lista(idLista = "7", nombreLista = "El Cuerpo Humano", idUsuario = "user1", idTema = "3", puntuacion = 7),
            Lista(idLista = "8", nombreLista = "Transporte y Viajes", idUsuario = "user1", idTema = "4", puntuacion = 8),
            Lista(idLista = "9", nombreLista = "Profesiones y Oficios", idUsuario = "user1", idTema = "5", puntuacion = 10),
            Lista(idLista = "10", nombreLista = "Tiempo y Estaciones", idUsuario = "user1", idTema = "5", puntuacion = 12)
        )
    )
    val listasPrueba = _listasPrueba.asStateFlow()


    private val _tarjetasPrueba = MutableStateFlow(
        listOf(
            // 🟢 Tema 1: Saludos y Presentaciones
            Tarjeta(
                idTarjeta = "1",
                preguntaFrente = "Hallo",
                respuestaReverso = "Hola",
                idUsuario = "user1",
                idTema = "1",
                idLista = "1",
                puntajeTarjeta = 0
            ),
            Tarjeta(idTarjeta = "2", preguntaFrente = "Guten Morgen", respuestaReverso = "Buenos días", idUsuario = "user1", idTema = "1", idLista = "1", puntajeTarjeta = 1),
            Tarjeta(idTarjeta = "3", preguntaFrente = "Guten Abend", respuestaReverso = "Buenas tardes/noches", idUsuario = "user1", idTema = "1", idLista = "1", puntajeTarjeta = 2),
            Tarjeta(idTarjeta = "4", preguntaFrente = "Wie geht’s?", respuestaReverso = "¿Cómo estás?", idUsuario = "user1", idTema = "1", idLista = "1", puntajeTarjeta = 3),
            Tarjeta(idTarjeta = "5", preguntaFrente = "Mir geht’s gut", respuestaReverso = "Estoy bien", idUsuario = "user1", idTema = "1", idLista = "1", puntajeTarjeta = 4),
            Tarjeta(idTarjeta = "6", preguntaFrente = "Das Brot", respuestaReverso = "El pan", idUsuario = "user1", idTema = "2", idLista = "4", puntajeTarjeta = 0),
            Tarjeta(idTarjeta = "7", preguntaFrente = "Der Käse", respuestaReverso = "El queso", idUsuario = "user1", idTema = "2", idLista = "4", puntajeTarjeta = 5),
            Tarjeta(idTarjeta = "8", preguntaFrente = "Die Milch", respuestaReverso = "La leche", idUsuario = "user1", idTema = "2", idLista = "4", puntajeTarjeta = 6),
            Tarjeta(idTarjeta = "9", preguntaFrente = "Das Wasser", respuestaReverso = "El agua", idUsuario = "user1", idTema = "2", idLista = "4", puntajeTarjeta = 7),
            Tarjeta(idTarjeta = "10", preguntaFrente = "Das Obst", respuestaReverso = "La fruta", idUsuario = "user1", idTema = "2", idLista = "4", puntajeTarjeta = 8),
            Tarjeta(idTarjeta = "11", preguntaFrente = "Das Haus", respuestaReverso = "La casa", idUsuario = "user1", idTema = "3", idLista = "6", puntajeTarjeta = 9),
            Tarjeta(idTarjeta = "12", preguntaFrente = "Das Zimmer", respuestaReverso = "La habitación", idUsuario = "user1", idTema = "3", idLista = "6", puntajeTarjeta = 10),
            Tarjeta(idTarjeta = "13", preguntaFrente = "Die Küche", respuestaReverso = "La cocina", idUsuario = "user1", idTema = "3", idLista = "6", puntajeTarjeta = 20)
            )
    )
    val tarjetasPrueba = _tarjetasPrueba.asStateFlow()
}