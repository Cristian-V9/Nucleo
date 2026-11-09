package com.nucleo.tienda.ui.admin

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.data.Rol
import com.nucleo.tienda.databinding.FragmentDashboardBinding
import com.nucleo.tienda.databinding.IncludeAlertaBinding
import com.nucleo.tienda.databinding.IncludeEstadisticaBinding
import com.nucleo.tienda.ui.comun.Dialogos
import com.nucleo.tienda.ui.comun.PedidoAdapter
import com.nucleo.tienda.ui.comun.cerrarSesion
import com.nucleo.tienda.ui.comun.configurar
import com.nucleo.tienda.util.comoPesos
import java.time.YearMonth

/** Dashboard principal del administrador. */
class DashboardFragment : Fragment() {

    // ViewBinding: _binding vive mientras exista la vista del fragment.
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.encabezado.configurar(
            rol = Rol.ADMIN,
            titulo = getString(R.string.panel_general),
            subtitulo = getString(R.string.panel_resumen),
            iconoAccion = R.drawable.ic_salir,
            descripcionAccion = getString(R.string.cerrar_sesion)
        ) {
            Dialogos.confirmar(requireContext(), getString(R.string.cerrar_sesion), getString(R.string.cerrar_sesion_pregunta),
                getString(R.string.cerrar_sesion)) { cerrarSesion(requireActivity()) }
        }

        val pedidos = Repositorio.todosLosPedidos()
        val ultimoMes = YearMonth.from(pedidos.maxOf { it.fecha })
        val ventasMes = pedidos.filter { YearMonth.from(it.fecha) == ultimoMes }.sumOf { it.total }
        val solicitudes = Repositorio.solicitudesPendientes().size
        val ocultos = Repositorio.todosLosProductos().count { it.oculto }

        binding.estadisticaComercios.llenar(R.string.comercios_activos, Repositorio.vendedoresActivos().size.toString(), R.drawable.ic_tienda)
        binding.estadisticaSolicitudes.llenar(R.string.solicitudes_pendientes, solicitudes.toString(), R.drawable.ic_reloj, R.color.advertencia)
        binding.estadisticaUsuarios.llenar(R.string.usuarios_registrados, Repositorio.usuarios().size.toString(), R.drawable.ic_usuarios)
        binding.estadisticaVentas.llenar(R.string.ventas_mes, ventasMes.comoPesos(), R.drawable.ic_grafica)

        binding.alertaSolicitudes.llenar(resources.getQuantityString(R.plurals.comercios_por_verificar, solicitudes, solicitudes),
            getString(R.string.revisar_documentos), R.drawable.ic_tienda, R.color.advertencia, R.color.advertencia_suave)
        binding.alertaSolicitudes.root.setOnClickListener { findNavController().navigate(R.id.solicitudesFragment) }

        binding.alertaProductos.llenar(resources.getQuantityString(R.plurals.publicaciones_ocultas, ocultos, ocultos),
            getString(R.string.revisar_moderacion), R.drawable.ic_ocultar, R.color.error, R.color.error_suave)
        binding.alertaProductos.root.setOnClickListener {
            (requireActivity() as AdminActivity).irA(R.id.productosAdminFragment)
        }

        binding.listaPedidos.adapter = PedidoAdapter(pedidos.take(4), mostrarComprador = true)
    }

    private fun IncludeEstadisticaBinding.llenar(etiqueta: Int, valor: String, icono: Int, colorValor: Int? = null) {
        this.etiqueta.setText(etiqueta)
        this.valor.text = valor
        this.icono.setImageResource(icono)
        if (colorValor != null) this.valor.setTextColor(ContextCompat.getColor(requireContext(), colorValor))
    }

    private fun IncludeAlertaBinding.llenar(titulo: String, descripcion: String, icono: Int, color: Int, fondo: Int) {
        this.titulo.text = titulo
        this.descripcion.text = descripcion
        this.icono.setImageResource(icono)
        this.icono.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
        fondoIcono.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), fondo))
    }
}
