package com.cibertec.calculadorasueldo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.calculadorasueldo.R
import com.cibertec.calculadorasueldo.adapter.TrabajadorAdapter
import com.cibertec.calculadorasueldo.data.TrabajadorStore
import com.cibertec.calculadorasueldo.databinding.DialogEditarTrabajadorBinding
import com.cibertec.calculadorasueldo.databinding.FragmentListaBinding
import com.cibertec.calculadorasueldo.model.Trabajador
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ListaFragment : Fragment() {

    private var _binding: FragmentListaBinding? = null
    private val binding get() = _binding!!

    private lateinit var trabajadorAdapter: TrabajadorAdapter
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
        _binding = FragmentListaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        cargarTrabajadores()
    }

    private fun configurarRecyclerView() {
        trabajadorAdapter = TrabajadorAdapter()
        binding.recyclerViewTrabajadores.layoutManager =
            LinearLayoutManager(requireContext())
        binding.recyclerViewTrabajadores.adapter = trabajadorAdapter
        configurarGestos()
    }

    private fun configurarGestos() {
        val callback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.START or ItemTouchHelper.END
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val posicion = viewHolder.bindingAdapterPosition
                if (posicion == RecyclerView.NO_POSITION) return

                val trabajador = trabajadorAdapter.obtenerTrabajador(posicion)
                if (direction == ItemTouchHelper.END) {
                    mostrarEdicion(trabajador, posicion)
                } else {
                    confirmarEliminacion(trabajador, posicion)
                }
            }
        }

        ItemTouchHelper(callback).attachToRecyclerView(binding.recyclerViewTrabajadores)
    }

    private fun cargarTrabajadores() {
        binding.progressIndicator.visibility = View.VISIBLE
        binding.recyclerViewTrabajadores.visibility = View.GONE
        binding.textViewListaVacia.visibility = View.GONE

        Thread {
            val trabajadores = trabajadorStore.obtenerTodas()

            activity?.runOnUiThread {
                val bindingActual = _binding ?: return@runOnUiThread
                trabajadorAdapter.actualizar(trabajadores)
                bindingActual.progressIndicator.visibility = View.GONE
                bindingActual.recyclerViewTrabajadores.visibility =
                    if (trabajadores.isEmpty()) View.GONE else View.VISIBLE
                bindingActual.textViewListaVacia.visibility =
                    if (trabajadores.isEmpty()) View.VISIBLE else View.GONE
            }
        }.start()
    }

    private fun mostrarEdicion(trabajador: Trabajador, posicion: Int) {
        val dialogBinding = DialogEditarTrabajadorBinding.inflate(layoutInflater)
        dialogBinding.editTextNombreEditar.setText(trabajador.nombre)
        dialogBinding.editTextSueldoBaseEditar.setText(trabajador.sueldoBase.toString())
        dialogBinding.editTextBonoEditar.setText(trabajador.bono.toString())

        dialogBinding.autoCompleteAreaEditar.setSimpleItems(R.array.areas)
        dialogBinding.autoCompleteAreaEditar.setText(trabajador.area, false)

        val radioSeleccionado = when (trabajador.pension) {
            getString(R.string.pension_afp) -> R.id.radioAfpEditar
            else -> R.id.radioOnpEditar
        }
        dialogBinding.radioGroupPensionEditar.check(radioSeleccionado)

        val dialogo = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialogo_editar_titulo, trabajador.id))
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.dialogo_guardar, null)
            .setNegativeButton(R.string.dialogo_cancelar) { _, _ ->
                trabajadorAdapter.restaurarTarjeta(posicion)
            }
            .setOnCancelListener {
                trabajadorAdapter.restaurarTarjeta(posicion)
            }
            .create()

        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                guardarEdicion(dialogo, dialogBinding, trabajador, posicion)
            }
        }
        dialogo.show()
    }

    private fun guardarEdicion(
        dialogo: AlertDialog,
        dialogBinding: DialogEditarTrabajadorBinding,
        trabajadorOriginal: Trabajador,
        posicion: Int
    ) {
        val nombre = dialogBinding.editTextNombreEditar.text.toString().trim()
        val textoSueldoBase = dialogBinding.editTextSueldoBaseEditar.text.toString()
        val textoBono = dialogBinding.editTextBonoEditar.text.toString()
        val area = dialogBinding.autoCompleteAreaEditar.text.toString().trim()

        val sueldoBase = textoSueldoBase.replace(",", ".").toDoubleOrNull()
        val bono = textoBono.replace(",", ".").toDoubleOrNull()

        val pension = when (dialogBinding.radioGroupPensionEditar.checkedRadioButtonId) {
            R.id.radioAfpEditar -> getString(R.string.pension_afp)
            R.id.radioOnpEditar -> getString(R.string.pension_onp)
            else -> null
        }

        dialogBinding.inputLayoutNombreEditar.error = null
        dialogBinding.inputLayoutSueldoBaseEditar.error = null
        dialogBinding.inputLayoutBonoEditar.error = null
        dialogBinding.inputLayoutAreaEditar.error = null
        dialogBinding.textViewErrorPensionEditar.visibility = View.GONE

        var formularioValido = true

        if (nombre.isBlank()) {
            dialogBinding.inputLayoutNombreEditar.error = getString(R.string.error_nombre)
            formularioValido = false
        }

        if (sueldoBase == null || sueldoBase <= 0) {
            dialogBinding.inputLayoutSueldoBaseEditar.error =
                getString(R.string.error_sueldo_base)
            formularioValido = false
        }

        if (bono == null || bono < 0) {
            dialogBinding.inputLayoutBonoEditar.error = getString(R.string.error_bono)
            formularioValido = false
        }

        if (area.isBlank() || area == getString(R.string.area_placeholder)) {
            dialogBinding.inputLayoutAreaEditar.error = getString(R.string.error_area)
            formularioValido = false
        }

        if (pension == null) {
            dialogBinding.textViewErrorPensionEditar.visibility = View.VISIBLE
            formularioValido = false
        }

        if (!formularioValido || sueldoBase == null || bono == null || pension == null) {
            return
        }

        val sueldoTotal = calcularSueldoTotal(sueldoBase, bono, pension)

        val trabajadorEditado = trabajadorOriginal.copy(
            nombre = nombre,
            sueldoBase = sueldoBase,
            bono = bono,
            pension = pension,
            area = area,
            sueldoTotal = sueldoTotal
        )

        Thread {
            val filasActualizadas = trabajadorStore.actualizar(trabajadorEditado)
            activity?.runOnUiThread {
                if (_binding == null) return@runOnUiThread
                if (filasActualizadas == 1) {
                    dialogo.dismiss()
                    cargarTrabajadores()
                } else {
                    trabajadorAdapter.restaurarTarjeta(posicion)
                    Toast.makeText(
                        requireContext(),
                        R.string.error_actualizar_trabajador,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }

    private fun confirmarEliminacion(trabajador: Trabajador, posicion: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialogo_eliminar_titulo)
            .setMessage(
                getString(
                    R.string.dialogo_eliminar_mensaje,
                    trabajador.nombre,
                    trabajador.id
                )
            )
            .setPositiveButton(R.string.dialogo_eliminar_confirmar) { _, _ ->
                eliminarTrabajador(trabajador, posicion)
            }
            .setNegativeButton(R.string.dialogo_cancelar) { _, _ ->
                trabajadorAdapter.restaurarTarjeta(posicion)
            }
            .setOnCancelListener {
                trabajadorAdapter.restaurarTarjeta(posicion)
            }
            .show()
    }

    private fun eliminarTrabajador(trabajador: Trabajador, posicion: Int) {
        Thread {
            val filasEliminadas = trabajadorStore.eliminar(trabajador.id)

            activity?.runOnUiThread {
                if (_binding == null) return@runOnUiThread
                if (filasEliminadas == 1) {
                    trabajadorAdapter.eliminar(posicion)
                    if (trabajadorAdapter.getItemCount() == 0) {
                        binding.textViewListaVacia.visibility = View.VISIBLE
                        binding.recyclerViewTrabajadores.visibility = View.GONE
                    }
                } else {
                    trabajadorAdapter.restaurarTarjeta(posicion)
                    Toast.makeText(
                        requireContext(),
                        R.string.error_eliminar_trabajador,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }

    private fun calcularSueldoTotal(sueldoBase: Double, bono: Double, pension: String): Double {
        return when (pension) {
            getString(R.string.pension_afp) -> sueldoBase - sueldoBase * 0.10 + bono
            getString(R.string.pension_onp) -> sueldoBase - sueldoBase * 0.13 + bono
            else -> 0.0
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}