package com.example.memoryapplication.Ventanas

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.memoryapplication.R
import com.example.memoryapplication.Rutas
import com.example.memoryapplication.ViewModels.UsuarioViewModel
import com.example.memoryapplication.utils.getGoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider


@Composable
fun LogIng(navController: NavHostController, usuarioViewModel: UsuarioViewModel) {

    val context = LocalContext.current
    val isLoading by usuarioViewModel.isLoading.collectAsState()

    val activity = context as Activity
    val googleSignInClient = remember { getGoogleSignInClient(context) }

    // Lanza el intent de Google Sign-In
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            usuarioViewModel.loginWithGoogleCredential(credential) { success ->
                if (success) {
                    Toast.makeText(context, "Inicio de sesión exitoso ✅", Toast.LENGTH_SHORT).show()
                    navController.navigate(Rutas.VentanaPrincipal)
                } else {
                    Toast.makeText(context, "Error en el inicio de sesión ❌", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "Error: ${e.statusCode}", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // centra horizontalmente
            verticalArrangement = Arrangement.Center // centra verticalmente
        ) {
            Text(text = "Bienvenido MemoryApp", style = MaterialTheme.typography.titleLarge)



            var nombreUsuario by remember { mutableStateOf(TextFieldValue("")) }
            var password by remember { mutableStateOf(TextFieldValue("")) }

            TextField(
                value = nombreUsuario,
                onValueChange = { nombreUsuario = it },
                placeholder = { Text("Nombre de Usuario") },
                modifier = Modifier
                    .width(300.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Contraseña") },
                modifier = Modifier
                    .width(300.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
            )


            Button(
                onClick = {
                    if (nombreUsuario.text.isNotBlank() && password.text.isNotBlank()) {
                        // ------>>>>>>usuarioViewModel.loginWithUsername(email.text, password.text)
                        usuarioViewModel.loginWithUsername(nombreUsuario.text, password.text){ success, mensaje ->
                            if(success){
                                Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                                if(success){
                                    navController.navigate(Rutas.VentanaPrincipal)
                                }

                            }else{
                                Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Por favor, ingresa un correo y contraseña", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Iniciar Sesión")
                }
            }



            Button(
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,   //centrar una fila
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Image(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Actividades"
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Iniciar sesión con Google")
                }

            }


            Row (){
                TextButton(
                    onClick = {
                        Toast.makeText(context, "ir a recuperar contraseña", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("¿Olvidaste tu contraseña?")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        navController.navigate(Rutas.VentanaRegistro)
                    },
                ) {

                    Text("Sign Up")
                }
            }



        }
    }


}