package com.example.memoryapplication.Modelo

data class Tarjeta (
    var idTarjeta: String? = null,
    var preguntaFrente: String = "",
    var respuestaReverso: String = "",
    var idUsuario: String = "",
    var idTema: String = "",
    var idLista: String = "",
    var puntajeTarjeta: Int = 0,
    val audioUrl: String = ""
)


