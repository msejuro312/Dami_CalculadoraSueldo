package com.cibertec.calculadorasueldo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
// Descomentar junto con MaterialAlertDialogBuilder para construir el diálogo.
import com.cibertec.calculadorasueldo.R
import com.cibertec.calculadorasueldo.adapter.SolicitudAdapter
import com.cibertec.calculadorasueldo.data.SolicitudStore
import com.cibertec.calculadorasueldo.databinding.DialogDetalleSolicitudBinding
// Descomentar para acceder a los componentes del diálogo personalizado.

import com.cibertec.calculadorasueldo.databinding.FragmentListaBinding
import com.cibertec.calculadorasueldo.model.Solicitud
// Descomentar al implementar el diálogo de detalle.
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ListaFragment : Fragment() {

    private var _binding: FragmentListaBinding? = null
    private val binding get() = _binding!!
    private lateinit var solicitudAdapter: SolicitudAdapter

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
        cargarSolicitudes()
    }

    private fun configurarRecyclerView() {
        solicitudAdapter = SolicitudAdapter { solicitud ->
            mostrarDetalle(solicitud)
        }
        binding.recyclerViewSolicitudes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSolicitudes.adapter = solicitudAdapter
    }

    private fun cargarSolicitudes() {
        binding.progressIndicator.visibility = View.VISIBLE
        binding.recyclerViewSolicitudes.visibility = View.GONE
        binding.textViewListaVacia.visibility = View.GONE

        // La espera breve representa la lectura que luego realizará la persistencia.
        binding.recyclerViewSolicitudes.postDelayed({
            val bindingActual = _binding ?: return@postDelayed
            val solicitudes = SolicitudStore.obtenerTodas()
            solicitudAdapter.actualizar(solicitudes)

            bindingActual.progressIndicator.visibility = View.GONE
            bindingActual.recyclerViewSolicitudes.visibility =
                if (solicitudes.isEmpty()) View.GONE else View.VISIBLE
            bindingActual.textViewListaVacia.visibility =
                if (solicitudes.isEmpty()) View.VISIBLE else View.GONE
        }, DURACION_CARGA_MS)
    }

    private fun mostrarDetalle(solicitud: Solicitud) {
        val dialogBinding = DialogDetalleSolicitudBinding.inflate(layoutInflater)
        dialogBinding.textViewTituloDialog.text = getString(R.string.dialogo_titulo_formato, solicitud.id)
        dialogBinding.textViewSolicitanteDialog.text = solicitud.nombre
        dialogBinding.textViewProblemaDialog.text = solicitud.descripcion
        dialogBinding.textViewCategoriaDialog.text = solicitud.categoria
        dialogBinding.textViewPrioridadDialog.text = solicitud.prioridad


        val fondoSemaforo = when (solicitud.prioridad){
            getString(R.string.prioridad_baja) -> R.drawable.bg_semaforo_verde
            getString(R.string.prioridad_media) -> R.drawable.bg_semaforo_amarillo
            getString(R.string.prioridad_alta) -> R.drawable.bg_semaforo_rojo

            else -> R.drawable.bg_semaforo_neutro

        }

        dialogBinding.viewSemaforoDialog.setBackgroundResource(fondoSemaforo)

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.dialogo_boton_aceptar,null)
            .show()


        // PRÁCTICA EN CLASE:
        // 1. Inflar DialogDetalleSolicitudBinding y mostrar los datos recibidos.
        // 2. Elegir con when el color del semáforo inferior según la prioridad.
        // 3. Crear MaterialAlertDialogBuilder con setView, botón y show().

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DURACION_CARGA_MS = 500L
    }
}
