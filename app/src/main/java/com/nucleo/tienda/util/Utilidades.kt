package com.nucleo.tienda.util

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.nucleo.tienda.R
import com.nucleo.tienda.data.EstadoCuenta
import com.nucleo.tienda.data.EstadoPedido
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val localeCo = Locale("es", "CO")
private val formatoPesos = DecimalFormat("#,##0", DecimalFormatSymbols(localeCo).apply { groupingSeparator = '.' })
private val formatoFecha = DateTimeFormatter.ofPattern("d MMM yyyy", localeCo)

/** 1390000 → "$1.390.000" */
fun Long.comoPesos(): String = "$" + formatoPesos.format(this)

fun LocalDate.comoTexto(): String = format(formatoFecha)

fun YearMonth.abreviado(): String =
    month.getDisplayName(TextStyle.SHORT, localeCo).replace(".", "").replaceFirstChar { it.uppercase() }

fun View.mostrarMensaje(texto: String, accion: String? = null, alPulsar: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, texto, Snackbar.LENGTH_LONG)
    if (accion != null && alPulsar != null) snackbar.setAction(accion) { alPulsar() }
    snackbar.show()
}

/** Pinta una etiqueta tipo píldora con un color de texto y su versión suave de fondo. */
fun TextView.pintarPildora(@ColorRes texto: Int, @ColorRes fondo: Int) {
    setTextColor(ContextCompat.getColor(context, texto))
    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, fondo))
}

fun TextView.pintarEstado(estado: EstadoPedido) {
    text = estado.titulo
    when (estado) {
        EstadoPedido.PENDIENTE -> pintarPildora(R.color.advertencia, R.color.advertencia_suave)
        EstadoPedido.ENVIADO -> pintarPildora(R.color.acento_comprador, R.color.primario_suave)
        EstadoPedido.ENTREGADO -> pintarPildora(R.color.exito, R.color.exito_suave)
    }
}

fun TextView.pintarEstado(estado: EstadoCuenta) {
    text = estado.titulo
    when (estado) {
        EstadoCuenta.ACTIVA -> pintarPildora(R.color.exito, R.color.exito_suave)
        EstadoCuenta.PENDIENTE -> pintarPildora(R.color.advertencia, R.color.advertencia_suave)
        EstadoCuenta.RECHAZADA, EstadoCuenta.INACTIVA -> pintarPildora(R.color.error, R.color.error_suave)
    }
}

/** Lee el texto de un campo y muestra el error si está vacío. Devuelve null cuando falla. */
fun TextInputLayout.textoObligatorio(mensaje: String): String? {
    val valor = editText?.text?.toString()?.trim().orEmpty()
    error = if (valor.isEmpty()) mensaje else null
    return valor.ifEmpty { null }
}

fun TextInputLayout.texto(): String = editText?.text?.toString()?.trim().orEmpty()

fun esCorreoValido(correo: String) = android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()

/**
 * Preferencias locales mínimas para el ingreso con huella: el último correo que inició sesión
 * y si el usuario dejó activa la huella. Nunca se guardan contraseñas ni datos biométricos.
 */
object Sesion {
    private const val ARCHIVO = "nucleo_sesion"
    private const val ULTIMO_CORREO = "ultimo_correo"
    private const val HUELLA_ACTIVA = "huella_activa"

    private fun prefs(context: Context) = context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)

    fun guardarUltimoCorreo(context: Context, correo: String) =
        prefs(context).edit().putString(ULTIMO_CORREO, correo).apply()

    fun ultimoCorreo(context: Context): String? = prefs(context).getString(ULTIMO_CORREO, null)

    fun huellaActiva(context: Context): Boolean = prefs(context).getBoolean(HUELLA_ACTIVA, true)

    fun cambiarHuella(context: Context, activa: Boolean) =
        prefs(context).edit().putBoolean(HUELLA_ACTIVA, activa).apply()

    fun olvidar(context: Context) = prefs(context).edit().clear().apply()
}
