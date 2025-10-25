package com.example.memoryapplication.Ventanas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.memoryapplication.Rutas
import com.example.memoryapplication.ViewModels.UsuarioViewModel

@Composable
fun Configuracion(navController: NavHostController, usuarioViewModel: UsuarioViewModel) {

    var selectedOption by remember { mutableStateOf("Opción 1") }
    val options = listOf("Opción 1", "Opción 2", "Opción 3")
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Texto que actúa como ComboBox
        Text(
            text = selectedOption,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp)
        )

        // Menu desplegable simple
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate(Rutas.VentanaPrincipal) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
}
