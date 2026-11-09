package com.nucleo.tienda.ui.vendedor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.databinding.ActivityVendedorBinding
import com.nucleo.tienda.ui.comun.cerrarSesion

/** Pantalla principal del vendedor: contenedor de fragments + barra de navegación inferior. */
class VendedorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVendedorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Repositorio.usuarioActual == null) {
            cerrarSesion(this)
            return
        }
        binding = ActivityVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val host = supportFragmentManager.findFragmentById(R.id.contenedor) as NavHostFragment
        val navController = host.navController
        binding.navegacion.setupWithNavController(navController)

        val principales = listOf(R.id.productosVendedorFragment, R.id.pedidosVendedorFragment, R.id.perfilFragment)
        navController.addOnDestinationChangedListener { _, destino, _ ->
            val visible = destino.id in principales
            binding.navegacion.isVisible = visible
            binding.separadorNavegacion.isVisible = visible
        }
    }
}
