package com.cibertec.calculadorasueldo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
// Descomentar al mostrar el código formateado dentro de la tarjeta.
import com.cibertec.calculadorasueldo.R
import com.cibertec.calculadorasueldo.databinding.ItemTrabajadorBinding
import com.cibertec.calculadorasueldo.model.Trabajador

class TrabajadorAdapter : RecyclerView.Adapter<TrabajadorAdapter.TrabajadorViewHolder>() {

    private val trabajadores = mutableListOf<Trabajador>()

    fun actualizar(nuevosTrabajadores: List<Trabajador>) {
        val cantidadAnterior = trabajadores.size
        trabajadores.clear()
        if (cantidadAnterior > 0) {
            notifyItemRangeRemoved(0, cantidadAnterior)
        }

        trabajadores.addAll(nuevosTrabajadores)
        if (trabajadores.isNotEmpty()) {
            notifyItemRangeInserted(0, trabajadores.size)
        }
    }

    fun obtenerTrabajador(position: Int): Trabajador {
        return trabajadores[position]
    }

    fun eliminar(posicion: Int) {
        trabajadores.removeAt(posicion)
        notifyItemRemoved(posicion)
    }

    fun restaurarTarjeta(position: Int) {
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrabajadorViewHolder {
        val binding = ItemTrabajadorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrabajadorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrabajadorViewHolder, position: Int) {
        holder.vincular(trabajadores[position])
    }

    override fun getItemCount(): Int {
        return trabajadores.size
    }

    inner class TrabajadorViewHolder(
        private val binding: ItemTrabajadorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun vincular(trabajador: Trabajador) {
            val contexto = binding.root.context
            binding.textViewCodigo.text =
                contexto.getString(R.string.codigo_formato, trabajador.id)
            binding.textViewNombre.text = trabajador.nombre
            binding.textViewSueldoTotal.text =
                contexto.getString(R.string.sueldo_formato, trabajador.sueldoTotal)
            binding.textViewArea.text =
                contexto.getString(R.string.area_formato, trabajador.area)
            binding.textViewPension.text = trabajador.pension
        }
    }

}
