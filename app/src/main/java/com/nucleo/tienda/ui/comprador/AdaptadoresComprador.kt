package com.nucleo.tienda.ui.comprador

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Categoria
import com.nucleo.tienda.data.ItemCarrito
import com.nucleo.tienda.data.Producto
import com.nucleo.tienda.data.ProductoCatalogo
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.databinding.ItemCarritoBinding
import com.nucleo.tienda.databinding.ItemCategoriaBinding
import com.nucleo.tienda.databinding.ItemFichaBinding
import com.nucleo.tienda.databinding.ItemOfertaBinding
import com.nucleo.tienda.databinding.ItemProductoTarjetaBinding
import com.nucleo.tienda.util.comoPesos

/** Tarjetas del catálogo (item_producto_tarjeta.xml). */
class ProductoAdapter(private val alPulsar: (ProductoCatalogo) -> Unit) :
    RecyclerView.Adapter<ProductoAdapter.Vista>() {

    private var productos: List<ProductoCatalogo> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun actualizar(lista: List<ProductoCatalogo>) {
        productos = lista
        notifyDataSetChanged()
    }

    class Vista(val binding: ItemProductoTarjetaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Vista(ItemProductoTarjetaBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = productos.size

    override fun onBindViewHolder(holder: Vista, position: Int) {
        val producto = productos[position]
        val contexto = holder.itemView.context
        with(holder.binding) {
            icono.setImageResource(producto.categoria.icono)
            nombre.text = producto.nombre
            precio.text = contexto.getString(R.string.desde, producto.precioDesde.comoPesos())
            comercios.text = contexto.resources.getQuantityString(R.plurals.comercios, producto.numeroComercios, producto.numeroComercios)
            root.setOnClickListener { alPulsar(producto) }
        }
    }
}

/** Accesos a categorías en el inicio (item_categoria.xml). */
class CategoriaAdapter(private val alPulsar: (Categoria) -> Unit) : RecyclerView.Adapter<CategoriaAdapter.Vista>() {

    private val categorias = Categoria.entries

    class Vista(val binding: ItemCategoriaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Vista(ItemCategoriaBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = categorias.size

    override fun onBindViewHolder(holder: Vista, position: Int) {
        val categoria = categorias[position]
        holder.binding.icono.setImageResource(categoria.icono)
        holder.binding.nombre.text = categoria.titulo
        holder.binding.root.setOnClickListener { alPulsar(categoria) }
    }
}

/** Datos de la ficha técnica (item_ficha.xml). */
class FichaAdapter(private val datos: List<Pair<String, String>>) : RecyclerView.Adapter<FichaAdapter.Vista>() {

    class Vista(val binding: ItemFichaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Vista(ItemFichaBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = datos.size

    override fun onBindViewHolder(holder: Vista, position: Int) {
        holder.binding.clave.text = datos[position].first
        holder.binding.valor.text = datos[position].second
    }
}

/** Comercios que venden el mismo producto, con selección única (item_oferta.xml). */
class OfertaAdapter(
    private val ofertas: List<Producto>,
    private val alElegir: (Producto) -> Unit
) : RecyclerView.Adapter<OfertaAdapter.Vista>() {

    private var elegida = 0

    class Vista(val binding: ItemOfertaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Vista(ItemOfertaBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = ofertas.size

    override fun onBindViewHolder(holder: Vista, position: Int) {
        val oferta = ofertas[position]
        val comercio = Repositorio.usuario(oferta.vendedorId)?.comercio
        val contexto = holder.itemView.context
        with(holder.binding) {
            this.comercio.text = Repositorio.usuario(oferta.vendedorId)?.nombreVisible?.removeSuffix(" S.A.S.")
            detalle.text = contexto.getString(R.string.envio_stock, comercio?.tiempoEntrega.orEmpty(), oferta.stock)
            precio.text = oferta.precio.comoPesos()
            root.isChecked = position == elegida
            precio.setTextColor(contexto.getColor(if (position == elegida) R.color.acento_comprador else R.color.texto))
            root.setOnClickListener {
                val anterior = elegida
                elegida = holder.bindingAdapterPosition
                notifyItemChanged(anterior)
                notifyItemChanged(elegida)
                alElegir(oferta)
            }
        }
    }
}

/** Productos del carrito con selector de cantidad (item_carrito.xml). */
class CarritoAdapter(
    private val alCambiarCantidad: (ItemCarrito, Int) -> Unit,
    private val alQuitar: (ItemCarrito) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.Vista>() {

    private var items: List<ItemCarrito> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun actualizar(lista: List<ItemCarrito>) {
        items = lista
        notifyDataSetChanged()
    }

    class Vista(val binding: ItemCarritoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Vista(ItemCarritoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Vista, position: Int) {
        val item = items[position]
        val producto = Repositorio.producto(item.productoId) ?: return
        with(holder.binding) {
            icono.setImageResource(producto.categoria.icono)
            nombre.text = producto.nombre
            precio.text = producto.precio.comoPesos()
            cantidad.text = item.cantidad.toString()
            botonMenos.isEnabled = item.cantidad > 1
            botonMas.isEnabled = item.cantidad < producto.stock
            botonMenos.setOnClickListener { alCambiarCantidad(item, -1) }
            botonMas.setOnClickListener { alCambiarCantidad(item, 1) }
            botonQuitar.setOnClickListener { alQuitar(item) }
        }
    }
}
