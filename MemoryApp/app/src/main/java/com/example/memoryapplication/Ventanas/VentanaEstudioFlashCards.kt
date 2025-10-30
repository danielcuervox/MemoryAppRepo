package com.example.memoryapplication.Ventanas

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.memoryapplication.Modelo.Tarjeta
import com.example.memoryapplication.R
import com.example.memoryapplication.ViewModels.TarjetasViewModel
import com.example.memoryapplication.ViewModels.UsuarioViewModel
import com.example.memoryapplication.utils.BottonBarMio
import kotlin.math.log

// Definición de colores
val ColorSilverStart = Color(0xFFC0C0C0)
val ColorSilverEnd = Color(0xFFE0E0E0)
val ColorOrangeStart = Color(0xFFFFA500)
val ColorOrangeEnd = Color(0xFFFFCC66)
val ColorGreenStart = Color(0xFF00C853)
val ColorGreenEnd = Color(0xFF69F0AE)
val ColorInactive = Color(0x60808080) //

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudioFlashCards(navController: NavHostController,
                      usuarioViewModel: UsuarioViewModel,
                      idTema : String?,
                      idLista:String?) {

    val context = LocalContext.current
    val user = usuarioViewModel.getCurrentUser()
    val tarjetasViewModel: TarjetasViewModel = viewModel()
    val idTemaActual = idTema ?: ""
    val idListaActual = idLista ?: ""

    val listaTarjetas by tarjetasViewModel.listaTarjetas.collectAsState(initial = emptyList())

    var indiceListaTarjetas by remember {
        mutableStateOf(
            if (listaTarjetas.isNotEmpty()) (0 until listaTarjetas.size).random() else 0
        )
    }

    val tarjetaActual = listaTarjetas.getOrNull(indiceListaTarjetas)
    var tarjetaLadoPregunta by remember { mutableStateOf(false) }
    var avisoSiguienteActividad by remember { mutableStateOf(false) }
    var numeroDeActividadActual by remember { mutableStateOf(1) }


    LaunchedEffect(key1 = idTemaActual, idListaActual) {
        if (user != null && idTemaActual.isNotEmpty()) {
            tarjetasViewModel.cargarListasTarjetas(user.uid, idTemaActual, idListaActual)
        }
    }

    // Check if the list is empty
    val isListEmpty = listaTarjetas.isEmpty()

    var botónMalVisible by remember { mutableStateOf(false) }
    var actividadFinalizada by remember { mutableStateOf(false) }
    var contadorTarjetasPorEstudiar by remember {mutableStateOf(0)}


    val listaIndicesDisponibles = remember(listaTarjetas, numeroDeActividadActual) {
        (0 until listaTarjetas.size).toMutableList()
    }


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
                    text = "Tarjeta ${indiceListaTarjetas + 1} de totalCards",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Zona que muestra la tarjeta
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isListEmpty) {
                    Text(
                        "No hay tarjetas para estudiar en esta lista.",
                        style = MaterialTheme.typography.headlineSmall
                    )
                } else if (tarjetaActual != null) {
                    FlashCardView(card = tarjetaActual, tarjetaLadoPregunta, numeroDeActividadActual,  onFlipChange = { tarjetaLadoPregunta = it })

                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botón "Repasar / Mal"
                if (tarjetaLadoPregunta) {
                    Button(
                        onClick = {

                            //quita siempre y punto y si aún ahy indices de tarjetas por estudiar sigue repitiendo la segunda parte
                            // creo que sobra
                            tarjetaActual?.let{
                                tarjetaActual.puntajeTarjeta--
                                tarjetasViewModel.actualizarPuntajes(tarjetaActual, user!!.uid)
                            }


                            tarjetaLadoPregunta = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("Repasar")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Botón "Bien"
                    Button(
                        onClick = {

                            tarjetaActual?.let { tarjeta ->
                                //siempre suma la tarjeta
                                if(tarjetaActual.puntajeTarjeta < 10){
                                    tarjeta.puntajeTarjeta++
                                    tarjetasViewModel.actualizarPuntajes(tarjeta, user!!.uid)
                                }

                                //.2. verificiación de nivel
                                val objetivoNivel1 = tarjeta.puntajeTarjeta == 4 && numeroDeActividadActual == 1
                                val objetivoNivel2 = tarjeta.puntajeTarjeta == 8 && numeroDeActividadActual == 2
                                val objetivoNivel3 = tarjeta.puntajeTarjeta == 10 && numeroDeActividadActual == 3

                                if (objetivoNivel1 || objetivoNivel2 || objetivoNivel3) {
                                    listaIndicesDisponibles.remove(indiceListaTarjetas)
                                }

                                // 3. Decide qué hacer a continuación
                                if (listaIndicesDisponibles.isNotEmpty()) {
                                    // Si todavía quedan tarjetas, elige una nueva al azar.
                                    indiceListaTarjetas = listaIndicesDisponibles.random()
                                    tarjetaLadoPregunta = false // Voltea la nueva tarjeta al lado inicial
                                } else {
                                    // Si no quedan tarjetas, hemos terminado el nivel actual.
                                    actividadFinalizada = true

                                    if (numeroDeActividadActual == 1 || numeroDeActividadActual == 2 || numeroDeActividadActual == 3) {
                                        // Si estábamos en el nivel 1, mostramos el aviso para pasar al 2.
                                        avisoSiguienteActividad = true
                                    } else {
                                        // Si estábamos en el nivel 2, la sesión ha terminado completamente.
                                        // Aquí podrías mostrar un diálogo final de "¡Has completado todo!"
                                        // y luego navegar hacia atrás.
                                        // Por ahora, simplemente navegamos hacia atrás.
                                        navController.popBackStack()
                                    }
                                }

                            }

                            Toast.makeText(context, "PP:  $indiceListaTarjetas", Toast.LENGTH_SHORT).show()
                            Log.e("disponibles", "${listaIndicesDisponibles}")


                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("Bien")
                    }
                } else {
                    // Botón "Ver Respuesta"
                    Button(
                        onClick = { tarjetaLadoPregunta = true; botónMalVisible = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Ver Respuesta")
                    }
                }
            }

        }

    }

    if(avisoSiguienteActividad){

        AlertDialog(
            onDismissRequest = { },
            title = { Text("Felicidades") },
            confirmButton = {
                TextButton(onClick = {

                    if(numeroDeActividadActual == 1){
                        numeroDeActividadActual = 2
                    }else if(numeroDeActividadActual == 2){
                        numeroDeActividadActual = 3
                    }


                    actividadFinalizada = false
                    avisoSiguienteActividad = false


                    //se reestablecen los indices
                    //indiceListaTarjetas = listaIndicesDisponibles.random()

                }) {
                    Text("Siguiente Nivel")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    avisoSiguienteActividad = false
                    navController.popBackStack()
                }) {
                    Text("Terminar")
                }
            }
        )
    }
}




