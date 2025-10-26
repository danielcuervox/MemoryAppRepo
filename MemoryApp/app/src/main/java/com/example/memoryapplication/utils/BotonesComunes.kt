package com.example.memoryapplication.utils


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.memoryapplication.Rutas
import com.example.memoryapplication.ViewModels.UsuarioViewModel


@Composable
fun BottonBarMio(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel
) {
    NavigationBar(
        tonalElevation = 10.dp,
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        val items = listOf(
            BottomNavItem("Inicio", Icons.Filled.Home, Rutas.VentanaPrincipal),
            BottomNavItem("Perfil", Icons.Filled.Person, Rutas.VentanaPerfil),
            BottomNavItem("Ajustes", Icons.Filled.Settings, Rutas.VentanaConfiguracion)

        )

        items.forEach { item ->
            val currentRoute = navController.currentDestination?.route

            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(text = item.label)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

// Estructura de cada botón de navegación
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

