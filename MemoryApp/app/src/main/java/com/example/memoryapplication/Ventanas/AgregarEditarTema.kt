package com.example.memoryapplication.Ventanas

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.memoryapplication.Modelo.Tema
import com.example.memoryapplication.ViewModels.TemaViewModel
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import com.example.memoryapplication.ViewModels.UsuarioViewModel
import com.example.memoryapplication.utils.BottonBarMio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoTema(navController: NavHostController, usuarioViewModel: UsuarioViewModel) {

    val user = usuarioViewModel.getCurrentUser() ?: return
    val temaViewModel: TemaViewModel = viewModel()
    var iconoSelec by remember { mutableStateOf("\uD83D\uDCDA") }
    var nombreNuevoTema by remember { mutableStateOf("") }
    val context = LocalContext.current
    var abrirAlertIconos by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Agregar Tema") },
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

            //ícono grande con un botón para cambiarlo
            IconBox(iconoSelec, abrirAlertIconos = abrirAlertIconos, onClickCambiar = {abrirAlertIconos = true})

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = nombreNuevoTema,
                onValueChange = { nombreNuevoTema = it },
                label = { Text("Nuevo Tema") },
                modifier = Modifier
                    .width(300.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if(nombreNuevoTema.isBlank()){
                        Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    var nuevoTema = Tema(nombreTema= nombreNuevoTema, icono = iconoSelec, idUsuario = user.uid)

                    user.let{

                        temaViewModel.agregarNuevoTema(nuevoTema){ success, mensaje ->
                            if(success){
                                Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                                //volver a la ventana anterior
                                navController.popBackStack()
                            }else{
                                Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                }
            )
            {
                Text("Guardar")
            }


            if(abrirAlertIconos){
                AlertDialog(
                    onDismissRequest = { abrirAlertIconos = false },
                    title = { Text("Seleciona un ícono") },
                    text = {
                        CuadriculaBotones(onClickSeleccion = {
                            iconoSelec = it
                            abrirAlertIconos = false
                        })
                    },
                    confirmButton = {
                    },
                    dismissButton = {
                        TextButton(onClick = { abrirAlertIconos = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

        }

    }





}

@Composable
fun IconBox(icono: String, abrirAlertIconos: Boolean, onClickCambiar: () -> Unit) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth(0.5f) // 1/2 del ancho de la pantalla
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        // Usamos Column para que icono y botón queden verticalmente
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Text(text = icono, fontSize = 50.sp) // icono grande
            Button(
                onClick = {
                    onClickCambiar()
                    Toast.makeText(context, "Cambiar ícono", Toast.LENGTH_SHORT).show()
                }
            ) {
                Text("Cambiar ícono")
            }
        }
    }
}

@Composable
fun CuadriculaBotones(onClickSeleccion: (String) -> Unit) {

        val iconos = listOf(
        "\uD83D\uDCDA", // libro 📚
        "\uD83C\uDDF0\uD83C\uDDF7", //bandera uk
        "\uD83C\uDDE9\uD83C\uDDF0", // bandera de España 🇪🇸
        "\uD83C\uDDEA\uD83C\uDDF8", // España 🇪🇸
        "\uD83C\uDDE9\uD83C\uDDEA", // Alemania 🇩🇪
        "\uD83C\uDDEC\uD83C\uDDE7",  // Reino Unido 🇬🇧
        "\uD83C\uDDEB\uD83C\uDDF7", // Francia 🇫🇷
        "\uD83D\uDD22", // números 🔢
        "\uD83D\uDD2C", // microscopio 🔬
        "\uD83D\uDDE3\uFE0F", // mapa 🗣️
        "\uD83D\uDC68\u200D\uD83C\uDF93", // profesor 👨‍🎓
        "\uD83C\uDF10", // globo terráqueo 🌐
        "\uD83C\uDF0D", // mundo 🌍
        "\uD83D\uDD17", // enlace 🔗 (para conexiones)
        "\uD83D\uDC68\u200D\uD83D\uDCBB", // persona frente a computadora 👨‍💻
        "\uD83D\uDCC5", // calendario 📅
        "\uD83C\uDF93", // birrete 🎓
        "\uD83D\uDD27", // herramientas 🔧
        "\uD83D\uDD2D", // lupa 🔍
        "\uD83D\uDCD6", // libro abierto 📖
        "\uD83C\uDFDB" // Edificio clásico / museo
    )

    androidx.compose.foundation.layout.Column {
        iconos.chunked(3).forEach { fila ->
            androidx.compose.foundation.layout.Row {
                fila.forEach { ic ->
                    Button(
                        onClick = { onClickSeleccion(ic) }, // llama al lambda del padre
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(1f)
                    ) {
                        Text(ic, fontSize = 30.sp)
                    }
                }
            }
        }
    }

}