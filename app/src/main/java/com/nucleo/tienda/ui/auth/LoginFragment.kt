package com.nucleo.tienda.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nucleo.tienda.R
import com.nucleo.tienda.data.EstadoCuenta
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.data.Usuario
import com.nucleo.tienda.databinding.FragmentLoginBinding
import com.nucleo.tienda.ui.comun.abrirInicioDeRol
import com.nucleo.tienda.util.Biometria
import com.nucleo.tienda.util.Sesion
import com.nucleo.tienda.util.esCorreoValido
import com.nucleo.tienda.util.mostrarMensaje
import com.nucleo.tienda.util.textoObligatorio

class LoginFragment : Fragment() {

    // ViewBinding: _binding vive mientras exista la vista del fragment.
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Sesion.ultimoCorreo(requireContext())?.let { binding.campoCorreo.editText?.setText(it) }

        binding.botonIngresar.setOnClickListener { ingresarConClave() }
        binding.campoClave.editText?.setOnEditorActionListener { _, accion, _ ->
            if (accion == EditorInfo.IME_ACTION_DONE) ingresarConClave()
            false
        }
        binding.botonHuella.setOnClickListener { ingresarConHuella() }
        binding.botonOlvide.setOnClickListener { findNavController().navigate(R.id.aRecuperar) }
        binding.botonCrearCuenta.setOnClickListener { findNavController().navigate(R.id.aRegistro) }
    }

    private fun ingresarConClave() {
        val correo = binding.campoCorreo.textoObligatorio(getString(R.string.campo_obligatorio))
        val clave = binding.campoClave.textoObligatorio(getString(R.string.campo_obligatorio))
        if (correo == null || clave == null) return
        if (!esCorreoValido(correo)) {
            binding.campoCorreo.error = getString(R.string.correo_invalido)
            return
        }
        val usuario = Repositorio.validarCredenciales(correo, clave)
        if (usuario == null) {
            // Mensaje genérico: no revela si la cuenta existe (RF-01).
            binding.campoClave.error = getString(R.string.credenciales_incorrectas)
            return
        }
        entrar(usuario)
    }

    private fun ingresarConHuella() {
        val correo = Sesion.ultimoCorreo(requireContext())
        val usuario = correo?.let { Repositorio.buscarPorCorreo(it) }
        when {
            usuario == null -> binding.root.mostrarMensaje(getString(R.string.huella_sin_usuario))
            !Sesion.huellaActiva(requireContext()) -> binding.root.mostrarMensaje(getString(R.string.huella_desactivada))
            !Biometria.disponible(this) -> binding.root.mostrarMensaje(getString(R.string.huella_no_disponible))
            else -> Biometria.autenticar(
                fragment = this,
                titulo = getString(R.string.huella_titulo),
                subtitulo = getString(R.string.huella_subtitulo, usuario.nombre),
                textoCancelar = getString(R.string.huella_usar_clave),
                alAutenticar = { entrar(usuario) },
                alFallar = { binding.root.mostrarMensaje(it) }
            )
        }
    }

    /** Verifica el estado de la cuenta y abre la pantalla de inicio de su rol. */
    private fun entrar(usuario: Usuario) {
        val aviso = when (usuario.estado) {
            EstadoCuenta.ACTIVA -> null
            EstadoCuenta.PENDIENTE -> R.string.cuenta_pendiente
            EstadoCuenta.RECHAZADA -> R.string.cuenta_rechazada
            EstadoCuenta.INACTIVA -> R.string.cuenta_inactiva
        }
        if (aviso != null) {
            binding.root.mostrarMensaje(getString(aviso))
            return
        }
        Repositorio.iniciarSesion(usuario)
        Sesion.guardarUltimoCorreo(requireContext(), usuario.correo)
        abrirInicioDeRol(requireActivity(), usuario.rol)
    }
}
