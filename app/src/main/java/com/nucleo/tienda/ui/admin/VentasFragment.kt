package com.nucleo.tienda.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.data.Rol
import com.nucleo.tienda.databinding.FragmentVentasBinding
import com.nucleo.tienda.databinding.IncludeEstadisticaBinding
import com.nucleo.tienda.databinding.ItemBarraBinding
import com.nucleo.tienda.databinding.ItemVentaComercioBinding
import com.nucleo.tienda.ui.comun.configurar
import com.nucleo.tienda.util.abreviado
import com.nucleo.tienda.util.comoPesos

/** Reporte de ventas: totales, gráfica de los últimos 6 meses, ventas por comercio y más vendidos. */
class VentasFragment : Fragment() {

    // ViewBinding: _binding vive mientras exista la vista del fragment.
    private var _binding: FragmentVentasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVentasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.encabezado.configurar(Rol.ADMIN, getString(R.string.reporte_ventas), getString(R.string.ultimos_meses))

        val meses = Repositorio.ventasPorMes()
        val total = meses.sumOf { it.total }
        val pedidos = meses.sumOf { it.pedidos }
        binding.estadisticaTotal.llenar(R.string.total_vendido, total.comoPesos(), R.drawable.ic_billete)
        binding.estadisticaPedidos.llenar(R.string.pedidos, pedidos.toString(), R.drawable.ic_caja)
        binding.estadisticaPromedio.llenar(R.string.ticket_promedio, (if (pedidos > 0) total / pedidos else 0).comoPesos(), R.drawable.ic_grafica)

        dibujarGrafica()
        llenarComercios()
        llenarMasVendidos()
    }

    private fun IncludeEstadisticaBinding.llenar(etiqueta: Int, valor: String, icono: Int) {
        this.etiqueta.setText(etiqueta)
        this.valor.text = valor
        this.icono.setImageResource(icono)
    }

    /** Gráfica de barras con vistas: el alto de cada barra es proporcional a las ventas del mes. */
    private fun dibujarGrafica() {
        val meses = Repositorio.ventasPorMes()
        val maximo = meses.maxOf { it.total }.coerceAtLeast(1)
        val altoMaximo = resources.getDimensionPixelSize(R.dimen.alto_grafica) - resources.getDimensionPixelSize(R.dimen.espacio_xl) * 2
        meses.forEach { mes ->
            val columna = ItemBarraBinding.inflate(layoutInflater, binding.grafica, true)
            columna.mes.text = mes.mes.abreviado()
            columna.valor.text = if (mes.total >= 1_000_000) "%.1fM".format(mes.total / 1_000_000.0) else "${mes.total / 1000}k"
            columna.barra.layoutParams.height = (altoMaximo * mes.total / maximo).toInt().coerceAtLeast(4)
            if (mes == meses.last()) columna.barra.backgroundTintList = null
            else columna.barra.alpha = 0.55f
        }
    }

    private fun llenarComercios() {
        val ventas = Repositorio.ventasPorComercio()
        val maximo = ventas.maxOf { it.total }
        ventas.forEach { v ->
            val fila = ItemVentaComercioBinding.inflate(layoutInflater, binding.listaComercios, true)
            fila.nombre.text = v.comercio
            fila.valor.text = v.total.comoPesos()
            fila.detalle.text = getString(R.string.n_pedidos, v.pedidos)
            fila.proporcion.setProgressCompat((100 * v.total / maximo).toInt(), false)
        }
    }

    private fun llenarMasVendidos() {
        val productos = Repositorio.productosMasVendidos()
        val maximo = productos.maxOf { it.unidades }
        productos.forEachIndexed { i, p ->
            val fila = ItemVentaComercioBinding.inflate(layoutInflater, binding.listaMasVendidos, true)
            fila.nombre.text = "${i + 1}. ${p.nombre}"
            fila.valor.text = p.total.comoPesos()
            fila.detalle.text = getString(R.string.unidades, p.unidades)
            fila.proporcion.setProgressCompat(100 * p.unidades / maximo, false)
        }
    }
}
