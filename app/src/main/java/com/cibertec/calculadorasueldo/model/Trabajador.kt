package com.cibertec.calculadorasueldo.model

data class Trabajador(
    val id: Int,
    val nombre: String,
    val sueldoBase: Double,
    val bono: Double,
    val pension: String,
    val area: String,
    val sueldoTotal: Double
)
