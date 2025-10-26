package com.example.memoryapplication

import android.os.Bundle
import android.view.WindowManager

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.memoryapplication.Ventanas.Configuracion
import com.example.memoryapplication.Ventanas.EstudioFlashCards
import com.example.memoryapplication.Ventanas.LogIng
import com.example.memoryapplication.Ventanas.NuevaLista
import com.example.memoryapplication.Ventanas.NuevoTema
import com.example.memoryapplication.Ventanas.Perfil
import com.example.memoryapplication.Ventanas.Principal
import com.example.memoryapplication.Ventanas.Registro
import com.example.memoryapplication.Ventanas.VentanaPalabras
import com.example.memoryapplication.ViewModels.UsuarioViewModel
import com.example.memoryapplication.ViewModels.ViewModel
import com.example.memoryapplication.ui.theme.MemoryApplicationTheme
import kotlin.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )

        enableEdgeToEdge()
        setContent {
            val viewModel: ViewModel = viewModel()
            val usuarioViewModel: UsuarioViewModel by viewModels() //private ???

            MemoryApplicationTheme {

                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Rutas.VentanaLogIng){
                    composable(Rutas.VentanaLogIng){
                        LogIng(navController, usuarioViewModel)
                    }
                    composable(Rutas.VentanaRegistro){
                        Registro(navController, usuarioViewModel , viewModel)
                    }
                    composable(Rutas.VentanaPrincipal){
                        Principal(navController, usuarioViewModel)
                    }
                    composable(Rutas.VentanaAgregarTema){
                        NuevoTema(navController, usuarioViewModel)
                    }
//                    composable(Rutas.VentanaAgregarLista){
//                        NuevaLista(navController, usuarioViewModel)
//                    }
                    composable("nuevaLista/{idTema}") { backStackEntry ->
                        val idTema = backStackEntry.arguments?.getString("idTema")
                        NuevaLista(navController, usuarioViewModel, idTema)
                    }
                    composable("VentanaPalabras/{idTema}/{idLista}") { backStackEntry ->
                        val idTema = backStackEntry.arguments?.getString("idTema")
                        val idLista = backStackEntry.arguments?.getString("idLista")
                        VentanaPalabras(navController, usuarioViewModel, idTema, idLista)
                    }
                    composable("VentanaEstudioFlashCards/{idTema}/{idLista}") { backStackEntry ->
                        val idTema = backStackEntry.arguments?.getString("idTema")
                        val idLista = backStackEntry.arguments?.getString("idLista")
                        EstudioFlashCards(navController, usuarioViewModel, idTema, idLista)
                    }
                    composable(Rutas.VentanaPerfil){
                        Perfil(navController, usuarioViewModel)
                    }
                    composable(Rutas.VentanaConfiguracion){
                        Configuracion(navController, usuarioViewModel)
                    }
                }
            }
        }



    }
}
