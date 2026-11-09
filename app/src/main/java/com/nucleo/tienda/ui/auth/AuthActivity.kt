package com.nucleo.tienda.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nucleo.tienda.databinding.ActivityAuthBinding

/** Contenedor del flujo de acceso. Los fragments se definen en res/navigation/nav_auth.xml. */
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
