package com.nucleo.tienda.ui.comprador

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Producto
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.databinding.FragmentDetalleProductoBinding
import com.nucleo.tienda.ui.comun.configurar
import com.nucleo.tienda.util.comoPesos
import com.nucleo.tienda.util.mostrarMensaje

class DetalleProductoFragment : Fragment() {

    // ViewBinding: _binding vive mientras exista la vista del fragment.
    private var _binding: FragmentDetalleProductoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetalleProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private lateinit var elegido: Producto

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.encabezado.configurar(getString(R.string.detalle_titulo)) { findNavController().navigateUp() }

        val referencia = requireArguments().getString("referencia").orEmpty()
        val producto = Repositorio.productoCatalogo(referencia)
        if (producto == null) {
            findNavController().navigateUp()
            return
        }

        binding.icono.setImageResource(producto.categoria.icono)
        binding.textoCategoria.text = getString(R.string.nuevo_sellado, producto.categoria.titulo)
        binding.nombre.text = producto.nombre
        binding.listaFicha.adapter = FichaAdapter(producto.publicaciones.first().ficha)

        val comercios = resources.getQuantityString(R.plurals.comercios, producto.numeroComercios, producto.numeroComercios)
        binding.textoDisponible.text = getString(R.string.disponible_en, comercios)
        binding.listaOfertas.adapter = OfertaAdapter(producto.publicaciones) { mostrarElegido(it) }
        mostrarElegido(producto.publicaciones.first())

        binding.botonAgregar.setOnClickListener { agregar() }
    }

    private fun mostrarElegido(oferta: Producto) {
        elegido = oferta
        binding.textoComercioElegido.text = Repositorio.usuario(oferta.vendedorId)?.nombreVisible
        binding.textoPrecioElegido.text = oferta.precio.comoPesos()
    }

    /** El carrito admite un solo comercio: si hay productos de otro, se pregunta antes de vaciarlo (RF-07). */
    private fun agregar() {
        if (Repositorio.requiereVaciarCarrito(elegido.id)) {
            val actual = Repositorio.vendedorDelCarrito()?.nombreVisible.orEmpty()
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.carrito_otro_comercio_titulo)
                .setMessage(getString(R.string.carrito_otro_comercio_mensaje, actual))
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.vaciar_y_agregar) { _, _ -> confirmarAgregado() }
                .show()
        } else {
            confirmarAgregado()
        }
    }

    private fun confirmarAgregado() {
        Repositorio.agregarAlCarrito(elegido.id)
        val actividad = requireActivity() as CompradorActivity
        actividad.actualizarInsigniaCarrito()
        binding.root.mostrarMensaje(getString(R.string.agregado_carrito), getString(R.string.ver_carrito)) {
            actividad.irA(R.id.carritoFragment)
        }
    }
}