@Composable
fun FlashCardView(card: Tarjeta,
                  tarjetaEstaVolteada : Boolean,
                  numeroDeActividadActual : Int,
                  onFlipChange : (Boolean) -> Unit) {

    var isFlippedLocal by remember(card) { mutableStateOf(tarjetaEstaVolteada) }
    var actividadEscribir by remember { mutableStateOf(false) }
    var respuestaEscrita by remember { mutableStateOf(TextFieldValue("")) }


    // Cada vez que cambia tarjetaEstaVolteada, sincronizamos
    LaunchedEffect(tarjetaEstaVolteada) {
        isFlippedLocal = tarjetaEstaVolteada
    }

    val rotation by animateFloatAsState(
        targetValue = if (isFlippedLocal) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "cardRotationAnimation"
    )
    Log.e("actividad", "ACTUAL: $numeroDeActividadActual")

    var currentProgress = card.puntajeTarjeta

    var contentText : String = ""

    when (numeroDeActividadActual){

        1 -> contentText = if (isFlippedLocal) card.respuestaReverso else card.preguntaFrente
        2 -> contentText = if (isFlippedLocal) card.preguntaFrente else card.respuestaReverso
        3 -> {
            contentText = if (isFlippedLocal) card.preguntaFrente else card.respuestaReverso
            actividadEscribir = true
        }
    }

    val cardColor = if (isFlippedLocal) MaterialTheme.colorScheme.surfaceContainer
    else MaterialTheme.colorScheme.secondaryContainer

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.7f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8 * density
            }
            .clickable {
                isFlippedLocal = !isFlippedLocal
                onFlipChange(isFlippedLocal)
            },
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {


        if (numeroDeActividadActual == 3) {
            // --- LAYOUT PARA EL NIVEL 3 (con TextField) ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Texto de la pregunta
                Text(
                    text = contentText,
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (isFlippedLocal) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .graphicsLayer { rotationY = if (rotation > 90f) 180f else 0f }
                        .padding(bottom = 32.dp),
                    textAlign = TextAlign.Center
                )

                // TextField para la respuesta del usuario
                if (actividadEscribir && !isFlippedLocal) {
                    TextField(
                        value = respuestaEscrita,
                        onValueChange = { respuestaEscrita = it },
                        label = { Text("Escribe la respuesta") },
                        modifier = Modifier
                            .width(300.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Contenedor para la barra de progreso
                Box(modifier = Modifier.fillMaxSize()) {
                    if (!isFlippedLocal) {
                        GradientProgressBar(
                            progress = currentProgress,
                            // ... otros parámetros
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )
                    }
                }
            }
        }else{
            // --- LAYOUT PARA LOS NIVELES 1 y 2 (texto centrado) ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center // Esto centra el texto perfectamente
            ) {
                // Texto de la pregunta/respuesta
                Text(
                    text = contentText,
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (isFlippedLocal) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.graphicsLayer {
                        rotationY = if (rotation > 90f) 180f else 0f
                    },
                    textAlign = TextAlign.Center
                )

                // Barra de progreso (se alinea abajo a la derecha dentro del Box)
                if (!isFlippedLocal) {
                    GradientProgressBar(
                        progress = currentProgress,
                        // ... otros parámetros
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )
                }
            }
        }





//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally, // Centra los hijos horizontalmente
//            verticalArrangement = Arrangement.Center      // Centra el grupo de hijos verticalmente
//        ) {
//            Text(
//                text = contentText,
//                style = MaterialTheme.typography.headlineMedium,
//                color = if (isFlippedLocal) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
//                modifier = Modifier
//                    .graphicsLayer {
//                        rotationY = if (rotation > 90f) 180f else 0f
//                    }
//                    .padding(bottom = 32.dp), // Añade espacio entre el texto y el TextField
//                textAlign = TextAlign.Center
//            )
//
//            if(actividadEscribir && !isFlippedLocal){
//
//
//                TextField(
//                    value = respuestaEscrita,
//                    onValueChange = { respuestaEscrita = it },
//                    label = { Text("escribe la respuesta") },
//                    modifier = Modifier
//                        .width(300.dp)
//                        .height(56.dp),
//                    shape = RoundedCornerShape(8.dp)
//                )
//            }
//
//
//            Box(
//                modifier = Modifier
//                    .fillMaxSize() // Ocupa todo el espacio restante de la Column
//            ) {
//                if (!isFlippedLocal) {
//                    GradientProgressBar(
//                        progress = currentProgress,
//                        totalLevels = 10,
//                        barWidth = 8f,
//                        barHeight = 40f,
//                        modifier = Modifier.align(Alignment.BottomEnd) // Lo alineamos en la esquina inferior derecha
//                    )
//                }
//            }
//
//        }
    }
}

