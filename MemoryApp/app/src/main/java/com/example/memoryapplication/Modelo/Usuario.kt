package com.example.memoryapplication.Modelo

data class Usuario(
    var id: String? = null,
    var nombre: String = "",
    var apellido: String = "",
    var nombreUsuario: String = "",
    var contrasenia: String = "",
    var fechaNacimiento: String? = "",
    var sexo: String = "",
    var email: String = "",
    var fotoPerfil: String? = "",
    //var preferencias:Preferencia? = null,
    //var listaAmigos: List<String> = emptyList(),

    //var listaTemas: Tema? = null,
    val rol: Int = 0,

)


