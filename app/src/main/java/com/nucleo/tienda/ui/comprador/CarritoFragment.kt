package com.nucleo.tienda.ui.comprador

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.data.Rol
import com.nucleo.tienda.databinding.FragmentCarritoBinding
import com.nucleo.tienda.databinding.IncludeResumenBinding
import com.nucleo.tienda.ui.comun.configurar
import com.nucleo.tienda.ui.comun.mostrar
import com.nucleo.tienda.util.comoPesos

class CarritoFragment : Fragment() {

    // ViewBinding: _binding vive mientras exista la vista del fragment.
    private var _binding: FragmentCarritoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCarritoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private lateinit var adaptador: CarritoAdapter
    private val actividad get() = requireActivity() as CompradorActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.encabezado.configurar(Rol.COMPRADOR, getString(R.string.carrito_titulo))
        binding.aviso.mostrar(getString(R.string.aviso_un_comercio))

        adaptador = CarritoAdapter(
            alCambiarCantidad = { item, delta ->
                Repositorio.cambiarCantidad(item.productoId, delta)
                refrescar()
            },
            alQuitar = { item ->
                Repositorio.quitarDelCarrito(item.productoId)
                refrescar()
            }
        )
        binding.listaCarrito.adapter = adaptador
        binding.botonIrCatalogo.setOnClickListener { actividad.irA(R.id.catalogoFragment) }
        binding.botonPagar.setOnClickListener { findNavController().navigate(R.id.pagoFragment) }
        refrescar()
    }

    private fun refrescar() {
        val items = Repositorio.carrito()
        val vacio = items.isEmpty()
        binding.grupoVacio.isVisible = vacio
        binding.grupoContenido.isVisible = !vacio
        binding.botonPagar.isVisible = !vacio
        actividad.actualizarInsigniaCarrito()
        if (vacio) return

        binding.textoComercio.text = Repositorio.vendedorDelCarrito()?.nombreVisible
        adaptador.actualizar(items)
        binding.resumen.llenar()
    }
}

/** Llena el bloque de totales (include_resumen.xml) con los valores del carrito. */
fun IncludeResumenBinding.llenar() {
    val contexto = root.context
    filaSubtotal.etiqueta.text = contexto.getString(R.string.subtotal, contexto.resources.getQuantityString(R.plurals.productos, Repositorio.cantidadEnCarrito(), Repositorio.cantidadEnCarrito()))
    filaSubtotal.valor.text = Repositorio.subtotalCarrito().comoPesos()
    filaEnvio.etiqueta.text = contexto.getString(R.string.envio)
    filaEnvio.valor.text = Repositorio.envioCarrito().comoPesos()
    textoTotal.text = (Repositorio.subtotalCarrito() + Repositorio.envioCarrito()).comoPesos()
}
