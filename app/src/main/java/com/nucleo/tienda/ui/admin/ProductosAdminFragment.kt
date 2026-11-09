package com.nucleo.tienda.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Producto
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.data.Rol
import com.nucleo.tienda.databinding.FragmentProductosAdminBinding
import com.nucleo.tienda.ui.comun.Dialogos
import com.nucleo.tienda.ui.comun.ProductoFilaAdapter
import com.nucleo.tienda.ui.comun.configurar
import com.nucleo.tienda.util.mostrarMensaje
import com.nucleo.tienda.util.texto

/** Gestión de productos del administrador: ocultar o volver a mostrar publicaciones (RF-05). */
class ProductosAdminFragment : Fragment() {

    // ViewBinding: _binding vive mientras exista la vista del fragment.
    private var _binding: FragmentProductosAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductosAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val adaptador = ProductoFilaAdapter(modoAdmin = true, alModerar = { moderar(it) })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.encabezado.configurar(Rol.ADMIN, getString(R.string.productos_titulo))
        binding.listaProductos.adapter = adaptador
        binding.grupoFiltros.setOnCheckedStateChangeListener { _, _ -> filtrar() }
        binding.campoBuscar.editText?.doAfterTextChanged { filtrar() }
        filtrar()
    }

    private fun moderar(producto: Producto) {
        if (producto.oculto) {
            Repositorio.mostrarProducto(producto.id)
            filtrar()
            binding.root.mostrarMensaje(getString(R.string.publicacion_visible))
        } else {
            Dialogos.pedirTexto(requireContext(), getString(R.string.ocultar_titulo), producto.nombre,
                getString(R.string.motivo_hint), getString(R.string.ocultar)) { motivo ->
                if (motivo.isEmpty()) {
                    binding.root.mostrarMensaje(getString(R.string.motivo_obligatorio))
                    return@pedirTexto
                }
                Repositorio.ocultarProducto(producto.id, motivo)
                filtrar()
                binding.root.mostrarMensaje(getString(R.string.publicacion_oculta))
            }
        }
    }

    private fun filtrar() {
        val texto = binding.campoBuscar.texto().lowercase()
        val lista = Repositorio.todosLosProductos()
            .filter { texto.isEmpty() || it.nombre.lowercase().contains(texto) }
            .filter {
                when (binding.grupoFiltros.checkedChipId) {
                    R.id.chipVisibles -> !it.oculto
                    R.id.chipOcultas -> it.oculto
                    else -> true
                }
            }
            .sortedWith(compareByDescending<Producto> { it.oculto }.thenBy { it.nombre })
        adaptador.actualizar(lista)
    }
}
