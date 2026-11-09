package com.nucleo.tienda.ui.comun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.data.Rol
import com.nucleo.tienda.databinding.DialogoEditarPerfilBinding
import com.nucleo.tienda.databinding.FragmentPerfilBinding
import com.nucleo.tienda.util.Sesion
import com.nucleo.tienda.util.comoPesos
import com.nucleo.tienda.util.mostrarMensaje
import com.nucleo.tienda.util.textoObligatorio

/** Perfil del usuario (RF-10): consultar, editar, foto con la cámara, huella y eliminar cuenta. */
class PerfilFragment : Fragment() {

    // ViewBinding: _binding vive mientras exista la vista del fragment.
    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val tomarFoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { foto ->
        if (foto != null) {
            binding.foto.setImageBitmap(foto)
            binding.foto.isVisible = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val usuario = Repositorio.usuarioActual ?: return
        mostrarDatos()

        binding.botonFoto.setOnClickListener {
            try {
                tomarFoto.launch(null)
            } catch (e: android.content.ActivityNotFoundException) {
                binding.root.mostrarMensaje(getString(R.string.proxima_version))
            }
        }
        binding.botonEditar.setOnClickListener { editar() }

        binding.interruptorHuella.isChecked = Sesion.huellaActiva(requireContext())
        binding.interruptorHuella.setOnCheckedChangeListener { _, activa -> Sesion.cambiarHuella(requireContext(), activa) }

        binding.opcionPrivacidad.icono.setImageResource(R.drawable.ic_privacidad)
        binding.opcionPrivacidad.texto.setText(R.string.privacidad)
        binding.opcionPrivacidad.root.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.privacidad)
                .setMessage(R.string.politica_texto)
                .setPositiveButton(R.string.aceptar, null)
                .show()
        }
        binding.opcionSalir.icono.setImageResource(R.drawable.ic_salir)
        binding.opcionSalir.texto.setText(R.string.cerrar_sesion)
        binding.opcionSalir.root.setOnClickListener {
            Dialogos.confirmar(requireContext(), getString(R.string.cerrar_sesion), getString(R.string.cerrar_sesion_pregunta),
                getString(R.string.cerrar_sesion)) { cerrarSesion(requireActivity()) }
        }
        binding.botonEliminarCuenta.setOnClickListener { eliminarCuenta() }

        // Secciones según el rol
        val comercio = usuario.comercio
        binding.grupoComercio.isVisible = usuario.rol == Rol.VENDEDOR && comercio != null
        if (comercio != null) {
            binding.razonSocial.text = comercio.razonSocial
            binding.datosComercio.text = listOf(
                "${getString(R.string.nit)}: ${comercio.nit}",
                comercio.direccion,
                getString(R.string.envio_comercio, comercio.costoEnvio.comoPesos(), comercio.tiempoEntrega)
            ).joinToString("\n")
        }
        val esComprador = usuario.rol == Rol.COMPRADOR
        binding.grupoPedidos.isVisible = esComprador
        if (esComprador) {
            val pedidos = Repositorio.pedidosDeComprador(usuario.id)
            binding.listaPedidos.adapter = PedidoAdapter(pedidos)
            binding.textoSinPedidos.isVisible = pedidos.isEmpty()
        }
    }

    private fun mostrarDatos() {
        val usuario = Repositorio.usuarioActual ?: return
        binding.iniciales.text = usuario.iniciales
        binding.pildoraRol.text = usuario.rol.titulo
        binding.nombre.text = usuario.nombre
        binding.correo.text = usuario.correo
    }

    private fun editar() {
        val usuario = Repositorio.usuarioActual ?: return
        val vista = DialogoEditarPerfilBinding.inflate(LayoutInflater.from(requireContext()))
        vista.campoNombre.editText?.setText(usuario.nombre)
        vista.campoCelular.editText?.setText(usuario.celular)
        val dialogo = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.editar_perfil)
            .setView(vista.root)
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.guardar, null)
            .create()
        dialogo.setOnShowListener {
            dialogo.getButton(android.content.DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val obligatorio = getString(R.string.campo_obligatorio)
                val nombre = vista.campoNombre.textoObligatorio(obligatorio)
                val celular = vista.campoCelular.textoObligatorio(obligatorio)
                if (nombre != null && celular != null) {
                    Repositorio.actualizarPerfil(nombre, celular)
                    mostrarDatos()
                    dialogo.dismiss()
                    binding.root.mostrarMensaje(getString(R.string.perfil_actualizado))
                }
            }
        }
        dialogo.show()
    }

    /** Eliminar la cuenta pide la contraseña (Ley 1581 de 2012: derecho de supresión). */
    private fun eliminarCuenta() {
        val usuario = Repositorio.usuarioActual ?: return
        Dialogos.pedirTexto(
            contexto = requireContext(),
            titulo = getString(R.string.eliminar_cuenta),
            mensaje = getString(R.string.eliminar_cuenta_mensaje),
            hint = getString(R.string.clave),
            textoAccion = getString(R.string.eliminar),
            esClave = true
        ) { clave ->
            if (clave != usuario.clave) {
                binding.root.mostrarMensaje(getString(R.string.clave_incorrecta))
                return@pedirTexto
            }
            Repositorio.eliminarCuentaActual()
            Sesion.olvidar(requireContext())
            cerrarSesion(requireActivity())
        }
    }
}
