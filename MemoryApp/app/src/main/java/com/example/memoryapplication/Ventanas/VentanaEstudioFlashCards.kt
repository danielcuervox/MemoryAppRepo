package com.example.memoryapplication.Ventanas

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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.memoryapplication.Modelo.Tarjeta
import com.example.memoryapplication.R
import com.example.memoryapplication.ViewModels.TarjetasViewModel
import com.example.memoryapplication.ViewModels.UsuarioViewModel
import com.example.memoryapplication.utils.BottonBarMio

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
    //var isShowingAnswer by remember { mutableStateOf(false) } // New state
    var tarjetaLadoPregunta by remember { mutableStateOf(false) }
    var avisoSiguienteActividad by remember { mutableStateOf(false) }


    LaunchedEffect(key1 = idTemaActual, idListaActual) {
        if (user != null && idTemaActual.isNotEmpty()) {
            tarjetasViewModel.cargarListasTarjetas(user.uid, idTemaActual, idListaActual)
        }
    }

    // Check if the list is empty
    val isListEmpty = listaTarjetas.isEmpty()
    val totalCards = listaTarjetas.size
    var botónMalVisible by remember { mutableStateOf(false) }
    var actividadFinalizada by remember { mutableStateOf(false) }
    var contadorTarjetasPorEstudiar by remember {mutableStateOf(0)}


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
                    text = "Tarjeta ${indiceListaTarjetas + 1} de $totalCards",
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
                    FlashCardView(card = tarjetaActual, tarjetaLadoPregunta, onFlipChange = { tarjetaLadoPregunta = it })
//
//                    val progreso = tarjetaActual.puntajeTarjeta.toFloat() / 10f
//                    Row(){
//
//                        Box(
//                            modifier = Modifier
//                                .height(200.dp) // altura de la barra
//                                .width(24.dp)  // ancho de la barra
//                        ){
//
//                            LinearProgressIndicator(
//                                progress = { progreso },
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(vertical = 8.dp)
//                                    .rotate(-90f),
//                                color = MaterialTheme.colorScheme.primary,
//                                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
//                                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
//                            )
//
//                        }
//                    }
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

                            tarjetaActual?.let{
                                tarjetaActual.puntajeTarjeta--
                                tarjetasViewModel.actualizarPuntajes(tarjetaActual, user!!.uid)
                            }

                            if (indiceListaTarjetas < totalCards - 1) {
                                //en el caso de que se equivoque repite la misma pregunta
                                tarjetaLadoPregunta = false
                                //botónMalVisible = false
                            } else {
                                navController.popBackStack()
                            }
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
                            //se evalúa el número de tarjetas que han llegado a puntaje 4
                            contadorTarjetasPorEstudiar = 0

                            for(i in listaTarjetas){
                                if(i.puntajeTarjeta == 4){
                                    contadorTarjetasPorEstudiar++
                                }
                            }

                            if(contadorTarjetasPorEstudiar == listaTarjetas.size-1){
                                actividadFinalizada = true
                                avisoSiguienteActividad = true
                            }

//                            if(tarjetaActual!!.puntajeTarjeta < 4){
//                                tarjetaActual.puntajeTarjeta++
//                                tarjetasViewModel.actualizarPuntajes(tarjetaActual, user!!.uid)
//                            }

                            tarjetaActual?.let { tarjeta ->
                                if (tarjeta.puntajeTarjeta < 4) {
                                    tarjeta.puntajeTarjeta++
                                    tarjetasViewModel.actualizarPuntajes(tarjeta, user!!.uid)
                                }
                            }

                            if (!actividadFinalizada) {
                                //indiceListaTarjetas++
                                var indiceRandom = (0..totalCards-1).random()

                                indiceListaTarjetas = indiceRandom
                                //listaTarjetas[indiceRandom]

                                tarjetaLadoPregunta = false
                                //botónMalVisible = false
                            }

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
                TextButton(onClick = { avisoSiguienteActividad = false }) {
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
fun FlashCardView(card: Tarjeta, tarjetaEstaVolteada : Boolean, onFlipChange : (Boolean) -> Unit) {

    var isFlippedLocal by remember(card) { mutableStateOf(tarjetaEstaVolteada) }

    // Cada vez que cambia tarjetaEstaVolteada, sincronizamos
    LaunchedEffect(tarjetaEstaVolteada) {
        isFlippedLocal = tarjetaEstaVolteada
    }

    val rotation by animateFloatAsState(
        targetValue = if (isFlippedLocal) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "cardRotationAnimation"
    )


    //val contentText = if (tarjetaEstaVolteada) card.respuestaReverso else card.preguntaFrente
    //val cardColor = if (tarjetaEstaVolteada) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer

//    val currentProgress = when{
//        card.puntajeTarjeta < 0 -> R.drawable.rojo
//        card.puntajeTarjeta == 1 -> R.drawable.plateado1
//        card.puntajeTarjeta == 2 -> R.drawable.plateado2
//        card.puntajeTarjeta == 3 -> R.drawable.plateado3
//        card.puntajeTarjeta == 4 -> R.drawable.plateado4
//        card.puntajeTarjeta  in 5..9 -> R.drawable.naranja
//        else -> R.drawable.verde
//    }

    var currentProgress = card.puntajeTarjeta

    val contentText = if (isFlippedLocal) card.respuestaReverso else card.preguntaFrente
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center // <--- contentAlignment es correcto aquí.
        ) {
            Text(
                text = contentText,
                style = MaterialTheme.typography.headlineMedium,
                color = if (isFlippedLocal) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.graphicsLayer {
                    rotationY = if (rotation > 90f) 180f else 0f
                }
            )
            // Reemplazo de Image con el componente GradientProgressBar
            GradientProgressBar(
                progress = currentProgress,
                totalLevels = 10,
                barWidth = 8f,
                barHeight = 40f,
                // ¡CORRECCIÓN CLAVE! Pasar .align(Alignment.BottomEnd) al modificador del componente
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
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
