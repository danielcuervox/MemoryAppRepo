package com.example.memoryapplication.Ventanas

import android.widget.Toast
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.memoryapplication.Modelo.Lista
import com.example.memoryapplication.Modelo.Tarjeta
import com.example.memoryapplication.R
import com.example.memoryapplication.ViewModels.ListasViewModel
import com.example.memoryapplication.ViewModels.TarjetasViewModel
import com.example.memoryapplication.ViewModels.UsuarioViewModel
import com.example.memoryapplication.utils.BottonBarMio

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
    val listasTarjetas by tarjetasViewModel.listaTarjetas.collectAsState(initial = emptyList())

    LaunchedEffect(key1 = idTemaActual, idListaActual) {
        if (user != null && idTemaActual.isNotEmpty()) {
            tarjetasViewModel.cargarListasTarjetas(user.uid, idTemaActual, idListaActual)
        }
    }

    var abrirAlertAgregarPalabra by remember { mutableStateOf(false) }
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
                        }


                    },
                    confirmButton = {
                        TextButton(onClick = {
                            abrirAlertTipoTarjeta = false

                        }) {
                            Text("Aceptar")
                        }
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