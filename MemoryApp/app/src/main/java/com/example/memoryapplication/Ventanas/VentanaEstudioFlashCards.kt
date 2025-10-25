package com.example.memoryapplication.Ventanas

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.memoryapplication.Modelo.Tarjeta
import com.example.memoryapplication.ViewModels.TarjetasViewModel
import com.example.memoryapplication.ViewModels.UsuarioViewModel
import com.example.memoryapplication.utils.BottonBarMio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudioFlashCards(navController: NavHostController,
                      usuarioViewModel: UsuarioViewModel,
                      idTema : String?,
                      idLista:String?) {

    val user = usuarioViewModel.getCurrentUser()
    val tarjetasViewModel: TarjetasViewModel = viewModel()
    val idTemaActual = idTema ?: ""
    val idListaActual = idLista ?: ""

    val listaTarjetas by tarjetasViewModel.listaTarjetas.collectAsState(initial = emptyList())

    // 3. State for controlling the study session
    var currentCardIndex by remember { mutableStateOf(0) }
    val currentCard = listaTarjetas.getOrNull(currentCardIndex)
    var isShowingAnswer by remember { mutableStateOf(false) } // New state

    LaunchedEffect(key1 = idTemaActual, idListaActual) {
        if (user != null && idTemaActual.isNotEmpty()) {
            tarjetasViewModel.cargarListasTarjetas(user.uid, idTemaActual, idListaActual)
        }
    }

    // Check if the list is empty
    val isListEmpty = listaTarjetas.isEmpty()
    val totalCards = listaTarjetas.size


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Estudio: Flashcards") }
            )
        },
        bottomBar = {
            BottonBarMio(navController = navController, usuarioViewModel = usuarioViewModel)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // Show Card Progress
            if (!isListEmpty) {
                Text(
                    text = "Tarjeta ${currentCardIndex + 1} de $totalCards",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Flashcard Display Area
            Box(
                modifier = Modifier
                    .weight(1f) // Takes up remaining space
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isListEmpty) {
                    Text(
                        "No hay tarjetas para estudiar en esta lista.",
                        style = MaterialTheme.typography.headlineSmall
                    )
                } else if (currentCard != null) {
                    FlashCardView(card = currentCard)
                }
            }


            // Navigation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Previous Button
                Button(
                    onClick = {
                        if (currentCardIndex > 0) currentCardIndex--
                    },
                    enabled = currentCardIndex > 0 && !isListEmpty,
                    modifier = Modifier.weight(1f).height(56.dp)
                ) {
                    Text("Anterior")
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Next Button
                Button(
                    onClick = {
                        if (currentCardIndex < totalCards - 1 ) {
                            currentCardIndex++

                        } else {
                            // Logic for when study session is finished
                            navController.popBackStack() // Example: go back to the previous screen
                        }
                    },
                    enabled = !isListEmpty,
                    modifier = Modifier.weight(1f).height(56.dp)
                ) {
                    Text(if (currentCardIndex < totalCards - 1) "Bien" else "Repasar")
                }


            }


        }


    }
}



@Composable
fun FlashCardView(card: Tarjeta) {

    var isFlipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "cardRotationAnimation"
    )

    val contentText = if (rotation <= 90f) card.preguntaFrente else card.respuestaReverso
    val cardColor = if (rotation <= 90f) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.7f)
            .graphicsLayer {
                // Apply the rotation effect
                rotationY = rotation
                // Optional: keep the card centered during rotation
                cameraDistance = 8 * density
            }
            .clickable {
                isFlipped = !isFlipped
            },
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Use another graphicsLayer to instantly flip the text content
            // once the card is past 90 degrees, ensuring the text reads correctly
            Text(
                text = contentText,
                style = MaterialTheme.typography.headlineMedium,
                color = if (rotation <= 90f) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.graphicsLayer {
                    rotationY = if (rotation > 90f) 180f else 0f
                }
            )
        }
    }
}

