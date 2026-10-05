package com.cibertec.calculadorasueldo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cibertec.calculadorasueldo.MainActivity
import com.cibertec.calculadorasueldo.R
import com.cibertec.calculadorasueldo.data.TrabajadorStore
import com.cibertec.calculadorasueldo.databinding.FragmentRegistroBinding

class RegistroFragment : Fragment() {

    private var _binding: FragmentRegistroBinding? = null
    private val binding get() = _binding!!

    private lateinit var trabajadorStore: TrabajadorStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trabajadorStore = TrabajadorStore(requireContext().applicationContext)
    }

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
        configurarSpinnerArea()

        binding.buttonCalcular.setOnClickListener {
            calcular()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun configurarSpinnerArea() {
        binding.spinnerArea.adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.areas,
            R.layout.spinner_area_item
        )
    }

    private fun obtenerAreaSeleccionada(): String? {
        if (binding.spinnerArea.selectedItemPosition == 0) return null
        return binding.spinnerArea.selectedItem?.toString()
    }

    private fun obtenerPensionSeleccionada(): String? {
        return when (binding.radioGroupPension.checkedRadioButtonId) {
            R.id.radioAfp -> getString(R.string.pension_afp)
            R.id.radioOnp -> getString(R.string.pension_onp)
            else -> null
        }
    }

    private fun calcularSueldoTotal(sueldoBase: Double, bono: Double, pension: String): Double {
        return when (pension) {
            getString(R.string.pension_afp) -> sueldoBase - sueldoBase * 0.10 + bono
            getString(R.string.pension_onp) -> sueldoBase - sueldoBase * 0.13 + bono
            else -> 0.0
        }
    }

    private fun limpiarFormulario() {
        binding.inputLayoutNombre.error = null
        binding.inputLayoutSueldoBase.error = null
        binding.inputLayoutBono.error = null
        binding.textViewErrorArea.visibility = View.GONE
        binding.textViewErrorPension.visibility = View.GONE
        binding.editTextNombre.text?.clear()
        binding.editTextSueldoBase.text?.clear()
        binding.editTextBono.text?.clear()
        binding.spinnerArea.setSelection(0)
        binding.radioGroupPension.clearCheck()

    }

    private fun calcular() {
        val nombre = binding.editTextNombre.text.toString().trim()
        val textoSueldoBase = binding.editTextSueldoBase.text.toString()
        val textoBono = binding.editTextBono.text.toString()
        val area = obtenerAreaSeleccionada()
        val pension = obtenerPensionSeleccionada()
        val sueldoBase = textoSueldoBase.replace(",", ".").toDoubleOrNull()
        val bono = textoBono.replace(",", ".").toDoubleOrNull()

        binding.inputLayoutNombre.error = null
        binding.inputLayoutSueldoBase.error = null
        binding.inputLayoutBono.error = null
        binding.textViewErrorArea.visibility = View.GONE
        binding.textViewErrorPension.visibility = View.GONE

        var formularioValido = true

        if (nombre.isBlank()) {
            binding.inputLayoutNombre.error = getString(R.string.error_nombre)
            formularioValido = false
        }

        if (sueldoBase == null || sueldoBase <= 0) {
            binding.inputLayoutSueldoBase.error = getString(R.string.error_sueldo_base)
            formularioValido = false
        }

        if (bono == null || bono < 0) {
            binding.inputLayoutBono.error = getString(R.string.error_bono)
            formularioValido = false
        }

        if (area == null) {
            binding.textViewErrorArea.visibility = View.VISIBLE
            formularioValido = false
        }

        if (pension == null) {
            binding.textViewErrorPension.visibility = View.VISIBLE
            formularioValido = false
        }

        if (!formularioValido || sueldoBase == null || bono == null || area == null || pension == null) {
            return
        }

        val sueldoTotal = calcularSueldoTotal(sueldoBase, bono, pension)

        Thread {
            val idInsertado =
                trabajadorStore.agregar(nombre, sueldoBase, bono, pension, area, sueldoTotal)
            activity?.runOnUiThread {
                if (_binding == null) return@runOnUiThread
                if (idInsertado == -1L) {
                    Toast.makeText(
                        requireContext(),
                        R.string.error_guardar_trabajador,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    limpiarFormulario()
                    (activity as? MainActivity)?.abrirLista()
                }
            }
        }.start()

    }
}
