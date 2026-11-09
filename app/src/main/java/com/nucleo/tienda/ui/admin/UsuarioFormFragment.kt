package com.nucleo.tienda.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nucleo.tienda.R
import com.nucleo.tienda.data.EstadoCuenta
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.data.Rol
import com.nucleo.tienda.databinding.FragmentUsuarioFormBinding
import com.nucleo.tienda.ui.comun.configurar
import com.nucleo.tienda.ui.comun.mostrar
import com.nucleo.tienda.util.esCorreoValido
import com.nucleo.tienda.util.mostrarMensaje
import com.nucleo.tienda.util.textoObligatorio

/** Crear o editar un usuario desde el panel del administrador (RF-04). */
class UsuarioFormFragment : Fragment() {

    // ViewBinding: _binding vive mientras exista la vista del fragment.
    private var _binding: FragmentUsuarioFormBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUsuarioFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private var usuarioId: String? = null
    private var rol = Rol.COMPRADOR

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        usuarioId = arguments?.getString("usuarioId")
        val usuario = usuarioId?.let { Repositorio.usuario(it) }
        binding.encabezado.configurar(getString(if (usuario == null) R.string.nuevo_usuario else R.string.editar_usuario)) {
            findNavController().navigateUp()
        }

        binding.listaRoles.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, Rol.entries.map { it.titulo }))
        binding.listaRoles.setOnItemClickListener { _, _, posicion, _ -> rol = Rol.entries[posicion] }

        if (usuario != null) {
            rol = usuario.rol
            binding.campoNombre.editText?.setText(usuario.nombre)
            binding.campoCorreo.editText?.setText(usuario.correo)
            binding.campoCorreo.isEnabled = false // El correo solo lo cambia el propio usuario.
            binding.campoCelular.editText?.setText(usuario.celular)
            binding.interruptorActiva.isChecked = usuario.estado != EstadoCuenta.INACTIVA
            binding.aviso.root.isVisible = false
        } else {
            binding.aviso.mostrar(getString(R.string.aviso_clave_temporal))
        }
        binding.listaRoles.setText(rol.titulo, false)
        binding.botonGuardar.setOnClickListener { guardar() }
    }

    private fun guardar() {
        val obligatorio = getString(R.string.campo_obligatorio)
        val nombre = binding.campoNombre.textoObligatorio(obligatorio)
        val correo = binding.campoCorreo.textoObligatorio(obligatorio)
        val celular = binding.campoCelular.textoObligatorio(obligatorio)
        if (nombre == null || correo == null || celular == null) return
        val activa = binding.interruptorActiva.isChecked

        val id = usuarioId
        if (id == null) {
            when {
                !esCorreoValido(correo) -> { binding.campoCorreo.error = getString(R.string.correo_invalido); return }
                Repositorio.existeCorreo(correo) -> { binding.campoCorreo.error = getString(R.string.correo_registrado); return }
            }
            Repositorio.crearUsuario(nombre, correo, celular, rol, activa)
        } else {
            Repositorio.actualizarUsuario(id, nombre, celular, rol, activa)
        }
        findNavController().navigateUp()
        requireActivity().findViewById<View>(android.R.id.content).mostrarMensaje(getString(R.string.usuario_guardado))
    }
}
