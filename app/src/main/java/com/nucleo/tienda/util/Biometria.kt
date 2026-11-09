package com.nucleo.tienda.util

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Envoltorio de BiometricPrompt. La huella la valida el sistema operativo:
 * la app solo recibe si la autenticación fue exitosa o no (RF-01, RNF-05).
 */
object Biometria {

    fun disponible(fragment: Fragment): Boolean =
        BiometricManager.from(fragment.requireContext()).canAuthenticate(BIOMETRIC_STRONG) ==
            BiometricManager.BIOMETRIC_SUCCESS

    fun autenticar(
        fragment: Fragment,
        titulo: String,
        subtitulo: String,
        textoCancelar: String,
        alAutenticar: () -> Unit,
        alFallar: (String) -> Unit = {}
    ) {
        val ejecutor = ContextCompat.getMainExecutor(fragment.requireContext())
        val prompt = BiometricPrompt(fragment, ejecutor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) = alAutenticar()

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON && errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                    alFallar(errString.toString())
                }
            }
        })
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(titulo)
            .setSubtitle(subtitulo)
            .setNegativeButtonText(textoCancelar)
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()
        prompt.authenticate(info)
    }
}
