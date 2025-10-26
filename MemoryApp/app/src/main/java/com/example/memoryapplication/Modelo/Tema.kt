package com.example.memoryapplication.Modelo

import androidx.compose.ui.text.input.TextFieldValue

data class Tema (
    var idTema: String? = null,
    var nombreTema: String = "",
    var icono : String = "",
    var idUsuario: String = ""
)