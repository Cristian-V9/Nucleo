package com.nucleo.tienda.ui.comprador

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Categoria
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.data.Rol
import com.nucleo.tienda.databinding.FragmentCatalogoBinding
import com.nucleo.tienda.ui.comun.configurar
import com.nucleo.tienda.util.texto

class CatalogoFragment : Fragment() {

    // ViewBinding: _binding vive mientras exista la vista del fragment.
    private var _binding: FragmentCatalogoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCatalogoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private lateinit var adaptador: ProductoAdapter
    private var categoria: Categoria? = null
    private var ascendente = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.encabezado.configurar(Rol.COMPRADOR, getString(R.string.catalogo_titulo))

        adaptador = ProductoAdapter { producto ->
            findNavController().navigate(R.id.detalleProductoFragment, bundleOf("referencia" to producto.referencia))
        }
        binding.listaProductos.adapter = adaptador

        crearChipsDeCategoria()
        binding.grupoCategorias.setOnCheckedStateChangeListener { grupo, ids ->
            categoria = ids.firstOrNull()?.let { grupo.findViewById<Chip>(it).tag as? Categoria }
            filtrar()
        }
        binding.grupoOrden.addOnButtonCheckedListener { _, id, marcado ->
            if (marcado) {
                ascendente = id == R.id.botonMenorPrecio
                filtrar()
            }
        }
        binding.campoBuscar.editText?.doAfterTextChanged { filtrar() }

        aplicarFiltroPendiente()
        filtrar()
    }

    /** Un Chip por categoría, creado desde el enum para no repetir XML. */
    private fun crearChipsDeCategoria() {
        Categoria.entries.forEach { cat ->
            val chip = Chip(requireContext()).apply {
                id = View.generateViewId()
                text = cat.titulo
                tag = cat
                isCheckable = true
            }
            binding.grupoCategorias.addView(chip)
        }
    }

    /** Lee lo que dejó la pantalla de inicio: una categoría elegida o la búsqueda enfocada. */
    private fun aplicarFiltroPendiente() {
        val actividad = requireActivity() as CompradorActivity
        actividad.categoriaPendiente?.let { cat ->
            (0 until binding.grupoCategorias.childCount)
                .map { binding.grupoCategorias.getChildAt(it) as Chip }
                .firstOrNull { it.tag == cat }?.isChecked = true
            categoria = cat
        }
        if (actividad.enfocarBusqueda) {
            val campo = binding.campoBuscar.editText
            campo?.requestFocus()
            campo?.post { requireContext().getSystemService<InputMethodManager>()?.showSoftInput(campo, 0) }
        }
        actividad.categoriaPendiente = null
        actividad.enfocarBusqueda = false
    }

    private fun filtrar() {
        val resultados = Repositorio.catalogo(categoria, binding.campoBuscar.texto(), ascendente)
        adaptador.actualizar(resultados)
        binding.textoResultados.text = getString(R.string.resultados, resultados.size)
        binding.textoVacio.isVisible = resultados.isEmpty()
    }
}
