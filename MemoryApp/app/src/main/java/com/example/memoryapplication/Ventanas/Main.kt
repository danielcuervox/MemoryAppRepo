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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.memoryapplication.Modelo.Tema
import com.example.memoryapplication.R
import com.example.memoryapplication.Rutas
import com.example.memoryapplication.ViewModels.UsuarioViewModel
import com.example.memoryapplication.utils.BottonBarMio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Principal(navController: NavHostController, usuarioViewModel: UsuarioViewModel) {

    val context = LocalContext.current
    val user = usuarioViewModel.getCurrentUser()
    var abrirAlertDiagogNuevoTema by remember { mutableStateOf(false) }
    //val temasPrueba by usuarioViewModel.temas.collectAsState(initial = emptyList())
//    val user2 by usuarioViewModel.usuarioActual.collectAsState()

    var nombreTema by remember { mutableStateOf("") }
    var nombreLista = usuarioViewModel.nombreLista.collectAsState().value    //var nombreLista by usuarioViewModel.nombreTema.collectAsState()

    val temasPrueba by usuarioViewModel.temasPrueba.collectAsState(initial = emptyList())
    val temas by usuarioViewModel.temas.collectAsState(initial = emptyList())


    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Tus Temas") },
            )
        },
        bottomBar = {
            BottonBarMio(
                navController = navController,
                usuarioViewModel = usuarioViewModel
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            user?.let {
                Text(
                    text = "Bienvenido ${it.displayName ?: it.email ?: "Usuario" }",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            } ?: Text(
                text = "No se encontró información del usuario",
                style = MaterialTheme.typography.bodyMedium
            )



        //lazy column con la lista de temas

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){

            Text(
                text = "Selecciona un tema de estudio",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            ListaTemas(
                temas = temas,
                onClickTema = { tema ->
                    navController.navigate("nuevaLista/${tema.idTema}")
                    //Toast.makeText(context, "click en el tema $tema", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))



            Button(
                onClick = {
                    Toast.makeText(context, "Agregar Nuevo Tema", Toast.LENGTH_SHORT).show()
                    navController.navigate(Rutas.VentanaAgregarTema)
                },

                modifier = Modifier
                    .height(50.dp)
                    .width(200.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row (
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Nuevo Tema")
                    //Spacer(modifier = Modifier.width(15.dp))
                    Image(
                        modifier = Modifier.size(150.dp),
                        painter = painterResource(id = R.drawable.add),
                        contentDescription = "Nuevo Tema"
                    )
                }
            }


        }

        }

    }

}

@Composable
fun ListaTemas(temas: List<Tema>, onClickTema: (Tema) -> Unit) {

    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 500.dp)
            .background(Color(0xFFEFEFEF),RoundedCornerShape(8.dp)
            ) ,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),

    ) {
        items(temas) { tema ->
            TemaItem(tema = tema, onClick = {
                onClickTema(tema)
            })
        }
    }}


@Composable
fun TemaItem(tema: Tema, onClick: () -> Unit) {
    Button(
        onClick = onClick ,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        Row(){
            Text(text = tema.icono)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = tema.nombreTema)
        }

    }
}

