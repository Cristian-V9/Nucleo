package com.nucleo.tienda.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.data.Rol
import com.nucleo.tienda.databinding.FragmentRegistroBinding
import com.nucleo.tienda.ui.comun.abrirInicioDeRol
import com.nucleo.tienda.ui.comun.configurar
import com.nucleo.tienda.util.Sesion
import com.nucleo.tienda.util.esCorreoValido
import com.nucleo.tienda.util.mostrarMensaje
import com.nucleo.tienda.util.textoObligatorio

class RegistroFragment : Fragment() {

    // ViewBinding: _binding vive mientras exista la vista del fragment.
    private var _binding: FragmentRegistroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegistroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private var rolElegido = Rol.COMPRADOR

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.encabezado.configurar(getString(R.string.registro_titulo)) { findNavController().navigateUp() }
        binding.tarjetaComprador.setOnClickListener { elegirRol(Rol.COMPRADOR) }
        binding.tarjetaVendedor.setOnClickListener { elegirRol(Rol.VENDEDOR) }
        binding.botonContinuar.setOnClickListener { continuar() }
        elegirRol(rolElegido)
    }

    private fun elegirRol(rol: Rol) {
        rolElegido = rol
        binding.tarjetaComprador.isChecked = rol == Rol.COMPRADOR
        binding.tarjetaVendedor.isChecked = rol == Rol.VENDEDOR
        binding.botonContinuar.setText(if (rol == Rol.VENDEDOR) R.string.continuar else R.string.crear_cuenta)
    }

    private fun continuar() {
        val obligatorio = getString(R.string.campo_obligatorio)
        val nombre = binding.campoNombre.textoObligatorio(obligatorio)
        val correo = binding.campoCorreo.textoObligatorio(obligatorio)
        val celular = binding.campoCelular.textoObligatorio(obligatorio)
        val clave = binding.campoClave.textoObligatorio(obligatorio)
        if (nombre == null || correo == null || celular == null || clave == null) return

        when {
            !esCorreoValido(correo) -> { binding.campoCorreo.error = getString(R.string.correo_invalido); return }
            Repositorio.existeCorreo(correo) -> { binding.campoCorreo.error = getString(R.string.correo_registrado); return }
            clave.length < 8 -> { binding.campoClave.error = getString(R.string.clave_corta); return }
            !binding.casillaPolitica.isChecked -> { binding.root.mostrarMensaje(getString(R.string.politica_obligatoria)); return }
        }

        if (rolElegido == Rol.VENDEDOR) {
            // El vendedor continúa con los datos legales de su comercio (RF-03).
            val datos = bundleOf("nombre" to nombre, "correo" to correo, "celular" to celular, "clave" to clave)
            findNavController().navigate(R.id.aRegistroComercio, datos)
        } else {
            val usuario = Repositorio.registrar(nombre, correo, celular, clave, Rol.COMPRADOR)
            Repositorio.iniciarSesion(usuario)
            Sesion.guardarUltimoCorreo(requireContext(), usuario.correo)
            abrirInicioDeRol(requireActivity(), Rol.COMPRADOR)
        }
    }
}
