package com.nucleo.tienda.ui.comprador

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.data.Rol
import com.nucleo.tienda.databinding.FragmentInicioBinding
import com.nucleo.tienda.ui.comun.configurar

class InicioFragment : Fragment() {

    // ViewBinding: _binding vive mientras exista la vista del fragment.
    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val actividad get() = requireActivity() as CompradorActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val nombre = Repositorio.usuarioActual?.nombre?.substringBefore(" ").orEmpty()
        binding.encabezado.configurar(Rol.COMPRADOR, getString(R.string.inicio_pregunta), getString(R.string.hola, nombre))
        binding.textoDireccion.text = getString(R.string.entregar_en, getString(R.string.direccion_ejemplo).substringBeforeLast(","))

        binding.barraBusqueda.setOnClickListener {
            actividad.enfocarBusqueda = true
            actividad.irA(R.id.catalogoFragment)
        }
        binding.botonVerTodo.setOnClickListener { actividad.irA(R.id.catalogoFragment) }

        binding.listaCategorias.adapter = CategoriaAdapter { categoria ->
            actividad.categoriaPendiente = categoria
            actividad.irA(R.id.catalogoFragment)
        }

        val destacados = ProductoAdapter { producto ->
            findNavController().navigate(R.id.detalleProductoFragment, bundleOf("referencia" to producto.referencia))
        }
        binding.listaDestacados.adapter = destacados
        destacados.actualizar(Repositorio.destacados())
    }
}
