package com.example.memoryapplication.Ventanas

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.memoryapplication.Modelo.Usuario
import com.example.memoryapplication.Rutas
import com.example.memoryapplication.ViewModels.UsuarioViewModel
import com.example.memoryapplication.ViewModels.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registro(navController: NavHostController, usuarioViewModel: UsuarioViewModel, viewModel: ViewModel) {
    val context = LocalContext.current

    val usuarioPendiente by usuarioViewModel.usuarioPendiente.collectAsState()

    //Variables locales para los campos de texto
    var nombreUsuario by remember { mutableStateOf(TextFieldValue(usuarioPendiente?.nombreUsuario ?: "")) }
    var contrasena by remember { mutableStateOf(TextFieldValue("")) }
    var confirContrasena by remember { mutableStateOf(TextFieldValue("")) }
    var nombre by remember { mutableStateOf(TextFieldValue(usuarioPendiente?.nombre ?: "")) }
    var apellidos by remember { mutableStateOf(TextFieldValue(usuarioPendiente?.apellido ?: "")) }
    var email by remember { mutableStateOf(TextFieldValue(usuarioPendiente?.email ?: "")) }
    var selectSexo by remember { mutableStateOf(usuarioPendiente?.sexo ?: "M") }
    var fechaNacimiento by remember { mutableStateOf<Date?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    //para abrir el DataPicker
    var openDialog by remember { mutableStateOf(false) }

    //Calcula la fecha mínima permitida (para mayores de 6 años)
    val fechaActual = Calendar.getInstance()
    fechaActual.add(Calendar.YEAR, -6)
    val fechaMin = fechaActual.time

    val esRegistroGoogle = usuarioPendiente != null

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val registroValido = nombre.text.isNotBlank() &&
            apellidos.text.isNotBlank() &&
            selectSexo.isNotBlank() &&
            fechaNacimiento != null &&
            contrasena == confirContrasena &&
            (esRegistroGoogle || (nombreUsuario.text.isNotBlank() && contrasena.text.isNotBlank() && email.text.isNotBlank()))


    Box(
        modifier = Modifier
            .fillMaxSize(), // ocupa toda la pantalla
        contentAlignment = Alignment.Center // centra el contenido dentro del Box
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // centra horizontalmente
            verticalArrangement = Arrangement.Center // centra verticalmente
        ) {

            //Campos para registro manual

            //var password by remember { mutableStateOf(TextFieldValue("")) }

            Box(
                modifier = Modifier
                    .size(100.dp) // tamaño del círculo
                    .clip(CircleShape) // hace el círculo
                    .background(Color.Gray) // color de fondo si no hay imagen
                    .clickable { launcher.launch("image/*") }, // abre la galería al tocar
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Imagen de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop // recorta para llenar el círculo
                    )
                } else {
                    Text(
                        text = "Subir",
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            TextField(
                value = nombreUsuario,
                onValueChange = { nombreUsuario = it },
                label = { Text("Nombre de Usuario") },
                modifier = Modifier
                    .width(300.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
            )
            Spacer(modifier = Modifier.height(6.dp))

            TextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                //tipo contraseña
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .width(300.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
            )

            Spacer(Modifier.height(6.dp))

            TextField(
                value = confirContrasena,
                onValueChange = { confirContrasena = it },
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .width(300.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
            )

            Spacer(modifier = Modifier.height(6.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("e-mail") },
                modifier = Modifier
                    .width(300.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))

            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier
                    .width(300.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            TextField(
                value = apellidos,
                onValueChange = { apellidos = it },
                label = { Text("Apellidos") },
                modifier = Modifier
                    .width(300.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center){
                Text(text = "Sexo")
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // RadioButton para Masculino
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectSexo == "M",
                            onClick = { selectSexo = "M" }
                        )
                        Text(text = "M")
                    }

                    // RadioButton para Femenino
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectSexo == "F",
                            onClick = { selectSexo = "F" }
                        )
                        Text(text = "F")
                    }
                }



            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

                Text(text = "Nacimiento: ")
                Button(onClick = { openDialog = true }) {
                    Text(
                        text = if (fechaNacimiento != null)
                            dateFormatter.format(fechaNacimiento!!)
                        else
                            "Seleccionar fecha"
                    )
                }
            }

            if (openDialog) {
                val datePickerState = rememberDatePickerState()

                DatePickerDialog(
                    onDismissRequest = { openDialog = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    fechaNacimiento = Date(it)
                                }
                                openDialog = false
                            }
                        ) { Text("Aceptar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { openDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

    //-- botón de registro
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)){

                Button(
                    onClick = {
                        // validación de email
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.trim()).matches()) {
                            Toast.makeText(context, "Por favor, introduce un correo electrónico válido.", Toast.LENGTH_LONG).show()
                            return@Button
                        }

                        // validación de campos vacios y de fecha de nacimiento
                        if (fechaNacimiento == null || fechaNacimiento!! > fechaMin) {
                            Toast.makeText(context, "Debes tener al menos 6 años para registrarte.", Toast.LENGTH_LONG).show()
                            return@Button
                        }

                        val nuevoUsuario = Usuario(
                            id = usuarioPendiente?.id,
                            nombre = nombre.text,
                            apellido = apellidos.text,
                            nombreUsuario = nombreUsuario.text,
                            contrasenia = contrasena.text,
                            fechaNacimiento = SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.getDefault()
                            ).format(fechaNacimiento!!),
                            sexo = selectSexo,
                            email = email.text,
                            rol = 0,
                            fotoPerfil = "")

                        usuarioViewModel.registrarUsuario(nuevoUsuario){ success, mensaje ->
                            if(success){
                                Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                                navController.navigate(Rutas.VentanaPrincipal)
                            }else{
                                Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                            }
                        }
                              },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .height(50.dp),
                    enabled = registroValido,
                            //&& (profileImageUri != Uri.EMPTY) && !isLoading && !isUploading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Registrar")
                }
            }



        }
    }


}