/**
 * Componente principal de la barra de progreso.
 * @param progress El valor de progreso actual (ej. 1 a 10).
 * @param totalLevels El número total de barras a mostrar (por defecto 10).
 */
@Composable
fun GradientProgressBar(
    progress: Int,
    totalLevels: Int = 10,
    barWidth: Float = 10f,
    barHeight: Float = 50f,
    modifier: Modifier = Modifier // <--- AÑADIDO ESTE PARÁMETRO
) {
    // Animación para el progreso si se desea una transición suave
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress.toFloat(),
            animationSpec = tween(durationMillis = 500)
        )
    }

    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
            .wrapContentSize()
    ) {
        // Crear las barras individuales
        for (i in 1..totalLevels) {
            GradientRoundedBar(
                level = i,
                totalLevels = totalLevels,
                isActive = i <= animatedProgress.value.toInt(), // Activa si es menor o igual al progreso animado
                barWidth = barWidth,
                barHeight = barHeight
            )
        }
    }
}

@Composable
fun GradientRoundedBar(
    level: Int,
    totalLevels: Int,
    isActive: Boolean,
    barWidth: Float,
    barHeight: Float,
) {
    // 1. Determinar el pincel de degradado (Brush)
    // Usamos remember para inicializar brush y asegurar que siempre tenga un valor.
    val brush: Brush by remember(level, isActive) {
        val colorStart: Color
        val colorEnd: Color

        when (level) {
            in 1..4 -> { // Plata
                colorStart = ColorSilverStart
                colorEnd = ColorSilverEnd
            }
            in 5..8 -> { // Naranja
                colorStart = ColorOrangeStart
                colorEnd = ColorOrangeEnd
            }
            in 9..10 -> { // Verde
                colorStart = ColorGreenStart
                colorEnd = ColorGreenEnd
            }
            else -> {
                colorStart = ColorInactive
                colorEnd = ColorInactive
            }
        }

        val effectiveBrush = if (isActive) {
            Brush.verticalGradient(
                colors = listOf(colorStart, colorEnd),
            )
        } else {
            // Usar un color inactivo si la barra no está activa
            Brush.verticalGradient(
                colors = listOf(ColorInactive, ColorInactive)
            )
        }
        mutableStateOf(effectiveBrush) // Retorna el Brush envuelto en un MutableState
    }

    // 2. Calcular la altura de la barra. (Tu código de escalado)
    val minHeightRatio = 0.2f
    val heightScale = (barHeight * (1f - minHeightRatio)) / (totalLevels - 1)
    val currentHeight = barHeight * minHeightRatio + heightScale * (level - 1)

    // 3. Dibujar en un Canvas
    Box(
        modifier = Modifier
            .width(barWidth.dp)
            .height(barHeight.dp)
            .padding(horizontal = 1.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter)
        ) {
            // Conversiones .toPx() que corrigieron el error anterior
            val rectSize = Size(
                width = barWidth.dp.toPx(),
                height = currentHeight.dp.toPx()
            )

            val cornerRadius = barWidth.dp.toPx() / 2f

            drawRoundRect(
                brush = brush, // Usa la variable brush asegurada por remember
                topLeft = Offset(x = 0f, y = size.height - rectSize.height),
                size = rectSize,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
            )
        }
    }
}
