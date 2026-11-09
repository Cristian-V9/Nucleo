package com.nucleo.tienda.ui.comun

import android.app.Activity
import android.content.Intent
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.data.Rol
import com.nucleo.tienda.ui.admin.AdminActivity
import com.nucleo.tienda.ui.auth.AuthActivity
import com.nucleo.tienda.ui.comprador.CompradorActivity
import com.nucleo.tienda.ui.vendedor.VendedorActivity

/** Abre la pantalla principal del rol y cierra las anteriores (el botón atrás no vuelve al login). */
fun abrirInicioDeRol(activity: Activity, rol: Rol) {
    val destino = when (rol) {
        Rol.COMPRADOR -> CompradorActivity::class.java
        Rol.VENDEDOR -> VendedorActivity::class.java
        Rol.ADMIN -> AdminActivity::class.java
    }
    val intent = Intent(activity, destino)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    activity.startActivity(intent)
}

/** Cierra la sesión y vuelve al inicio de sesión. */
fun cerrarSesion(activity: Activity) {
    Repositorio.cerrarSesion()
    val intent = Intent(activity, AuthActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    activity.startActivity(intent)
    activity.finish()
}
