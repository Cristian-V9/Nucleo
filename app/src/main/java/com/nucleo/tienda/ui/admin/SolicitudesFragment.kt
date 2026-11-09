package com.nucleo.tienda.ui.admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.data.Usuario
import com.nucleo.tienda.databinding.FragmentSolicitudesBinding
import com.nucleo.tienda.databinding.ItemSolicitudBinding
import com.nucleo.tienda.ui.comun.Dialogos
import com.nucleo.tienda.ui.comun.configurar
import com.nucleo.tienda.util.comoTexto
import com.nucleo.tienda.util.mostrarMensaje
import com.nucleo.tienda.util.pintarEstado

/** Verificación de comercios: el administrador revisa documentos y aprueba o rechaza (RF-03). */
class SolicitudesFragment : Fragment() {

    // ViewBinding: _binding vive mientras exista la vista del fragment.
    private var _binding: FragmentSolicitudesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSolicitudesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val adaptador = SolicitudAdapter(
        alVerDocumento = { binding.root.mostrarMensaje(getString(R.string.proxima_version)) },
        alAprobar = { u ->
            Repositorio.resolverSolicitud(u.id, aprobada = true)
            cargar()
            binding.root.mostrarMensaje(getString(R.string.solicitud_aprobada, u.nombreVisible))
        },
        alRechazar = { u ->
            Dialogos.pedirTexto(requireContext(), getString(R.string.rechazar_titulo), getString(R.string.rechazar_mensaje),
                getString(R.string.motivo), getString(R.string.rechazar)) { motivo ->
                if (motivo.isEmpty()) {
                    binding.root.mostrarMensaje(getString(R.string.motivo_obligatorio))
                    return@pedirTexto
                }
                Repositorio.resolverSolicitud(u.id, aprobada = false)
                cargar()
                binding.root.mostrarMensaje(getString(R.string.solicitud_rechazada))
            }
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.encabezado.configurar(getString(R.string.solicitudes_titulo)) { findNavController().navigateUp() }
        binding.listaSolicitudes.adapter = adaptador
        cargar()
    }

    private fun cargar() {
        val pendientes = Repositorio.solicitudesPendientes()
        adaptador.actualizar(pendientes)
        binding.textoVacio.isVisible = pendientes.isEmpty()
    }

    class SolicitudAdapter(
        private val alVerDocumento: () -> Unit,
        private val alAprobar: (Usuario) -> Unit,
        private val alRechazar: (Usuario) -> Unit
    ) : RecyclerView.Adapter<SolicitudAdapter.Vista>() {

        private var solicitudes: List<Usuario> = emptyList()

        @SuppressLint("NotifyDataSetChanged")
        fun actualizar(lista: List<Usuario>) {
            solicitudes = lista
            notifyDataSetChanged()
        }

        class Vista(val binding: ItemSolicitudBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            Vista(ItemSolicitudBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun getItemCount() = solicitudes.size

        override fun onBindViewHolder(holder: Vista, position: Int) {
            val u = solicitudes[position]
            val c = u.comercio ?: return
            val contexto = holder.itemView.context
            with(holder.binding) {
                razonSocial.text = c.razonSocial
                fecha.text = contexto.getString(R.string.solicitado_el, u.fechaRegistro.comoTexto())
                estado.pintarEstado(u.estado)
                datos.text = listOf(
                    "${contexto.getString(R.string.nit)}: ${c.nit}",
                    "${contexto.getString(R.string.telefono)}: ${c.telefono}",
                    c.direccion,
                    "${u.nombre} · ${u.correo}"
                ).joinToString("\n")
                documentoRut.setOnClickListener { alVerDocumento() }
                documentoMatricula.setOnClickListener { alVerDocumento() }
                botonAprobar.setOnClickListener { alAprobar(u) }
                botonRechazar.setOnClickListener { alRechazar(u) }
            }
        }
    }
}
