package com.nucleo.tienda.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Comercio
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.data.Rol
import com.nucleo.tienda.databinding.FragmentRegistroComercioBinding
import com.nucleo.tienda.databinding.IncludeDocumentoBinding
import com.nucleo.tienda.ui.comun.TipoAviso
import com.nucleo.tienda.ui.comun.configurar
import com.nucleo.tienda.ui.comun.marcarCargado
import com.nucleo.tienda.ui.comun.mostrar
import com.nucleo.tienda.util.mostrarMensaje
import com.nucleo.tienda.util.textoObligatorio

/**
 * Segundo paso del registro de un vendedor. Los documentos se adjuntan con la cámara
 * (foto) o con el selector de archivos del sistema (PDF); la cuenta queda pendiente de aprobación.
 */
class RegistroComercioFragment : Fragment() {

    // ViewBinding: _binding vive mientras exista la vista del fragment.
    private var _binding: FragmentRegistroComercioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegistroComercioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private var archivoRut: String? = null
    private var archivoMatricula: String? = null
    private var documentoEnCurso: String = RUT

    private val tomarFoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { foto ->
        if (foto != null) documentoCargado("foto.jpg")
    }

    private val elegirPdf = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) documentoCargado(if (documentoEnCurso == RUT) "rut.pdf" else "matricula.pdf")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.encabezado.configurar(getString(R.string.registro_comercio_titulo)) { findNavController().navigateUp() }
        binding.aviso.mostrar(getString(R.string.aviso_revision), TipoAviso.ADVERTENCIA)

        configurarDocumento(binding.documentoRut, getString(R.string.rut), RUT)
        configurarDocumento(binding.documentoMatricula, getString(R.string.matricula), MATRICULA)
        binding.botonEnviar.setOnClickListener { enviar() }
    }

    private fun configurarDocumento(documento: IncludeDocumentoBinding, titulo: String, clave: String) {
        documento.titulo.text = titulo
        documento.marcarCargado(if (clave == RUT) archivoRut else archivoMatricula)
        documento.botonFoto.setOnClickListener {
            documentoEnCurso = clave
            lanzarSeguro { tomarFoto.launch(null) }
        }
        documento.botonPdf.setOnClickListener {
            documentoEnCurso = clave
            lanzarSeguro { elegirPdf.launch(arrayOf("application/pdf")) }
        }
    }

    private fun lanzarSeguro(accion: () -> Unit) {
        try {
            accion()
        } catch (e: android.content.ActivityNotFoundException) {
            binding.root.mostrarMensaje(getString(R.string.proxima_version))
        }
    }

    private fun documentoCargado(nombre: String) {
        if (documentoEnCurso == RUT) {
            archivoRut = nombre
            binding.documentoRut.marcarCargado(nombre)
        } else {
            archivoMatricula = nombre
            binding.documentoMatricula.marcarCargado(nombre)
        }
    }

    private fun enviar() {
        val obligatorio = getString(R.string.campo_obligatorio)
        val razonSocial = binding.campoRazonSocial.textoObligatorio(obligatorio)
        val nit = binding.campoNit.textoObligatorio(obligatorio)
        val telefono = binding.campoTelefono.textoObligatorio(obligatorio)
        val direccion = binding.campoDireccion.textoObligatorio(obligatorio)
        if (razonSocial == null || nit == null || telefono == null || direccion == null) return
        if (archivoRut == null || archivoMatricula == null) {
            binding.root.mostrarMensaje(getString(R.string.documentos_faltantes))
            return
        }

        val datos = requireArguments()
        Repositorio.registrar(
            nombre = datos.getString("nombre").orEmpty(),
            correo = datos.getString("correo").orEmpty(),
            celular = datos.getString("celular").orEmpty(),
            clave = datos.getString("clave").orEmpty(),
            rol = Rol.VENDEDOR,
            comercio = Comercio(razonSocial, nit, telefono, direccion, 15_000, "1 a 2 días")
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.solicitud_enviada_titulo)
            .setMessage(R.string.solicitud_enviada_mensaje)
            .setCancelable(false)
            .setPositiveButton(R.string.volver_inicio_sesion) { _, _ ->
                findNavController().popBackStack(R.id.loginFragment, false)
            }
            .show()
    }

    private companion object {
        const val RUT = "rut"
        const val MATRICULA = "matricula"
    }
}
