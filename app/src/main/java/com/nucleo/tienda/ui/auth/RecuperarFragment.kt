package com.nucleo.tienda.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nucleo.tienda.R
import com.nucleo.tienda.databinding.FragmentRecuperarBinding
import com.nucleo.tienda.ui.comun.configurar
import com.nucleo.tienda.util.esCorreoValido
import com.nucleo.tienda.util.textoObligatorio

/**
 * Recuperación de contraseña. En la siguiente versión el envío lo hará Firebase Authentication;
 * el mensaje es el mismo exista o no la cuenta, para no revelar qué correos están registrados.
 */
class RecuperarFragment : Fragment() {

    // ViewBinding: _binding vive mientras exista la vista del fragment.
    private var _binding: FragmentRecuperarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecuperarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.encabezado.configurar(getString(R.string.recuperar_titulo)) { findNavController().navigateUp() }
        binding.botonEnviar.setOnClickListener { enviar() }
        binding.campoCorreo.editText?.setOnEditorActionListener { _, accion, _ ->
            if (accion == EditorInfo.IME_ACTION_SEND) enviar()
            false
        }
        binding.botonVolverLogin.setOnClickListener { findNavController().navigateUp() }
    }

    private fun enviar() {
        val correo = binding.campoCorreo.textoObligatorio(getString(R.string.campo_obligatorio)) ?: return
        if (!esCorreoValido(correo)) {
            binding.campoCorreo.error = getString(R.string.correo_invalido)
            return
        }
        binding.textoEnviado.text = getString(R.string.recuperar_enviado_mensaje, correo)
        binding.grupoFormulario.isVisible = false
        binding.grupoEnviado.isVisible = true
    }
}
