package com.cibertec.calculadorasueldo.data

// El contrato centraliza los nombres usados por SQLite. Así se evita repetir
// cadenas en las consultas y se reduce el riesgo de escribir una columna distinta.
object TrabajadorContrato {
    const val NOMBRE_BASE_DATOS = "calculadora_sueldo.db"
    const val VERSION_BASE_DATOS = 1

    const val TABLA_TRABAJADORES = "trabajadores"
    const val COLUMNA_ID = "id"
    const val COLUMNA_NOMBRE = "nombre"
    const val COLUMNA_SUELDO_BASE = "sueldo_base"
    const val COLUMNA_BONO = "bono"
    const val COLUMNA_PENSION = "pension"
    const val COLUMNA_AREA = "area"
    const val COLUMNA_SUELDO_TOTAL = "sueldo_total"

    // INTEGER PRIMARY KEY AUTOINCREMENT delega a SQLite la generación correlativa del id.
    // TEXT admite los datos escritos por el usuario y la ruta privada de la imagen.
    const val SQL_CREAR_TABLA = """
        CREATE TABLE $TABLA_TRABAJADORES (
            $COLUMNA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMNA_NOMBRE TEXT NOT NULL,
            $COLUMNA_SUELDO_BASE REAL NOT NULL,
            $COLUMNA_BONO REAL NOT NULL,
            $COLUMNA_PENSION TEXT NOT NULL,
            $COLUMNA_AREA TEXT NOT NULL,
            $COLUMNA_SUELDO_TOTAL REAL NOT NULL
        )
    """
}