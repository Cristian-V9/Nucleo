package com.nucleo.tienda.ui.comprador

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.nucleo.tienda.R
import com.nucleo.tienda.data.Categoria
import com.nucleo.tienda.data.Repositorio
import com.nucleo.tienda.databinding.ActivityCompradorBinding
import com.nucleo.tienda.ui.comun.cerrarSesion

/** Pantalla principal del comprador: contenedor de fragments + barra de navegación inferior. */
class CompradorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompradorBinding

    // Filtro que el inicio deja listo para el catálogo (categoría elegida o enfocar la búsqueda).
    var categoriaPendiente: Categoria? = null
    var enfocarBusqueda = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Repositorio.usuarioActual == null) {
            cerrarSesion(this)
            return
        }
        binding = ActivityCompradorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Conecta la barra inferior con el grafo de navegación (nav_comprador.xml).
        val host = supportFragmentManager.findFragmentById(R.id.contenedor) as NavHostFragment
        val navController = host.navController
        binding.navegacion.setupWithNavController(navController)

        // La barra solo se muestra en las 4 pantallas principales.
        val principales = listOf(R.id.inicioFragment, R.id.catalogoFragment, R.id.carritoFragment, R.id.perfilFragment)
        navController.addOnDestinationChangedListener { _, destino, _ ->
            val visible = destino.id in principales
            binding.navegacion.isVisible = visible
            binding.separadorNavegacion.isVisible = visible
        }
        actualizarInsigniaCarrito()
    }

    /** Cambia de sección como si el usuario tocara la barra inferior. */
    fun irA(destino: Int) {
        binding.navegacion.selectedItemId = destino
    }

    /** Muestra en la barra inferior cuántos productos hay en el carrito. */
    fun actualizarInsigniaCarrito() {
        val cantidad = Repositorio.cantidadEnCarrito()
        val insignia = binding.navegacion.getOrCreateBadge(R.id.carritoFragment)
        insignia.backgroundColor = ContextCompat.getColor(this, R.color.primario)
        insignia.badgeTextColor = ContextCompat.getColor(this, R.color.blanco)
        insignia.number = cantidad
        insignia.isVisible = cantidad > 0
    }
}
