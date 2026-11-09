package com.nucleo.tienda.ui.vendedor

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.imageview.ShapeableImageView
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Categoria
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.databinding.FragmentProductoFormBinding
import com.nucleo.tienda.ui.comun.TipoAviso
import com.nucleo.tienda.ui.comun.configurar
import com.nucleo.tienda.ui.comun.mostrar
import com.nucleo.tienda.util.mostrarMensaje
import com.nucleo.tienda.util.texto
import com.nucleo.tienda.util.textoObligatorio

/**
 * Crear o editar un producto. Las fotos se toman con la cámara o se eligen de la galería
 * (en esta versión se muestran como vista previa; su almacenamiento llega con Firebase Storage).
 */
class ProductoFormFragment : Fragment() {

    // ViewBinding: _binding vive mientras exista la vista del fragment.
    private var _binding: FragmentProductoFormBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductoFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private var productoId: String? = null
    private var categoria: Categoria? = null
    private var fotos = 0

    private val tomarFoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { foto: Bitmap? ->
        if (foto != null) agregarFoto { it.setImageBitmap(foto) }
    }

    private val elegirFoto = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) agregarFoto { it.setImageURI(uri) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        productoId = arguments?.getString("productoId")
        val titulo = if (productoId == null) R.string.nuevo_producto else R.string.editar_producto
        binding.encabezado.configurar(getString(titulo)) { findNavController().navigateUp() }
        binding.aviso.mostrar(getString(R.string.aviso_productos_nuevos), TipoAviso.INFO)

        // Lista desplegable con las categorías
        val nombres = Categoria.entries.map { it.titulo }
        binding.listaCategorias.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, nombres))
        binding.listaCategorias.setOnItemClickListener { _, _, posicion, _ -> categoria = Categoria.entries[posicion] }

        binding.botonCamara.setOnClickListener {
            lanzar { tomarFoto.launch(null) }
        }
        binding.botonGaleria.setOnClickListener {
            lanzar { elegirFoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
        }
        binding.botonGuardar.setOnClickListener { guardar() }

        productoId?.let { cargar(it) }
    }

    private fun cargar(id: String) {
        val p = Repositorio.producto(id) ?: return
        categoria = p.categoria
        binding.campoNombre.editText?.setText(p.nombre)
        binding.listaCategorias.setText(p.categoria.titulo, false)
        binding.campoMarca.editText?.setText(p.marca)
        binding.campoReferencia.editText?.setText(p.referencia)
        binding.campoPrecio.editText?.setText(p.precio.toString())
        binding.campoStock.editText?.setText(p.stock.toString())
        binding.campoFicha.editText?.setText(p.ficha.joinToString("\n") { "${it.first}: ${it.second}" })
    }

    private fun lanzar(accion: () -> Unit) {
        if (fotos >= MAXIMO_FOTOS) {
            binding.root.mostrarMensaje(getString(R.string.maximo_fotos))
            return
        }
        try {
            accion()
        } catch (e: android.content.ActivityNotFoundException) {
            binding.root.mostrarMensaje(getString(R.string.proxima_version))
        }
    }

    /** Agrega una miniatura redondeada al recuadro de fotos. */
    private fun agregarFoto(cargarImagen: (ShapeableImageView) -> Unit) {
        binding.textoSinFotos.isVisible = false
        val tamano = resources.getDimensionPixelSize(R.dimen.miniatura)
        val miniatura = ShapeableImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(0, tamano, 1f).apply { marginEnd = 6 }
            scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            shapeAppearanceModel = shapeAppearanceModel.withCornerSize(resources.getDimension(R.dimen.radio_s))
            contentDescription = getString(R.string.fotos_producto)
        }
        cargarImagen(miniatura)
        binding.contenedorFotos.addView(miniatura)
        fotos++
    }

    private fun guardar() {
        val obligatorio = getString(R.string.campo_obligatorio)
        val nombre = binding.campoNombre.textoObligatorio(obligatorio)
        val marca = binding.campoMarca.textoObligatorio(obligatorio)
        val referencia = binding.campoReferencia.textoObligatorio(obligatorio)
        val precioTexto = binding.campoPrecio.textoObligatorio(obligatorio)
        val stockTexto = binding.campoStock.textoObligatorio(obligatorio)
        binding.campoCategoria.error = if (categoria == null) obligatorio else null
        if (nombre == null || marca == null || referencia == null || precioTexto == null || stockTexto == null || categoria == null) return

        val precio = precioTexto.toLongOrNull() ?: 0
        val stock = stockTexto.toIntOrNull() ?: -1
        binding.campoPrecio.error = if (precio <= 0) getString(R.string.precio_invalido) else null
        binding.campoStock.error = if (stock < 0) getString(R.string.stock_invalido) else null
        if (precio <= 0 || stock < 0) return

        // Cada línea "Característica: valor" se convierte en un dato de la ficha técnica.
        val ficha = binding.campoFicha.texto().lines()
            .mapNotNull { linea ->
                val partes = linea.split(":", limit = 2)
                if (partes.size == 2 && partes[0].isNotBlank()) partes[0].trim() to partes[1].trim() else null
            }

        Repositorio.guardarProducto(productoId, nombre, categoria!!, marca, referencia.uppercase(), precio, stock, ficha)
        findNavController().navigateUp()
        requireActivity().findViewById<View>(android.R.id.content)
            .mostrarMensaje(getString(R.string.producto_guardado))
    }

    private companion object {
        const val MAXIMO_FOTOS = 3
    }
}
