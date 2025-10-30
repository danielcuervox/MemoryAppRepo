package com.example.memoryapplication.Ventanas

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.memoryapplication.Modelo.Lista
import com.example.memoryapplication.Modelo.Tarjeta
import com.example.memoryapplication.R
import com.example.memoryapplication.ViewModels.ListasViewModel
import com.example.memoryapplication.ViewModels.TarjetasViewModel
import com.example.memoryapplication.ViewModels.UsuarioViewModel
import com.example.memoryapplication.utils.BottonBarMio
import java.io.File
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentanaPalabras(navController: NavHostController,
                    usuarioViewModel: UsuarioViewModel,
                    listasViewModel : ListasViewModel,
                    idTema : String?,
                    idLista:String?) {

    val context = LocalContext.current
    val user = usuarioViewModel.getCurrentUser()

    val tarjetasViewModel: TarjetasViewModel = viewModel()
    val idTemaActual = idTema ?: ""
    val idListaActual = idLista ?: ""

    var preguntaFrente by remember { mutableStateOf("") }
    var respuestaReverso by remember { mutableStateOf("") }

    var grabando by remember { mutableStateOf(false) }
    var recorder: MediaRecorder? by remember { mutableStateOf(null) }
    var archivoAudio: File? by remember { mutableStateOf(null) }

    val listasTarjetas by tarjetasViewModel.listaTarjetas.collectAsState(initial = emptyList())

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido. Informa al usuario que ya puede grabar.
            Toast.makeText(context, "Permiso concedido. ¡Listo para grabar!", Toast.LENGTH_SHORT).show()
        } else {
            // Permiso denegado.
            Toast.makeText(context, "Permiso denegado. No se puede grabar audio.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(key1 = idTemaActual, idListaActual, Unit) {
        if (user != null && idTemaActual.isNotEmpty()) {
            tarjetasViewModel.cargarListasTarjetas(user.uid, idTemaActual, idListaActual)
        }
        //launcher.launch(Manifest.permission.RECORD_AUDIO)
    }

    var abrirAlertAgregarPalabra by remember { mutableStateOf(false) }
    var abrirAlertAgregarAudio by remember { mutableStateOf(false) }
    var abrirAlertTipoTarjeta by remember { mutableStateOf(false) }
    var AlerDiagBorrarPuntaje by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Tus palabras de ") },
            )
        },
        bottomBar = {
            BottonBarMio(
                navController = navController,
                usuarioViewModel = usuarioViewModel
            )
        },
        // Aquí añadimos el botón flotante
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    abrirAlertTipoTarjeta = true
                },
                shape = RoundedCornerShape(50.dp), // redondo
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_float_button),
                    contentDescription = "Nuevo Concepto",
                    tint = MaterialTheme.colorScheme.onPrimary, // 🔹 visible sobre fondo primario
                    modifier = Modifier.size(40.dp)
                )
            }
        },

        floatingActionButtonPosition = FabPosition.End // 🔸 esquina inferior derecha
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            if(listasTarjetas.isEmpty()){
                Text("No hay palabras para mostrar")
                Spacer(modifier = Modifier.height(16.dp))
                Text("Crea una lista para empezar a aprender")
                Spacer(modifier = Modifier.height(16.dp))

                Row(){
                    Button(onClick = {
                        navController.popBackStack()
                    }){
                        Text("Volver")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = {
                        abrirAlertTipoTarjeta = true
                    }){
                        Text("Crear lista")
                    }
                }

            }else{
                Button(onClick = {
                    navController.navigate("VentanaEstudioFlashCards/${idTema}/${idLista}")

                }, modifier = Modifier
                    .width(300.dp)
                    .height(56.dp)){
                    Text("INICIAR ESTUDIO")
                }

                Button(onClick = {
                    AlerDiagBorrarPuntaje = true
                }){
                    Text("Reiniciar Tarjetas")
                }

                ListaPalabras(
                    listasPalabras = listasTarjetas,
                    onClickPalabra = { palabra ->
                        //navController.navigate("VentanaPalabras/${lista.idLista}")
                        //navController.navigate(Rutas.VentanaPalabras)
                    }
                )
            }

            if(abrirAlertTipoTarjeta){
                AlertDialog(
                    onDismissRequest = { abrirAlertTipoTarjeta = false },
                    title = { Text("Elige el tipo de Tarjeta") },
                    text= {

                        val archivo = File(context.cacheDir, "grabacion_${System.currentTimeMillis()}.m4a")

                        Column {
                            Button(
                                onClick = {
                                    Toast.makeText(context, "se abre flashcard", Toast.LENGTH_SHORT).show()
                                    abrirAlertTipoTarjeta = false
                                    abrirAlertAgregarPalabra = true
                                },
                                modifier = Modifier
                                    .width(300.dp)
                                    .height(56.dp)
                            ) { Text(text="Flashcard") }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    Toast.makeText(context, "se abre multple choice", Toast.LENGTH_SHORT).show()
                                    abrirAlertTipoTarjeta = false
                                },
                                modifier = Modifier
                                    .width(300.dp)
                                    .height(56.dp)
                            ) { Text(text="Multiple Choice") }

                    //botón para grabar audio
                            Button(onClick = {
                                abrirAlertTipoTarjeta = false
                                abrirAlertAgregarAudio = true // <-- Abre el nuevo diálogo de audio
                            }) { Text("Tarjeta de Audio (Pronunciación)") }

                        }


                    },
                    confirmButton = {

                    },
                    dismissButton = {
                        TextButton(onClick = {
                            abrirAlertTipoTarjeta = false
                        }) {
                            Text("Cancelar/Finalizar")
                        }
                    }
               )
            }


            if(abrirAlertAgregarPalabra){
                AlertDialog(
                    onDismissRequest = { abrirAlertAgregarPalabra = false },
                    title = { Text("NUEVA TARJETA") },
                    text= {
                        Column {
                            TextField(
                                value = preguntaFrente,
                                onValueChange = { preguntaFrente = it },
                                //label = { Text("Nueva Lista") },
                                placeholder = { Text("Pregunta (lado frontal)") },
                                modifier = Modifier
                                    .width(300.dp)
                                    .height(56.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = respuestaReverso,
                                onValueChange = { respuestaReverso = it },
                                //label = { Text("Nueva Lista") },
                                placeholder = { Text("Respuesta (lado reverso)") },
                                modifier = Modifier
                                    .width(300.dp)
                                    .height(56.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                    },
                    confirmButton = {
                        TextButton(onClick = {

                            if(idLista.isNullOrEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Error: idLista no válido",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@TextButton
                            }



                            var user = usuarioViewModel.getCurrentUser() ?: return@TextButton

                            if(preguntaFrente.isEmpty()){
                                Toast.makeText(context, "Debes agregar una pregunta", Toast.LENGTH_SHORT).show()
                                return@TextButton
                            }

                            if(respuestaReverso.isEmpty()){
                                Toast.makeText(context, "Debes agregar una respuesta", Toast.LENGTH_SHORT).show()
                                return@TextButton
                            }

                            var nuevaTarjetaFlashcard = Tarjeta(
                                preguntaFrente = preguntaFrente,
                                respuestaReverso = respuestaReverso,

                                idUsuario = user.uid,
                                idTema = idTemaActual,
                                idLista = idLista!!)

                            tarjetasViewModel.agergarTarjetaFlashcard(nuevaTarjetaFlashcard){ success, mensaje ->
                                if(success){
                                    Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                                    preguntaFrente = ""
                                    respuestaReverso = ""
                                }else{
                                    Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                                }
                            }

                            preguntaFrente = ""
                            respuestaReverso = ""

                        }) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            abrirAlertAgregarPalabra = false
                            abrirAlertTipoTarjeta = true
                        }) {
                            Text("Cancelar/Finalizar")
                        }
                    }
                )
            }

            if (AlerDiagBorrarPuntaje) {
                AlertDialog(
                    onDismissRequest = { AlerDiagBorrarPuntaje = false },
                    title = { Text("Confirmación") },
                    text = { Text("¿Estás seguro que quieres borrar el puntaje de todas las tarjetas?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                tarjetasViewModel.reiniciarTarjetas(listasTarjetas, user!!.uid)
                                listasViewModel.updateLevel(idListaActual, 1)
                                AlerDiagBorrarPuntaje = false
                            }
                        ) {
                            Text("Sí")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { AlerDiagBorrarPuntaje = false }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            // Nuevo AlertDialog para grabar y guardar audio
            if (abrirAlertAgregarAudio) {

                // Pega este bloque completo donde tienes la marca <caret>

                AlertDialog(
                    onDismissRequest = {
                        // Detener la grabación si el diálogo se cierra inesperadamente
                        try {
                            recorder?.stop()
                            recorder?.release()
                        } catch (e: Exception) {
                            Log.e("VentanaPalabras", "Error al cerrar el diálogo durante la grabación", e)
                        }
                        recorder = null
                        grabando = false
                        abrirAlertAgregarAudio = false
                    },
                    title = { Text("NUEVA TARJETA DE AUDIO") },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Campos de texto para la pregunta y respuesta
                            TextField(
                                value = preguntaFrente,
                                onValueChange = { preguntaFrente = it },
                                placeholder = { Text("Palabra o frase a pronunciar") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = respuestaReverso,
                                onValueChange = { respuestaReverso = it },
                                placeholder = { Text("Significado o traducción") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Botón para grabar/detener audio
                            Button(
                                onClick = {
                                    // 1. Comprobar permisos ANTES de intentar grabar
                                    val permissionCheckResult = ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO)
                                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                        // 2. Si ya tenemos permiso, ejecutamos la lógica de grabación
                                        if (!grabando) {
                                            // --- INICIAR GRABACIÓN ---
                                            val archivo = File(context.cacheDir, "audio_${System.currentTimeMillis()}.m4a")
                                            val mediaRecorder: MediaRecorder

                                            // Comprueba la versión del SDK de Android del dispositivo
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                // Si es Android 12 (API 31) o superior, usa el nuevo constructor
                                                mediaRecorder = MediaRecorder(context)
                                            } else {
                                                // Si es una versión anterior, usa el constructor antiguo (deprecated, pero necesario para compatibilidad)
                                                @Suppress("DEPRECATION")
                                                mediaRecorder = MediaRecorder()
                                            }

                                            // Bloque try-catch para configurar e iniciar la grabación de forma segura
                                            try {
                                                // Configura el MediaRecorder
                                                mediaRecorder.apply {
                                                    setAudioSource(MediaRecorder.AudioSource.MIC)
                                                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                                                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                                                    setOutputFile(archivo.absolutePath)
                                                }

                                                // Prepara e inicia la grabación
                                                mediaRecorder.prepare()
                                                mediaRecorder.start()

                                                // Actualiza el estado de la UI
                                                recorder = mediaRecorder // Guardamos la referencia para poder detenerlo
                                                grabando = true
                                                archivoAudio = archivo // Guardamos la referencia al archivo
                                                Toast.makeText(context, "Grabando...", Toast.LENGTH_SHORT).show()

                                            } catch (e: IOException) {
                                                // Captura cualquier error durante prepare() o start()
                                                Log.e("MediaRecorder", "Prepare() o start() falló", e)
                                                Toast.makeText(context, "Error al iniciar la grabación.", Toast.LENGTH_SHORT).show()
                                                // Limpia los recursos si falla
                                                recorder = null
                                                grabando = false
                                                archivoAudio = null
                                            }
                                        } else {
                                            // --- DETENER GRABACIÓN ---
                                            try {
                                                recorder?.stop()
                                                recorder?.release()
                                            } catch (e: Exception) {
                                                Log.e("MediaRecorder", "Error al detener la grabación", e)
                                            } finally {
                                                recorder = null
                                                grabando = false
                                                Toast.makeText(context, "Grabación detenida.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        // 3. Si no tenemos permiso, lo solicitamos
                                        permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (grabando) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = if (grabando) R.drawable.stop_icon else R.drawable.mic_icon), // Necesitarás iconos para 'stop' y 'mic'
                                    contentDescription = if (grabando) "Detener grabación" else "Grabar audio"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (grabando) "Detener" else "Grabar")
                            }

                            if (archivoAudio != null && !grabando) {
                                Text(
                                    "¡Audio grabado!",
                                    modifier = Modifier.padding(top = 8.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            // El botón solo se activa si los textos están completos y hay un audio grabado
                            enabled = preguntaFrente.isNotBlank() && respuestaReverso.isNotBlank() && archivoAudio != null && !grabando,
                            onClick = {
                                val userActual = usuarioViewModel.getCurrentUser()
                                val audioFile = archivoAudio

                                if (userActual != null && audioFile != null) {
                                    val nuevaTarjetaAudio = Tarjeta(
                                        preguntaFrente = preguntaFrente,
                                        respuestaReverso = respuestaReverso,
                                        idUsuario = userActual.uid,
                                        idTema = idTemaActual,
                                        idLista = idLista!!
                                        // Asignaremos la 'audioUrl' en el ViewModel después de subir el archivo
                                    )

                                    // Llama a la función del ViewModel para subir el audio y guardar la tarjeta
                                    tarjetasViewModel.agregarTarjetaConAudio(nuevaTarjetaAudio, audioFile) { success, mensaje ->
                                        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                                        if (success) {
                                            // Limpiar y cerrar solo si fue exitoso
                                            preguntaFrente = ""
                                            respuestaReverso = ""
                                            archivoAudio = null
                                            abrirAlertAgregarAudio = false
                                        }
                                    }
                                }
                            }
                        ) {
                            Text("Guardar Tarjeta")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            abrirAlertAgregarAudio = false
                        }) {
                            Text("Cancelar")
                        }
                    }
                )





            }



        }

    }


}

@Composable
fun ListaPalabras(listasPalabras: List<Tarjeta>, onClickPalabra: (Tarjeta) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .heightIn(max = 500.dp)
            .background(
                Color(0xFFEFEFEF), RoundedCornerShape(8.dp)
            ),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(listasPalabras) { palabra ->
            ListaItemPalabra(tarjeta = palabra, onClick = { onClickPalabra(palabra) })
        }
    }
}



@Composable
fun ListaItemPalabra(tarjeta: Tarjeta, onClick: () -> Unit) {

    val iconoRes = when{
        tarjeta.puntajeTarjeta < 0 -> R.drawable.rojo
        tarjeta.puntajeTarjeta in 0..4 -> R.drawable.plateado
        tarjeta.puntajeTarjeta  in 5..9 -> R.drawable.naranja
        else -> R.drawable.verde
    }


    Button(
        onClick = onClick ,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(text = tarjeta.preguntaFrente)
            Image(
                painter = painterResource(id = iconoRes),
                contentDescription = "Icono de lista",
                modifier = Modifier.size(24.dp)
            )
        }

    }


}