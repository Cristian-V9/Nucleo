package com.nucleo.tienda.ui.comun

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Pedido
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.databinding.ItemPedidoBinding
import com.nucleo.tienda.util.comoPesos
import com.nucleo.tienda.util.comoTexto
import com.nucleo.tienda.util.pintarEstado

/** Lista compacta de pedidos (item_pedido.xml). Muestra el comercio o el comprador según quién la vea. */
class PedidoAdapter(private val pedidos: List<Pedido>, private val mostrarComprador: Boolean = false) :
    RecyclerView.Adapter<PedidoAdapter.Vista>() {

    class Vista(val binding: ItemPedidoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Vista(ItemPedidoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = pedidos.size

    override fun onBindViewHolder(holder: Vista, position: Int) {
        val pedido = pedidos[position]
        val contexto = holder.itemView.context
        val persona = if (mostrarComprador) Repositorio.usuario(pedido.compradorId)?.nombre
        else Repositorio.usuario(pedido.vendedorId)?.nombreVisible?.removeSuffix(" S.A.S.")
        holder.binding.titulo.text = contexto.getString(R.string.pedido_titulo, pedido.id, persona.orEmpty())
        holder.binding.resumen.text = contexto.getString(R.string.pedido_resumen,
            contexto.resources.getQuantityString(R.plurals.productos, pedido.cantidadProductos, pedido.cantidadProductos),
            pedido.total.comoPesos(), pedido.fecha.comoTexto())
        holder.binding.estado.pintarEstado(pedido.estado)
    }
}
