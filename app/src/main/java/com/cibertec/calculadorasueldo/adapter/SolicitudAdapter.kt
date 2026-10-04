package com.cibertec.calculadorasueldo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
// Descomentar al mostrar el código formateado dentro de la tarjeta.
import com.cibertec.calculadorasueldo.R
import com.cibertec.calculadorasueldo.databinding.ItemSolicitudBinding
import com.cibertec.calculadorasueldo.model.Solicitud

class SolicitudAdapter(
    private val onSolicitudClick: (Solicitud) -> Unit
) : RecyclerView.Adapter<SolicitudAdapter.SolicitudViewHolder>() {

    private val solicitudes = mutableListOf<Solicitud>()

    fun actualizar(nuevasSolicitudes: List<Solicitud>) {
        val cantidadAnterior = solicitudes.size
        solicitudes.clear()
        if (cantidadAnterior > 0) {
            notifyItemRangeRemoved(0, cantidadAnterior)
        }

        solicitudes.addAll(nuevasSolicitudes)
        if (solicitudes.isNotEmpty()) {
            notifyItemRangeInserted(0, solicitudes.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolicitudViewHolder {
        val binding = ItemSolicitudBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SolicitudViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SolicitudViewHolder, position: Int) {
        holder.vincular(solicitudes[position])
    }

    override fun getItemCount(): Int {
        return solicitudes.size
    }

    inner class SolicitudViewHolder(
        private val binding: ItemSolicitudBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun vincular(solicitud: Solicitud) {
            // PRÁCTICA EN CLASE:
            // 1. Mostrar código, nombre, descripción, categoría y prioridad.
            // 2. Elegir con when el fondo verde, amarillo o rojo del semáforo.
            // 3. Enviar la solicitud a onSolicitudClick cuando se toque la tarjeta.
            // La etiqueta conserva temporalmente el objeto y mantiene el proyecto ejecutable.
            binding.textViewCodigo.text =
                binding.root.context.getString(R.string.codigo_formato, solicitud.id)
            binding.textViewNombre.text = solicitud.nombre
            binding.textViewDescripcion.text = solicitud.descripcion
            binding.textViewCategoria.text =
                binding.root.context.getString(R.string.categoria_formato, solicitud.categoria)
            binding.textViewPrioridad.text = solicitud.prioridad
            //en um when -> separa cada valor evaluado del resultado correspondiente
            val fondoSemaforo = when (solicitud.prioridad) {
                binding.root.context.getString(R.string.prioridad_baja) ->
                    R.drawable.bg_semaforo_verde

                binding.root.context.getString(R.string.prioridad_media) ->
                    R.drawable.bg_semaforo_amarillo

                binding.root.context.getString(R.string.prioridad_alta) ->
                    R.drawable.bg_semaforo_rojo

                else -> R.drawable.bg_semaforo_neutro

            }

            binding.viewSemaforoTarjeta.setBackgroundResource(fondoSemaforo)





            binding.root.setOnClickListener {
                //se invoca la funcion recibida por el constructor y se entrega la solicitud
                onSolicitudClick(solicitud)
            }

        }
    }
}
