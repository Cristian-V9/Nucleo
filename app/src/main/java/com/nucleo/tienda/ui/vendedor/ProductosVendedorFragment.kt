package com.nucleo.tienda.ui.vendedor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.data.Rol
import com.nucleo.tienda.databinding.FragmentProductosVendedorBinding
import com.nucleo.tienda.ui.comun.Dialogos
import com.nucleo.tienda.ui.comun.ProductoFilaAdapter
import com.nucleo.tienda.ui.comun.configurar
import com.nucleo.tienda.util.mostrarMensaje
import com.nucleo.tienda.util.texto

/** Panel de productos del vendedor (CRUD completo, RF-06). */
class ProductosVendedorFragment : Fragment() {

    // ViewBinding: _binding vive mientras exista la vista del fragment.
    private var _binding: FragmentProductosVendedorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductosVendedorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private lateinit var adaptador: ProductoFilaAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.encabezado.configurar(Rol.VENDEDOR, getString(R.string.mis_productos),
            Repositorio.usuarioActual?.nombreVisible)

        adaptador = ProductoFilaAdapter(
            modoAdmin = false,
            alEditar = { abrirFormulario(it.id) },
            alEliminar = { producto ->
                Dialogos.confirmar(requireContext(), getString(R.string.eliminar_producto_titulo),
                    getString(R.string.eliminar_producto_mensaje, producto.nombre), getString(R.string.eliminar)) {
                    Repositorio.eliminarProducto(producto.id)
                    filtrar()
                    binding.root.mostrarMensaje(getString(R.string.producto_eliminado))
                }
            }
        )
        binding.listaProductos.adapter = adaptador
        binding.botonNuevo.setOnClickListener { abrirFormulario(null) }
        binding.grupoFiltros.setOnCheckedStateChangeListener { _, _ -> filtrar() }
        binding.campoBuscar.editText?.doAfterTextChanged { filtrar() }
        filtrar()
    }

    private fun abrirFormulario(id: String?) {
        findNavController().navigate(R.id.productoFormFragment, bundleOf("productoId" to id))
    }

    private fun filtrar() {
        val vendedor = Repositorio.usuarioActual?.id ?: return
        val texto = binding.campoBuscar.texto().lowercase()
        val lista = Repositorio.productosDe(vendedor)
            .filter { texto.isEmpty() || it.nombre.lowercase().contains(texto) || it.referencia.lowercase().contains(texto) }
            .filter {
                when (binding.grupoFiltros.checkedChipId) {
                    R.id.chipActivos -> it.stock > 0 && !it.oculto
                    R.id.chipSinStock -> it.stock == 0
                    R.id.chipOcultos -> it.oculto
                    else -> true
                }
            }
        adaptador.actualizar(lista)
        binding.textoVacio.isVisible = lista.isEmpty()
    }
}
