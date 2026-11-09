package com.nucleo.tienda.ui.vendedor

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.nucleo.tienda.R
import com.nucleo.tienda.data.EstadoPedido
import com.nucleo.tienda.data.Pedido
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.data.Rol
import com.nucleo.tienda.databinding.FragmentPedidosVendedorBinding
import com.nucleo.tienda.databinding.ItemPedidoVendedorBinding
import com.nucleo.tienda.ui.comun.configurar
import com.nucleo.tienda.util.comoPesos
import com.nucleo.tienda.util.comoTexto
import com.nucleo.tienda.util.mostrarMensaje
import com.nucleo.tienda.util.pintarEstado

/** Historial de pedidos recibidos, con cambio de estado (RF-09). */
class PedidosVendedorFragment : Fragment() {

    // ViewBinding: _binding vive mientras exista la vista del fragment.
    private var _binding: FragmentPedidosVendedorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPedidosVendedorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val adaptador = PedidosAdapter { pedido -> avanzar(pedido) }
    private var estado = EstadoPedido.PENDIENTE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.encabezado.configurar(Rol.VENDEDOR, getString(R.string.pedidos_recibidos))
        binding.listaPedidos.adapter = adaptador
        binding.pestanas.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                estado = EstadoPedido.entries[tab.position]
                cargar()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })
        cargar()
    }

    private fun cargar() {
        val vendedor = Repositorio.usuarioActual?.id ?: return
        val pedidos = Repositorio.pedidosDeVendedor(vendedor, estado)
        adaptador.actualizar(pedidos)
        binding.textoVacio.isVisible = pedidos.isEmpty()
        // Número de pedidos en cada pestaña
        EstadoPedido.entries.forEachIndexed { i, e ->
            val cantidad = Repositorio.pedidosDeVendedor(vendedor, e).size
            val titulo = getString(listOf(R.string.pendientes, R.string.enviados, R.string.entregados)[i])
            binding.pestanas.getTabAt(i)?.text = if (cantidad > 0) "$titulo ($cantidad)" else titulo
        }
    }

    private fun avanzar(pedido: Pedido) {
        Repositorio.avanzarPedido(pedido.id)
        cargar()
        binding.root.mostrarMensaje(getString(R.string.pedido_actualizado, pedido.id, pedido.estado.titulo))
    }

    class PedidosAdapter(private val alAvanzar: (Pedido) -> Unit) : RecyclerView.Adapter<PedidosAdapter.Vista>() {
        private var pedidos: List<Pedido> = emptyList()

        @SuppressLint("NotifyDataSetChanged")
        fun actualizar(lista: List<Pedido>) {
            pedidos = lista
            notifyDataSetChanged()
        }

        class Vista(val binding: ItemPedidoVendedorBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            Vista(ItemPedidoVendedorBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun getItemCount() = pedidos.size

        override fun onBindViewHolder(holder: Vista, position: Int) {
            val p = pedidos[position]
            with(holder.binding) {
                numero.text = "#${p.id} · ${p.fecha.comoTexto()}"
                estado.pintarEstado(p.estado)
                comprador.text = Repositorio.usuario(p.compradorId)?.nombre
                productos.text = p.items.joinToString(" · ") { "${it.nombre} ×${it.cantidad}" }
                direccion.text = p.direccion
                progreso.setProgressCompat(p.estado.ordinal, false)
                total.text = p.total.comoPesos()
                botonAvanzar.isVisible = p.estado != EstadoPedido.ENTREGADO
                botonAvanzar.setText(if (p.estado == EstadoPedido.PENDIENTE) R.string.marcar_enviado else R.string.marcar_entregado)
                botonAvanzar.setOnClickListener { alAvanzar(p) }
            }
        }
    }
}
