package com.nucleo.tienda.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.databinding.ActivityAdminBinding
import com.nucleo.tienda.ui.comun.cerrarSesion

/** Pantalla principal del administrador: contenedor de fragments + barra de navegación inferior. */
class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Repositorio.usuarioActual == null) {
            cerrarSesion(this)
            return
        }
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val host = supportFragmentManager.findFragmentById(R.id.contenedor) as NavHostFragment
        val navController = host.navController
        binding.navegacion.setupWithNavController(navController)

        val principales = listOf(R.id.dashboardFragment, R.id.usuariosFragment, R.id.productosAdminFragment, R.id.ventasFragment)
        navController.addOnDestinationChangedListener { _, destino, _ ->
            val visible = destino.id in principales
            binding.navegacion.isVisible = visible
            binding.separadorNavegacion.isVisible = visible
        }
    }

    /** Cambia de sección como si el usuario tocara la barra inferior. */
    fun irA(destino: Int) {
        binding.navegacion.selectedItemId = destino
    }
}
