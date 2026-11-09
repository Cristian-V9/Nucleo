package com.nucleo.tienda.ui.comun

import android.content.res.ColorStateList
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Rol
import com.nucleo.tienda.databinding.IncludeAvisoBinding
import com.nucleo.tienda.databinding.IncludeDocumentoBinding
import com.nucleo.tienda.databinding.IncludeEncabezadoBinding
import com.nucleo.tienda.databinding.IncludeEncabezadoRolBinding

/** Configura el encabezado incluido (include_encabezado.xml): título y acción del botón volver. */
fun IncludeEncabezadoBinding.configurar(titulo: String, alVolver: () -> Unit) {
    this.titulo.text = titulo
    botonVolver.setOnClickListener { alVolver() }
}

enum class TipoAviso(@ColorRes val fondo: Int, @ColorRes val icono: Int, val dibujo: Int) {
    INFO(R.color.primario_suave, R.color.primario_claro, R.drawable.ic_info),
    ADVERTENCIA(R.color.advertencia_suave, R.color.advertencia, R.drawable.ic_reloj),
    SEGURIDAD(R.color.exito_suave, R.color.exito, R.drawable.ic_candado)
}

/** Muestra un aviso (include_aviso.xml) con el texto y los colores del tipo indicado. */
fun IncludeAvisoBinding.mostrar(texto: String, tipo: TipoAviso = TipoAviso.INFO) {
    val contexto = root.context
    textoAviso.text = texto
    iconoAviso.setImageResource(tipo.dibujo)
    iconoAviso.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(contexto, tipo.icono))
    root.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(contexto, tipo.fondo))
}

/** Marca un documento (include_documento.xml) como cargado o pendiente. */
fun IncludeDocumentoBinding.marcarCargado(nombreArchivo: String?) {
    val contexto = root.context
    val cargado = nombreArchivo != null
    estado.text = if (cargado) contexto.getString(R.string.documento_cargado, nombreArchivo) else contexto.getString(R.string.documento_sin_cargar)
    icono.setImageResource(if (cargado) R.drawable.ic_check else R.drawable.ic_documento)
    val color = if (cargado) R.color.exito else R.color.texto_secundario
    val fondo = if (cargado) R.color.exito_suave else R.color.superficie_alta
    icono.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(contexto, color))
    fondoIcono.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(contexto, fondo))
}

/** Configura el encabezado de una pantalla principal (include_encabezado_rol.xml). */
fun IncludeEncabezadoRolBinding.configurar(
    rol: Rol,
    titulo: String,
    subtitulo: String? = null,
    iconoAccion: Int? = null,
    descripcionAccion: String? = null,
    primaria: Boolean = false,
    alPulsar: (() -> Unit)? = null
) {
    pildoraRol.text = rol.titulo
    this.titulo.text = titulo
    this.subtitulo.text = subtitulo
    this.subtitulo.visibility = if (subtitulo == null) View.GONE else View.VISIBLE
    if (iconoAccion != null && alPulsar != null) {
        botonAccion.visibility = View.VISIBLE
        botonAccion.setIconResource(iconoAccion)
        botonAccion.contentDescription = descripcionAccion
        if (primaria) {
            val contexto = root.context
            botonAccion.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(contexto, R.color.primario))
            botonAccion.iconTint = ColorStateList.valueOf(ContextCompat.getColor(contexto, R.color.blanco))
            botonAccion.strokeWidth = 0
        }
        botonAccion.setOnClickListener { alPulsar() }
    }
}
