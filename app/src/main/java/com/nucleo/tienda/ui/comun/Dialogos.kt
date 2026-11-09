package com.nucleo.tienda.ui.comun

import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nucleo.tienda.R
import com.nucleo.tienda.databinding.DialogoTextoBinding

/** Diálogos reutilizables con el estilo de la app (ThemeOverlay.Nucleo.Dialogo). */
object Dialogos {

    /** Diálogo de confirmación con botones Cancelar y la acción indicada. */
    fun confirmar(contexto: Context, titulo: String, mensaje: String, textoAccion: String, alConfirmar: () -> Unit) {
        MaterialAlertDialogBuilder(contexto)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(textoAccion) { _, _ -> alConfirmar() }
            .show()
    }

    /** Diálogo con un campo de texto (un motivo o la contraseña). Entrega lo escrito al confirmar. */
    fun pedirTexto(
        contexto: Context,
        titulo: String,
        mensaje: String?,
        hint: String,
        textoAccion: String,
        esClave: Boolean = false,
        alConfirmar: (String) -> Unit
    ) {
        val vista = DialogoTextoBinding.inflate(LayoutInflater.from(contexto))
        vista.campo.hint = hint
        if (esClave) {
            vista.campo.editText?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        MaterialAlertDialogBuilder(contexto)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setView(vista.root)
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(textoAccion) { _, _ ->
                alConfirmar(vista.campo.editText?.text.toString().trim())
            }
            .show()
    }
}
