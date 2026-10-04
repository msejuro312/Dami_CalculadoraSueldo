package com.cibertec.calculadorasueldo.data

import android.content.ContentValues
import android.content.Context
import com.cibertec.calculadorasueldo.model.Trabajador



class TrabajadorStore(context: Context) {

    private val dbHelper = TrabajadorDbHelper(context.applicationContext)

    fun agregar(
        nombre: String,
        sueldoBase: Double,
        bono: Double,
        pension: String,
        area: String,
        sueldoTotal: String
    ): Long {
        // PRÁCTICA TEMA 6 - BLOQUE 3:
        // 1. Crear ContentValues y relacionar las cinco columnas con sus valores.
        // 2. Abrir writableDatabase.
        // 3. Retornar el resultado de insert().
        // -1 mantiene la plantilla ejecutable hasta completar la inserción.
        //ContentValuies relacionada cada columna con el valor que se almacenará
        val valores = ContentValues().apply {
            put(TrabajadorContrato.COLUMNA_NOMBRE, nombre)
            put(TrabajadorContrato.COLUMNA_SUELDO_BASE, sueldoBase)
            put(TrabajadorContrato.COLUMNA_BONO, bono)
            put(TrabajadorContrato.COLUMNA_PENSION, pension)
            put(TrabajadorContrato.COLUMNA_AREA, area)
            put(TrabajadorContrato
                .COLUMNA_SUELDO_TOTAL,sueldoTotal)
        }

        //writableDatabase abre la bd para escritura.insert() devuelve el ide generado
        //-1 cuando no fue posible insertar la fila
        return dbHelper.writableDatabase.insert(
            TrabajadorContrato.TABLA_TRABAJADORES,
            null,
            valores
        )
    }

    fun obtenerTodas(): List<Trabajador> {
        // PRÁCTICA TEMA 6 - BLOQUE 4A (READ):
        // 1. Crear una MutableList<Solicitud> y el arreglo de columnas.
        // 2. Ejecutar query() sobre readableDatabase.
        // 3. Recorrer el Cursor con while (cursor.moveToNext()).
        // 4. Construir cada Solicitud, agregarla a la lista y retornar el resultado.
        val solicitudes = mutableListOf<Trabajador>()
        val columnas = arrayOf(
            TrabajadorContrato.COLUMNA_ID,
            TrabajadorContrato.COLUMNA_NOMBRE,
            TrabajadorContrato.COLUMNA_SUELDO_BASE,
            TrabajadorContrato.COLUMNA_BONO,
            TrabajadorContrato.COLUMNA_PENSION,
            TrabajadorContrato.COLUMNA_AREA,
            TrabajadorContrato.COLUMNA_SUELDO_TOTAL
        )

        //query() devuelve un Cursosr. El bloque use garantiza que el Cursor se cierra
        dbHelper.readableDatabase.query(
            TrabajadorContrato.TABLA_TRABAJADORES,
            columnas,
            null,
            null,
            null,
            null,
            "${TrabajadorContrato.COLUMNA_ID} DESC").use { cursor ->
            val indiceId = cursor.getColumnIndexOrThrow(TrabajadorContrato.COLUMNA_ID)
            val indiceNombre = cursor.getColumnIndexOrThrow(TrabajadorContrato.COLUMNA_NOMBRE)
            val indiceSueldoBase =
                cursor.getColumnIndexOrThrow(TrabajadorContrato.COLUMNA_SUELDO_BASE)
            val indiceCategoria = cursor.getColumnIndexOrThrow(SolicitudContrato.COLUMNA_CATEGORIA)
            val indicePrioridad = cursor.getColumnIndexOrThrow(SolicitudContrato.COLUMNA_PRIORIDAD)
            val indiceImagen = cursor.getColumnIndexOrThrow(SolicitudContrato.COLUMNA_RUTA_IMAGEN)

            //moveToNext() avanza fila por fila y devuelve false al terminar
            while (cursor.moveToNext()) {
                solicitudes.add(
                    Solicitud(
                        id = cursor.getInt(indiceId),
                        nombre = cursor.getString(indiceNombre),
                        descripcion = cursor.getString(indiceDescripcion),
                        categoria = cursor.getString(indiceCategoria),
                        prioridad = cursor.getString(indicePrioridad),
                        rutaImagen = if (cursor.isNull(indiceImagen)) {
                            null
                        } else {
                            cursor.getString(indiceImagen)
                        }
                    )
                )
            }
        }


        return solicitudes
    }

    fun actualizar(solicitud: Solicitud): Int {
        val valores = ContentValues().apply {
            put(SolicitudContrato.COLUMNA_NOMBRE,solicitud.nombre)
            put(SolicitudContrato.COLUMNA_DESCRIPCION,solicitud.descripcion)
            put(SolicitudContrato.COLUMNA_CATEGORIA,solicitud.categoria)
            put(SolicitudContrato.COLUMNA_PRIORIDAD,solicitud.prioridad)
            put(SolicitudContrato.COLUMNA_RUTA_IMAGEN,solicitud.rutaImagen)
        }

        //? es un marcador seguro: SQLite reemplaza su valor con selectionArgs y evita
        //concatenar directamente información dentro de la condición SQL
        return dbHelper.writableDatabase.update(
            SolicitudContrato.TABLA_SOLICITUDES,
            valores,
            "${SolicitudContrato.COLUMNA_ID} = ?",
            arrayOf(solicitud.id.toString())
        )
        // PRÁCTICA TEMA 6 - BLOQUE 4B (UPDATE):
        // 1. Crear ContentValues con los datos de solicitud.
        // 2. Llamar a writableDatabase.update().
        // 3. Usar "id = ?" y arrayOf(solicitud.id.toString()) como condición segura.
        // 4. Retornar la cantidad de filas actualizadas.

    }

    fun eliminar(id: Int): Int {
        return dbHelper.writableDatabase.delete(
            SolicitudContrato.TABLA_SOLICITUDES,
            "${SolicitudContrato.COLUMNA_ID} = ?",
            arrayOf(id.toString())
        )
        // PRÁCTICA TEMA 6 - BLOQUE 4C (DELETE):
        // 1. Llamar a writableDatabase.delete().
        // 2. Usar "id = ?" y arrayOf(id.toString()) como condición segura.
        // 3. Retornar la cantidad de filas eliminadas.

    }
}
