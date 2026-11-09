package com.nucleo.tienda.ui.comun

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Producto
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.databinding.ItemProductoFilaBinding
import com.nucleo.tienda.util.comoPesos

/**
 * Lista de productos con acciones (item_producto_fila.xml).
 * Modo vendedor: editar y eliminar. Modo administrador: ocultar o mostrar la publicación.
 */
class ProductoFilaAdapter(
    private val modoAdmin: Boolean,
    private val alEditar: (Producto) -> Unit = {},
    private val alEliminar: (Producto) -> Unit = {},
    private val alModerar: (Producto) -> Unit = {}
) : RecyclerView.Adapter<ProductoFilaAdapter.Vista>() {

    private var productos: List<Producto> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun actualizar(lista: List<Producto>) {
        productos = lista
        notifyDataSetChanged()
    }

    class Vista(val binding: ItemProductoFilaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Vista(ItemProductoFilaBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = productos.size

    override fun onBindViewHolder(holder: Vista, position: Int) {
        val p = productos[position]
        val contexto = holder.itemView.context
        with(holder.binding) {
            icono.setImageResource(p.categoria.icono)
            nombre.text = p.nombre
            if (modoAdmin) {
                val comercio = Repositorio.usuario(p.vendedorId)?.nombreVisible?.removeSuffix(" S.A.S.").orEmpty()
                detalle.text = contexto.getString(R.string.producto_vendedor, comercio, p.precio.comoPesos(), p.stock)
                detalle.setTextColor(ContextCompat.getColor(contexto, R.color.texto_secundario))
            } else {
                detalle.text = if (p.stock > 0) contexto.getString(R.string.precio_stock, p.precio.comoPesos(), p.stock)
                else contexto.getString(R.string.precio_sin_stock, p.precio.comoPesos())
                detalle.setTextColor(ContextCompat.getColor(contexto, if (p.stock > 0) R.color.texto_secundario else R.color.error))
            }
            aviso.isVisible = p.oculto
            aviso.text = contexto.getString(R.string.oculto_por_admin, p.motivoOculto.orEmpty())

            botonEditar.isVisible = !modoAdmin
            botonEliminar.isVisible = !modoAdmin
            botonModerar.isVisible = modoAdmin
            botonModerar.setText(if (p.oculto) R.string.mostrar else R.string.ocultar)
            botonEditar.setOnClickListener { alEditar(p) }
            botonEliminar.setOnClickListener { alEliminar(p) }
            botonModerar.setOnClickListener { alModerar(p) }
        }
    }
}
