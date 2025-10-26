package com.example.memoryapplication.Ventanas

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.memoryapplication.ViewModels.UsuarioViewModel

@Composable
fun Perfil(navController: NavHostController, usuarioViewModel: UsuarioViewModel) {

    Text(text = "esta es la ventana perfil")
}
