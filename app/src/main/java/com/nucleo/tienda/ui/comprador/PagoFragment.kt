package com.nucleo.tienda.ui.comprador

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.databinding.FragmentPagoBinding
import com.nucleo.tienda.ui.comun.TipoAviso
import com.nucleo.tienda.ui.comun.configurar
import com.nucleo.tienda.ui.comun.mostrar
import com.nucleo.tienda.util.Biometria
import com.nucleo.tienda.util.comoPesos
import com.nucleo.tienda.util.mostrarMensaje
import com.nucleo.tienda.util.textoObligatorio

/**
 * Confirmación del pedido. En esta versión el pago se simula; en la siguiente se abrirá
 * el checkout de Mercado Pago (entorno de pruebas) y el servidor confirmará el resultado.
 */
class PagoFragment : Fragment() {

    // ViewBinding: _binding vive mientras exista la vista del fragment.
    private var _binding: FragmentPagoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPagoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.encabezado.configurar(getString(R.string.pago_titulo)) { findNavController().navigateUp() }
        binding.avisoSeguridad.mostrar(getString(R.string.aviso_pasarela), TipoAviso.SEGURIDAD)
        binding.tarjetaMercadoPago.setOnClickListener { binding.tarjetaMercadoPago.isChecked = true }
        binding.botonUbicacion.setOnClickListener { binding.root.mostrarMensaje(getString(R.string.proxima_version)) }

        val total = Repositorio.subtotalCarrito() + Repositorio.envioCarrito()
        binding.resumen.llenar()
        binding.textoResumen.text = getString(R.string.resumen_pedido, resources.getQuantityString(R.plurals.productos, Repositorio.cantidadEnCarrito(), Repositorio.cantidadEnCarrito()),
            Repositorio.vendedorDelCarrito()?.nombreVisible.orEmpty())
        binding.botonPagar.text = getString(R.string.pagar, total.comoPesos())
        binding.botonPagar.setOnClickListener { pagar(total) }
    }

    private fun pagar(total: Long) {
        val direccion = binding.campoDireccion.textoObligatorio(getString(R.string.campo_obligatorio)) ?: return
        if (binding.interruptorHuella.isChecked && Biometria.disponible(this)) {
            Biometria.autenticar(
                fragment = this,
                titulo = getString(R.string.pago_huella_titulo),
                subtitulo = getString(R.string.pago_huella_subtitulo, total.comoPesos()),
                textoCancelar = getString(R.string.cancelar),
                alAutenticar = { confirmar(direccion) },
                alFallar = { binding.root.mostrarMensaje(it) }
            )
        } else {
            confirmar(direccion)
        }
    }

    private fun confirmar(direccion: String) {
        val comercio = Repositorio.vendedorDelCarrito()?.nombreVisible.orEmpty()
        val pedido = Repositorio.confirmarPedido(direccion) ?: return
        val actividad = requireActivity() as CompradorActivity
        actividad.actualizarInsigniaCarrito()

        MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_check)
            .setTitle(R.string.pago_aprobado_titulo)
            .setMessage(getString(R.string.pago_aprobado_mensaje, pedido.id, comercio, direccion))
            .setCancelable(false)
            .setPositiveButton(R.string.ver_mis_pedidos) { _, _ ->
                findNavController().popBackStack(R.id.carritoFragment, false)
                actividad.irA(R.id.perfilFragment)
            }
            .show()
    }
}
