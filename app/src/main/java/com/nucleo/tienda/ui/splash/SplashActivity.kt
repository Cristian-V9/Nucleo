package com.nucleo.tienda.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nucleo.tienda.databinding.ActivitySplashBinding
import com.nucleo.tienda.ui.auth.AuthActivity

/**
 * Pantalla de bienvenida. Primero se muestra la splash del sistema (tema Theme.Nucleo.Splash)
 * y luego este layout con el logotipo, antes de pasar al inicio de sesión.
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logo.alpha = 0f
        binding.logo.animate().alpha(1f).setDuration(500).start()
        binding.root.postDelayed({ abrirAcceso() }, DURACION_MS)
    }

    private fun abrirAcceso() {
        if (isFinishing) return
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    private companion object {
        const val DURACION_MS = 1400L
    }
}
