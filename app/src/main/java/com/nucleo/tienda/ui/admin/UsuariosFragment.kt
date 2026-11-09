package com.nucleo.tienda.ui.admin

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.nucleo.tienda.R
import com.nucleo.tienda.data.EstadoCuenta
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.data.Rol
import com.nucleo.tienda.data.Usuario
import com.nucleo.tienda.databinding.FragmentUsuariosBinding
import com.nucleo.tienda.databinding.ItemUsuarioBinding
import com.nucleo.tienda.ui.comun.Dialogos
import com.nucleo.tienda.ui.comun.configurar
import com.nucleo.tienda.util.mostrarMensaje
import com.nucleo.tienda.util.pintarEstado
import com.nucleo.tienda.util.texto

/** Gestión de usuarios: listado, creación, edición y eliminación lógica (RF-04). */
class UsuariosFragment : Fragment() {

    // ViewBinding: _binding vive mientras exista la vista del fragment.
    private var _binding: FragmentUsuariosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUsuariosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val adaptador = UsuarioAdapter(
        alEditar = { abrirFormulario(it.id) },
        alEliminar = { eliminar(it) }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.encabezado.configurar(Rol.ADMIN, getString(R.string.usuarios_titulo), iconoAccion = R.drawable.ic_agregar,
            descripcionAccion = getString(R.string.nuevo_usuario), primaria = true) { abrirFormulario(null) }
        binding.listaUsuarios.adapter = adaptador
        binding.grupoRoles.setOnCheckedStateChangeListener { _, _ -> filtrar() }
        binding.campoBuscar.editText?.doAfterTextChanged { filtrar() }
        filtrar()
    }

    private fun abrirFormulario(id: String?) =
        findNavController().navigate(R.id.usuarioFormFragment, bundleOf("usuarioId" to id))

    private fun eliminar(usuario: Usuario) {
        if (usuario.id == Repositorio.usuarioActual?.id) {
            binding.root.mostrarMensaje(getString(R.string.no_eliminarse))
            return
        }
        Dialogos.confirmar(requireContext(), getString(R.string.eliminar_usuario_titulo),
            getString(R.string.eliminar_usuario_mensaje, usuario.nombreVisible), getString(R.string.eliminar)) {
            Repositorio.eliminarUsuario(usuario.id)
            filtrar()
            binding.root.mostrarMensaje(getString(R.string.usuario_eliminado))
        }
    }

    private fun filtrar() {
        val texto = binding.campoBuscar.texto().lowercase()
        val rol = when (binding.grupoRoles.checkedChipId) {
            R.id.chipCompradores -> Rol.COMPRADOR
            R.id.chipVendedores -> Rol.VENDEDOR
            R.id.chipAdmins -> Rol.ADMIN
            else -> null
        }
        val lista = Repositorio.usuarios()
            .filter { rol == null || it.rol == rol }
            .filter { texto.isEmpty() || it.nombreVisible.lowercase().contains(texto) || it.correo.lowercase().contains(texto) }
        adaptador.actualizar(lista)
        binding.textoVacio.isVisible = lista.isEmpty()
    }

    class UsuarioAdapter(
        private val alEditar: (Usuario) -> Unit,
        private val alEliminar: (Usuario) -> Unit
    ) : RecyclerView.Adapter<UsuarioAdapter.Vista>() {

        private var usuarios: List<Usuario> = emptyList()

        @SuppressLint("NotifyDataSetChanged")
        fun actualizar(lista: List<Usuario>) {
            usuarios = lista
            notifyDataSetChanged()
        }

        class Vista(val binding: ItemUsuarioBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            Vista(ItemUsuarioBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun getItemCount() = usuarios.size

        override fun onBindViewHolder(holder: Vista, position: Int) {
            val u = usuarios[position]
            val contexto = holder.itemView.context
            // Cada rol tiene su color de acento (los mismos de los temas por rol).
            val (color, fondo) = when (u.rol) {
                Rol.COMPRADOR -> R.color.acento_comprador to R.color.acento_comprador_suave
                Rol.VENDEDOR -> R.color.acento_vendedor to R.color.acento_vendedor_suave
                Rol.ADMIN -> R.color.acento_admin to R.color.acento_admin_suave
            }
            with(holder.binding) {
                iniciales.text = u.iniciales
                iniciales.setTextColor(ContextCompat.getColor(contexto, color))
                iniciales.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(contexto, fondo))
                nombre.text = u.nombreVisible
                rol.text = u.rol.titulo
                rol.setTextColor(ContextCompat.getColor(contexto, color))
                estado.pintarEstado(u.estado)
                botonEliminar.isEnabled = u.estado != EstadoCuenta.INACTIVA
                botonEliminar.alpha = if (botonEliminar.isEnabled) 1f else 0.35f
                botonEditar.setOnClickListener { alEditar(u) }
                botonEliminar.setOnClickListener { alEliminar(u) }
            }
        }
    }
}
