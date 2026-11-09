package com.nucleo.tienda.data

import androidx.annotation.DrawableRes
import com.nucleo.tienda.R
import java.time.LocalDate

/** Roles de la aplicación. Cada uno abre su propia Activity con su menú de navegación. */
enum class Rol(val titulo: String) {
    COMPRADOR("Comprador"),
    VENDEDOR("Vendedor"),
    ADMIN("Administrador")
}

/** Estado de una cuenta. Los vendedores nuevos quedan PENDIENTE hasta que el administrador los aprueba. */
enum class EstadoCuenta(val titulo: String) {
    ACTIVA("Activa"),
    PENDIENTE("Pendiente"),
    RECHAZADA("Rechazada"),
    INACTIVA("Inactiva")
}

/** Datos legales del comercio de un vendedor (RF-03). */
data class Comercio(
    val razonSocial: String,
    val nit: String,
    val telefono: String,
    val direccion: String,
    val costoEnvio: Long,
    val tiempoEntrega: String
)

data class Usuario(
    val id: String,
    var nombre: String,
    val correo: String,
    var celular: String,
    var rol: Rol,
    var estado: EstadoCuenta,
    var clave: String = "12345678",
    val comercio: Comercio? = null,
    val fechaRegistro: LocalDate = LocalDate.of(2026, 9, 1)
) {
    /** Nombre que se muestra: la razón social para los vendedores, el nombre para los demás. */
    val nombreVisible: String get() = comercio?.razonSocial ?: nombre

    val iniciales: String
        get() = nombreVisible.split(" ").filter { it.isNotBlank() }.take(2)
            .joinToString("") { it.first().uppercase() }
}

enum class Categoria(val titulo: String, @DrawableRes val icono: Int) {
    PROCESADOR("Procesadores", R.drawable.ic_cpu),
    GRAFICA("Tarjetas gráficas", R.drawable.ic_gpu),
    RAM("Memoria RAM", R.drawable.ic_ram),
    ALMACENAMIENTO("Almacenamiento", R.drawable.ic_disco),
    PLACA("Tarjetas madre", R.drawable.ic_placa),
    FUENTE("Fuentes de poder", R.drawable.ic_fuente)
}

/** Publicación de un vendedor. Varios vendedores pueden publicar la misma referencia del fabricante. */
data class Producto(
    val id: String,
    var nombre: String,
    var categoria: Categoria,
    var marca: String,
    var referencia: String,
    var precio: Long,
    var stock: Int,
    val vendedorId: String,
    var ficha: List<Pair<String, String>>,
    var oculto: Boolean = false,
    var motivoOculto: String? = null,
    var eliminado: Boolean = false
)

/**
 * Un producto del catálogo agrupado por referencia: reúne todas las publicaciones
 * activas de esa referencia para comparar precios entre comercios.
 */
data class ProductoCatalogo(
    val referencia: String,
    val nombre: String,
    val categoria: Categoria,
    val precioDesde: Long,
    val numeroComercios: Int,
    val publicaciones: List<Producto>
)

data class ItemCarrito(val productoId: String, var cantidad: Int)

enum class EstadoPedido(val titulo: String) {
    PENDIENTE("Pendiente"),
    ENVIADO("Enviado"),
    ENTREGADO("Entregado")
}

data class ItemPedido(val nombre: String, val cantidad: Int, val precio: Long)

data class Pedido(
    val id: String,
    val compradorId: String,
    val vendedorId: String,
    val items: List<ItemPedido>,
    val envio: Long,
    val direccion: String,
    var estado: EstadoPedido,
    val fecha: LocalDate
) {
    val subtotal: Long get() = items.sumOf { it.precio * it.cantidad }
    val total: Long get() = subtotal + envio
    val cantidadProductos: Int get() = items.sumOf { it.cantidad }
}
