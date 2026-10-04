package com.cibertec.calculadorasueldo.data

import com.cibertec.calculadorasueldo.model.Solicitud

object SolicitudStore {

    // Esta lista vive en memoria. En una clase posterior, este archivo será
    // el punto de reemplazo por una fuente de persistencia.
    private val solicitudes = mutableListOf<Solicitud>()
    private var siguienteId = 1

    fun agregar(
        nombre: String,
        descripcion: String,
        categoria: String,
        prioridad: String
    ) {
        solicitudes.add(
            Solicitud(
                id = siguienteId,
                nombre = nombre,
                descripcion = descripcion,
                categoria = categoria,
                prioridad = prioridad
            )
        )
        siguienteId += 1
    }

    fun obtenerTodas(): List<Solicitud> {
        return solicitudes.toList()
    }
}
