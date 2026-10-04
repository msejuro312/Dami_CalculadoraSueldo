package com.cibertec.calculadorasueldo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cibertec.calculadorasueldo.MainActivity
import com.cibertec.calculadorasueldo.R
import com.cibertec.calculadorasueldo.data.SolicitudStore
import com.cibertec.calculadorasueldo.databinding.FragmentRegistroBinding

class RegistroFragment : Fragment() {

    private var _binding: FragmentRegistroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarSelectorCategoria()
        configurarSemaforoPrioridad()
        binding.buttonAgregar.setOnClickListener {
            registrarSolicitud()
        }
    }

    private fun registrarSolicitud() {
        val nombre = binding.editTextNombre.text.toString().trim()
        val descripcion = binding.editTextDescripcion.text.toString().trim()
        val categoria = obtenerCategoriaSeleccionada()
        val prioridad = obtenerPrioridadSeleccionada()

        binding.inputLayoutNombre.error = null

        //limpia el campo ante el error
        binding.inputLayoutDescripcion.error = null
        binding.inputLayoutCategoria.error = null
        binding.textViewErrorPrioridad.visibility = View.GONE

        var formularioValido = true

        if (nombre.isBlank()) {
            binding.inputLayoutNombre.error = getString(R.string.error_nombre)
            formularioValido = false
        }

        if (descripcion.length < LONGITUD_MINIMA_DESCRIPCION) {
            binding.inputLayoutDescripcion.error = getString(R.string.error_descripcion)
            formularioValido = false
        }

        if (categoria == null) {
            binding.inputLayoutCategoria.error = getString(R.string.error_categoria)
            formularioValido = false
        }

        if (prioridad == null) {
            binding.textViewErrorPrioridad.visibility = View.VISIBLE
            formularioValido = false
        }

        if (!formularioValido || categoria == null || prioridad == null) {
            return
        }

        SolicitudStore.agregar(nombre, descripcion, categoria, prioridad)
        limpiarFormulario()
        (activity as? MainActivity)?.abrirLista()
    }

    private fun obtenerPrioridadSeleccionada(): String? {
        // PRÁCTICA EN CLASE:
        // Utilizar when para convertir el RadioButton seleccionado en un texto.
        // Si no existe selección, se debe devolver null.
        //When evalua varios varios casos -> separa cada condición del resultado que devuelve

        return when (binding.radioGroupPrioridad.checkedRadioButtonId){
            R.id.radioPrioridadBaja -> getString(R.string.prioridad_baja)
            R.id.radioPrioridadMedia -> getString(R.string.prioridad_media)
            R.id.radioPrioridadAlta -> getString(R.string.prioridad_alta)
            else -> null
        }

    }

    private fun obtenerCategoriaSeleccionada(): String? {
        return binding.autoCompleteCategoria.text
            .toString()
            .takeIf { categoria -> categoria.isNotBlank() }
    }

    private fun configurarSelectorCategoria() {
        binding.autoCompleteCategoria.setSimpleItems(R.array.categorias)
        binding.autoCompleteCategoria.setOnItemClickListener { _, _, _, _ ->
            binding.inputLayoutCategoria.error = null
        }
    }

    private fun configurarSemaforoPrioridad() {
        binding.radioGroupPrioridad.setOnCheckedChangeListener { _, _ ->
            val prioridad = obtenerPrioridadSeleccionada()
            if (prioridad != null) {
                actualizarSemaforo(prioridad)
                binding.textViewErrorPrioridad.visibility = View.GONE
            }
        }
    }

    private fun actualizarSemaforo(prioridad: String) {
        val fondoSemaforo = when (prioridad) {
            getString(R.string.prioridad_baja) -> R.drawable.bg_semaforo_verde
            getString(R.string.prioridad_media) -> R.drawable.bg_semaforo_amarillo
            getString(R.string.prioridad_alta) -> R.drawable.bg_semaforo_rojo
            else -> R.drawable.bg_semaforo_neutro
        }
        binding.viewSemaforo.setBackgroundResource(fondoSemaforo)
        binding.textViewSemaforoEstado.text =
            getString(R.string.semaforo_estado_formato, prioridad)
    }

    private fun limpiarFormulario() {
        binding.editTextNombre.text?.clear()
        binding.editTextDescripcion.text?.clear()
        binding.autoCompleteCategoria.setText(null, false)
        binding.radioGroupPrioridad.clearCheck()
        binding.viewSemaforo.setBackgroundResource(R.drawable.bg_semaforo_neutro)
        binding.textViewSemaforoEstado.text = getString(R.string.semaforo_estado_inicial)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LONGITUD_MINIMA_DESCRIPCION = 8
    }
}
