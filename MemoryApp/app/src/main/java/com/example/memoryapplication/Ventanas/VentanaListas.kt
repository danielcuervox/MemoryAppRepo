package com.example.memoryapplication.Ventanas

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.memoryapplication.ViewModels.UsuarioViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.memoryapplication.Modelo.Lista
import com.example.memoryapplication.R
import com.example.memoryapplication.ViewModels.ListasViewModel
import com.example.memoryapplication.utils.BottonBarMio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaLista(navController: NavHostController, usuarioViewModel: UsuarioViewModel, idTema:String?) {


    val context = LocalContext.current
    //val user = usuarioViewModel.getCurrentUser()
    val idTemaFinal = idTema ?: ""
    val user = usuarioViewModel.getCurrentUser()

    var nuevaListaNombre by remember { mutableStateOf("") }
    val listasViewModel: ListasViewModel = viewModel()

    val listaDeListas by listasViewModel.listaDeListas.collectAsState(initial = emptyList())
    val litasPrueba by usuarioViewModel.listasPrueba.collectAsState(initial = emptyList())

    var abrirAlertAgregarLista by remember { mutableStateOf(false) }



    LaunchedEffect(key1 = idTemaFinal, ) {
        if (user != null && idTemaFinal.isNotEmpty()) {
            listasViewModel.cargarListasUsuarioYTema(user.uid, idTemaFinal)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Tus listas del tema") },
            )
        },
        bottomBar = {
            BottonBarMio(
                navController = navController,
                usuarioViewModel = usuarioViewModel
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    abrirAlertAgregarLista = true
                },
                shape = RoundedCornerShape(50.dp), // redondo
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_float_button),
                    contentDescription = "Nueva Lista",
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


            if(listaDeListas.isEmpty()){
                Text("No hay listas para mostrar")
                Spacer(modifier = Modifier.height(16.dp))
                Text("Crea una lista para empezar a aprender")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    abrirAlertAgregarLista = true
                }){
                    Text("Crear lista")
                }
            }else{
                ListaDeLista(
                    listas = listaDeListas,
                    onClickLista = { lista ->
                        navController.navigate("VentanaPalabras/${idTema}/${lista.idLista}")
                    }
                )
            }


            if(abrirAlertAgregarLista){
                AlertDialog(
                    onDismissRequest = { abrirAlertAgregarLista = false },
                    title = { Text("Escribe el nombre de la nueva lista") },
                    text= {
                        Column {
                            TextField(
                                value = nuevaListaNombre,
                                onValueChange = { nuevaListaNombre = it },
                                //label = { Text("Nueva Lista") },
                                placeholder = { Text("Escribe aquí...") },
                                modifier = Modifier
                                    .width(300.dp)
                                    .height(56.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                    },
                    confirmButton = {
                        TextButton(onClick = {

                            if (idTema.isNullOrEmpty()) {
                                Toast.makeText(context, "Error: idTema no válido", Toast.LENGTH_SHORT).show()
                                return@TextButton
                            }

                            var user = usuarioViewModel.getCurrentUser() ?: return@TextButton


                            var nuevaListaObj = Lista(nombreLista = nuevaListaNombre, idUsuario = user.uid, idTema = idTema!!)

                            Toast.makeText(context, "idUsuario ${user.uid} idTema ${idTema}", Toast.LENGTH_SHORT).show()
                            listasViewModel.agregarLista(nuevaListaObj){ success, mensaje ->
                                if(success){

                                    Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                                    //navController.navigate(Rutas.VentanaPrincipal)
                                    nuevaListaNombre = ""
                                }else{
                                    Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                                }
                            }


                            abrirAlertAgregarLista = false
                            nuevaListaNombre = ""
                        }) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { abrirAlertAgregarLista = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

        }

    }

}



@Composable
fun ListaDeLista(listas: List<Lista>, onClickLista: (Lista) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .heightIn(max = 100.dp)
            .background(Color(0xFFEFEFEF),RoundedCornerShape(8.dp)
            ),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(listas) { lista ->
            ListaItem(lista = lista, onClick = { onClickLista(lista) })
        }
    }

}

@Composable
fun ListaItem(lista: Lista, onClick: () -> Unit) {

    val iconoRes = when{
        lista.puntuacion < 0 -> R.drawable.rojo
        lista.puntuacion == 1 -> R.drawable.plateado
        lista.puntuacion == 2 -> R.drawable.naranja
        lista.puntuacion  == 3 -> R.drawable.verde
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
            Text(text = lista.nombreLista)
            Image(
                painter = painterResource(id = iconoRes),
                contentDescription = "Icono de lista",
                modifier = Modifier.size(24.dp)
            )
        }

    }


}