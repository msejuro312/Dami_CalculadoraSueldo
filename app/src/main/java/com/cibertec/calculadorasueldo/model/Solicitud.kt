package com.cibertec.calculadorasueldo.model

data class Solicitud(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val prioridad: String
)
