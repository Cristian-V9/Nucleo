package com.nucleo.tienda.data

import java.time.LocalDate
import java.time.YearMonth

/**
 * Fuente de datos de esta versión: datos de ejemplo en memoria.
 * Las pantallas solo hablan con este repositorio, así que en la siguiente versión
 * se puede reemplazar por Firebase sin cambiar las interfaces.
 */
object Repositorio {

    var usuarioActual: Usuario? = null
        private set

    private val usuarios = mutableListOf(
        Usuario("u1", "Andrea Rojas", "comprador@nucleo.co", "3001234567", Rol.COMPRADOR, EstadoCuenta.ACTIVA,
            fechaRegistro = LocalDate.of(2026, 6, 12)),
        Usuario("u2", "Camilo Pérez", "camilo.perez@correo.co", "3109876543", Rol.COMPRADOR, EstadoCuenta.ACTIVA,
            fechaRegistro = LocalDate.of(2026, 7, 3)),
        Usuario("u3", "Laura Martínez", "laura.martinez@correo.co", "3157654321", Rol.COMPRADOR, EstadoCuenta.INACTIVA,
            fechaRegistro = LocalDate.of(2026, 7, 21)),
        Usuario("u4", "Julián Torres", "julian.torres@correo.co", "3204567890", Rol.COMPRADOR, EstadoCuenta.ACTIVA,
            fechaRegistro = LocalDate.of(2026, 8, 30)),
        Usuario("v1", "Jorge Medina", "vendedor@nucleo.co", "6017441122", Rol.VENDEDOR, EstadoCuenta.ACTIVA,
            comercio = Comercio("Hardware Central S.A.S.", "900.123.456-1", "6017441122",
                "Cra. 15 # 77-05, Local 210, Bogotá", 15_000, "1 a 2 días"),
            fechaRegistro = LocalDate.of(2026, 5, 4)),
        Usuario("v2", "Paula Ríos", "megachip@nucleo.co", "6017338899", Rol.VENDEDOR, EstadoCuenta.ACTIVA,
            comercio = Comercio("MegaChip S.A.S.", "901.234.567-2", "6017338899",
                "Cl. 76 # 15-20, Local 118, Bogotá", 12_000, "1 día"),
            fechaRegistro = LocalDate.of(2026, 5, 18)),
        Usuario("v3", "Andrés Gil", "pcandina@nucleo.co", "6015554433", Rol.VENDEDOR, EstadoCuenta.ACTIVA,
            comercio = Comercio("PC Andina S.A.S.", "901.345.678-3", "6015554433",
                "Av. Cra. 15 # 78-33, Local 305, Bogotá", 18_000, "2 a 3 días"),
            fechaRegistro = LocalDate.of(2026, 6, 2)),
        Usuario("v4", "Diana León", "tecnored@correo.co", "6013216549", Rol.VENDEDOR, EstadoCuenta.PENDIENTE,
            comercio = Comercio("TecnoRed Bogotá S.A.S.", "901.456.789-4", "6013216549",
                "Cl. 79 # 14-10, Local 12, Bogotá", 14_000, "1 a 2 días"),
            fechaRegistro = LocalDate.of(2026, 11, 7)),
        Usuario("v5", "Mauricio Ruiz", "bytestore@correo.co", "6019876543", Rol.VENDEDOR, EstadoCuenta.PENDIENTE,
            comercio = Comercio("Byte Store S.A.S.", "901.567.890-5", "6019876543",
                "Cra. 13 # 63-40, Local 2, Bogotá", 10_000, "2 días"),
            fechaRegistro = LocalDate.of(2026, 11, 8)),
        Usuario("a1", "Sofía Gómez", "admin@nucleo.co", "3012223344", Rol.ADMIN, EstadoCuenta.ACTIVA,
            fechaRegistro = LocalDate.of(2026, 5, 1))
    )

    private val productos = mutableListOf<Producto>()

    init {
        val gpu4060 = listOf("Memoria" to "8 GB GDDR6", "Interfaz" to "PCIe 4.0", "Consumo" to "115 W", "Salidas" to "HDMI · DisplayPort")
        val rx7600 = listOf("Memoria" to "8 GB GDDR6", "Interfaz" to "PCIe 4.0", "Consumo" to "165 W", "Salidas" to "HDMI · DisplayPort")
        val r7600x = listOf("Núcleos" to "6 / 12 hilos", "Socket" to "AM5", "Frecuencia" to "Hasta 5,3 GHz", "Consumo" to "105 W")
        val i5 = listOf("Núcleos" to "10 / 16 hilos", "Socket" to "LGA 1700", "Frecuencia" to "Hasta 4,7 GHz", "Consumo" to "65 W")
        val fury = listOf("Capacidad" to "16 GB", "Tipo" to "DDR5", "Frecuencia" to "5600 MHz", "Latencia" to "CL40")
        val corsair = listOf("Capacidad" to "16 GB (2 × 8)", "Tipo" to "DDR4", "Frecuencia" to "3200 MHz", "Latencia" to "CL16")
        val samsung = listOf("Capacidad" to "1 TB", "Interfaz" to "NVMe PCIe 3.0", "Lectura" to "3500 MB/s", "Formato" to "M.2 2280")
        val nv2 = listOf("Capacidad" to "500 GB", "Interfaz" to "NVMe PCIe 4.0", "Lectura" to "3500 MB/s", "Formato" to "M.2 2280")
        val b650 = listOf("Socket" to "AM5", "Chipset" to "B650", "Formato" to "Micro-ATX", "Memoria" to "DDR5")
        val b760 = listOf("Socket" to "LGA 1700", "Chipset" to "B760", "Formato" to "Micro-ATX", "Memoria" to "DDR4")
        val rm750 = listOf("Potencia" to "750 W", "Certificación" to "80 Plus Gold", "Modular" to "Sí", "Formato" to "ATX")

        fun p(id: String, nombre: String, cat: Categoria, marca: String, ref: String, precio: Long, stock: Int,
              vendedor: String, ficha: List<Pair<String, String>>) =
            productos.add(Producto(id, nombre, cat, marca, ref, precio, stock, vendedor, ficha))

        p("p1", "NVIDIA GeForce RTX 4060 8 GB", Categoria.GRAFICA, "NVIDIA", "RTX4060-8G", 1_390_000, 6, "v1", gpu4060)
        p("p2", "NVIDIA GeForce RTX 4060 8 GB", Categoria.GRAFICA, "NVIDIA", "RTX4060-8G", 1_420_000, 2, "v2", gpu4060)
        p("p3", "NVIDIA GeForce RTX 4060 8 GB", Categoria.GRAFICA, "NVIDIA", "RTX4060-8G", 1_455_000, 10, "v3", gpu4060)
        p("p4", "AMD Radeon RX 7600 8 GB", Categoria.GRAFICA, "AMD", "RX7600-8G", 1_250_000, 3, "v1", rx7600)
        p("p5", "AMD Radeon RX 7600 8 GB", Categoria.GRAFICA, "AMD", "RX7600-8G", 1_229_000, 5, "v3", rx7600)
        p("p6", "AMD Ryzen 5 7600X", Categoria.PROCESADOR, "AMD", "100-100000593WOF", 905_000, 4, "v1", r7600x)
        p("p7", "AMD Ryzen 5 7600X", Categoria.PROCESADOR, "AMD", "100-100000593WOF", 899_000, 7, "v2", r7600x)
        p("p8", "AMD Ryzen 5 7600X", Categoria.PROCESADOR, "AMD", "100-100000593WOF", 925_000, 3, "v3", r7600x)
        p("p9", "Intel Core i5-14400F", Categoria.PROCESADOR, "Intel", "BX8071514400F", 795_000, 0, "v1", i5)
        p("p10", "Intel Core i5-14400F", Categoria.PROCESADOR, "Intel", "BX8071514400F", 789_000, 5, "v2", i5)
        p("p11", "Kingston FURY Beast DDR5 16 GB", Categoria.RAM, "Kingston", "KF556C40BB-16", 289_000, 12, "v1", fury)
        p("p12", "Kingston FURY Beast DDR5 16 GB", Categoria.RAM, "Kingston", "KF556C40BB-16", 295_000, 8, "v2", fury)
        p("p13", "Corsair Vengeance LPX DDR4 16 GB", Categoria.RAM, "Corsair", "CMK16GX4M2E3200C16", 219_000, 9, "v2", corsair)
        p("p14", "Samsung 980 NVMe 1 TB", Categoria.ALMACENAMIENTO, "Samsung", "MZ-V8V1T0BW", 349_000, 6, "v1", samsung)
        p("p15", "Samsung 980 NVMe 1 TB", Categoria.ALMACENAMIENTO, "Samsung", "MZ-V8V1T0BW", 339_000, 4, "v3", samsung)
        p("p16", "Kingston NV2 500 GB", Categoria.ALMACENAMIENTO, "Kingston", "SNV2S-500G", 169_000, 15, "v2", nv2)
        p("p17", "ASUS Prime B650M-A", Categoria.PLACA, "ASUS", "PRIME-B650M-A", 799_000, 2, "v3", b650)
        p("p18", "MSI PRO B760M-E DDR4", Categoria.PLACA, "MSI", "PRO-B760M-E-DDR4", 489_000, 3, "v1", b760)
        p("p19", "Corsair RM750e", Categoria.FUENTE, "Corsair", "CP-9020262", 489_000, 4, "v2", rm750)
        productos.first { it.id == "p13" }.apply {
            oculto = true
            motivoOculto = "Ficha técnica incompleta"
        }
    }

    private val pedidos = mutableListOf(
        pedido("NC-1001", "u2", "v1", listOf(ItemPedido("AMD Ryzen 5 7600X", 1, 905_000)), "2026-06-08", EstadoPedido.ENTREGADO),
        pedido("NC-1004", "u1", "v2", listOf(ItemPedido("Kingston FURY Beast DDR5 16 GB", 2, 295_000)), "2026-06-21", EstadoPedido.ENTREGADO),
        pedido("NC-1007", "u4", "v3", listOf(ItemPedido("NVIDIA GeForce RTX 4060 8 GB", 1, 1_455_000)), "2026-07-02", EstadoPedido.ENTREGADO),
        pedido("NC-1010", "u2", "v1", listOf(ItemPedido("Samsung 980 NVMe 1 TB", 1, 349_000), ItemPedido("Kingston FURY Beast DDR5 16 GB", 1, 289_000)), "2026-07-19", EstadoPedido.ENTREGADO),
        pedido("NC-1014", "u1", "v2", listOf(ItemPedido("Intel Core i5-14400F", 1, 789_000)), "2026-08-05", EstadoPedido.ENTREGADO),
        pedido("NC-1018", "u4", "v1", listOf(ItemPedido("NVIDIA GeForce RTX 4060 8 GB", 1, 1_390_000), ItemPedido("MSI PRO B760M-E DDR4", 1, 489_000)), "2026-08-24", EstadoPedido.ENTREGADO),
        pedido("NC-1021", "u3", "v3", listOf(ItemPedido("ASUS Prime B650M-A", 1, 799_000)), "2026-09-09", EstadoPedido.ENTREGADO),
        pedido("NC-1025", "u2", "v2", listOf(ItemPedido("Corsair RM750e", 1, 489_000), ItemPedido("Kingston NV2 500 GB", 2, 169_000)), "2026-09-27", EstadoPedido.ENTREGADO),
        pedido("NC-1029", "u1", "v1", listOf(ItemPedido("AMD Radeon RX 7600 8 GB", 1, 1_250_000)), "2026-10-04", EstadoPedido.ENTREGADO),
        pedido("NC-1031", "u1", "v2", listOf(ItemPedido("Kingston FURY Beast DDR5 16 GB", 1, 295_000)), "2026-10-18", EstadoPedido.ENTREGADO),
        pedido("NC-1035", "u4", "v3", listOf(ItemPedido("Samsung 980 NVMe 1 TB", 2, 339_000)), "2026-10-29", EstadoPedido.ENTREGADO),
        pedido("NC-1039", "u2", "v1", listOf(ItemPedido("AMD Ryzen 5 7600X", 1, 905_000)), "2026-11-03", EstadoPedido.ENVIADO),
        pedido("NC-1041", "u4", "v1", listOf(ItemPedido("Samsung 980 NVMe 1 TB", 1, 349_000)), "2026-11-06", EstadoPedido.PENDIENTE),
        pedido("NC-1042", "u1", "v1", listOf(ItemPedido("NVIDIA GeForce RTX 4060 8 GB", 1, 1_390_000), ItemPedido("Kingston FURY Beast DDR5 16 GB", 1, 289_000)), "2026-11-08", EstadoPedido.PENDIENTE)
    )

    private val carrito = mutableListOf<ItemCarrito>()
    private var consecutivoPedido = 1043
    private var consecutivoId = 100

    private fun pedido(id: String, comprador: String, vendedor: String, items: List<ItemPedido>, fecha: String, estado: EstadoPedido): Pedido {
        val envio = usuarios.first { it.id == vendedor }.comercio?.costoEnvio ?: 0
        return Pedido(id, comprador, vendedor, items, envio, "Cra. 15 # 85-20, Apto 402, Bogotá", estado, LocalDate.parse(fecha))
    }

    private fun nuevoId(prefijo: String) = "$prefijo${consecutivoId++}"

    // ---------------------------------------------------------------- Sesión

    /** Devuelve el usuario si el correo y la clave coinciden (sin distinguir mayúsculas en el correo). */
    fun validarCredenciales(correo: String, clave: String): Usuario? =
        usuarios.firstOrNull { it.correo.equals(correo.trim(), ignoreCase = true) && it.clave == clave }

    fun buscarPorCorreo(correo: String): Usuario? =
        usuarios.firstOrNull { it.correo.equals(correo.trim(), ignoreCase = true) }

    fun iniciarSesion(usuario: Usuario) {
        usuarioActual = usuario
    }

    fun cerrarSesion() {
        usuarioActual = null
        carrito.clear()
    }

    fun existeCorreo(correo: String) = buscarPorCorreo(correo) != null

    fun registrar(nombre: String, correo: String, celular: String, clave: String, rol: Rol, comercio: Comercio? = null): Usuario {
        val estado = if (rol == Rol.VENDEDOR) EstadoCuenta.PENDIENTE else EstadoCuenta.ACTIVA
        val usuario = Usuario(nuevoId("u"), nombre, correo.trim(), celular, rol, estado, clave, comercio, LocalDate.now())
        usuarios.add(usuario)
        return usuario
    }

    // ---------------------------------------------------------------- Usuarios (administrador)

    fun usuarios(): List<Usuario> = usuarios.sortedWith(compareBy<Usuario> { it.rol.ordinal }.thenBy { it.nombreVisible })

    fun usuario(id: String): Usuario? = usuarios.firstOrNull { it.id == id }

    fun crearUsuario(nombre: String, correo: String, celular: String, rol: Rol, activo: Boolean): Usuario {
        val usuario = Usuario(nuevoId("u"), nombre, correo.trim(), celular, rol,
            if (activo) EstadoCuenta.ACTIVA else EstadoCuenta.INACTIVA, fechaRegistro = LocalDate.now())
        usuarios.add(usuario)
        return usuario
    }

    fun actualizarUsuario(id: String, nombre: String, celular: String, rol: Rol, activo: Boolean) {
        usuario(id)?.apply {
            this.nombre = nombre
            this.celular = celular
            this.rol = rol
            if (estado == EstadoCuenta.ACTIVA || estado == EstadoCuenta.INACTIVA) {
                estado = if (activo) EstadoCuenta.ACTIVA else EstadoCuenta.INACTIVA
            }
        }
    }

    /** Eliminación lógica: la cuenta queda inactiva y conserva su historial (RF-04). */
    fun eliminarUsuario(id: String) {
        usuario(id)?.estado = EstadoCuenta.INACTIVA
    }

    fun solicitudesPendientes(): List<Usuario> =
        usuarios.filter { it.rol == Rol.VENDEDOR && it.estado == EstadoCuenta.PENDIENTE }

    fun resolverSolicitud(id: String, aprobada: Boolean) {
        usuario(id)?.estado = if (aprobada) EstadoCuenta.ACTIVA else EstadoCuenta.RECHAZADA
    }

    fun vendedoresActivos(): List<Usuario> =
        usuarios.filter { it.rol == Rol.VENDEDOR && it.estado == EstadoCuenta.ACTIVA }

    fun actualizarPerfil(nombre: String, celular: String) {
        usuarioActual?.apply {
            this.nombre = nombre
            this.celular = celular
        }
    }

    fun eliminarCuentaActual() {
        usuarioActual?.let { eliminarUsuario(it.id) }
        cerrarSesion()
    }

    // ---------------------------------------------------------------- Catálogo (comprador)

    private fun esVisible(p: Producto): Boolean {
        val vendedor = usuario(p.vendedorId)
        return !p.oculto && !p.eliminado && p.stock > 0 && vendedor?.estado == EstadoCuenta.ACTIVA
    }

    /** Catálogo agrupado por referencia del fabricante, con el menor precio disponible. */
    fun catalogo(categoria: Categoria? = null, texto: String = "", ascendente: Boolean = true): List<ProductoCatalogo> {
        val consulta = texto.trim().lowercase()
        val grupos = productos.filter(::esVisible)
            .filter { categoria == null || it.categoria == categoria }
            .filter { consulta.isEmpty() || it.nombre.lowercase().contains(consulta) || it.marca.lowercase().contains(consulta) || it.referencia.lowercase().contains(consulta) }
            .groupBy { it.referencia }
            .map { (ref, lista) ->
                val ordenados = lista.sortedBy { it.precio }
                ProductoCatalogo(ref, ordenados.first().nombre, ordenados.first().categoria,
                    ordenados.first().precio, ordenados.size, ordenados)
            }
        return if (ascendente) grupos.sortedBy { it.precioDesde } else grupos.sortedByDescending { it.precioDesde }
    }

    fun destacados(): List<ProductoCatalogo> = catalogo().sortedByDescending { it.numeroComercios }.take(6)

    fun productoCatalogo(referencia: String): ProductoCatalogo? = catalogo().firstOrNull { it.referencia == referencia }

    fun producto(id: String): Producto? = productos.firstOrNull { it.id == id }

    // ---------------------------------------------------------------- Productos (vendedor y administrador)

    fun productosDe(vendedorId: String): List<Producto> =
        productos.filter { it.vendedorId == vendedorId && !it.eliminado }

    fun todosLosProductos(): List<Producto> = productos.filter { !it.eliminado }

    fun guardarProducto(id: String?, nombre: String, categoria: Categoria, marca: String, referencia: String,
                        precio: Long, stock: Int, ficha: List<Pair<String, String>>) {
        val existente = id?.let { producto(it) }
        if (existente != null) {
            existente.nombre = nombre
            existente.categoria = categoria
            existente.marca = marca
            existente.referencia = referencia
            existente.precio = precio
            existente.stock = stock
            existente.ficha = ficha
        } else {
            val vendedor = usuarioActual?.id ?: return
            productos.add(Producto(nuevoId("p"), nombre, categoria, marca, referencia, precio, stock, vendedor, ficha))
        }
    }

    /** Eliminación lógica para conservar el historial de pedidos (RF-06). */
    fun eliminarProducto(id: String) {
        producto(id)?.eliminado = true
        carrito.removeAll { it.productoId == id }
    }

    fun ocultarProducto(id: String, motivo: String) {
        producto(id)?.apply {
            oculto = true
            motivoOculto = motivo
        }
        carrito.removeAll { it.productoId == id }
    }

    fun mostrarProducto(id: String) {
        producto(id)?.apply {
            oculto = false
            motivoOculto = null
        }
    }

    // ---------------------------------------------------------------- Carrito

    fun carrito(): List<ItemCarrito> = carrito.toList()

    fun cantidadEnCarrito(): Int = carrito.sumOf { it.cantidad }

    fun vendedorDelCarrito(): Usuario? = carrito.firstOrNull()?.let { producto(it.productoId) }?.let { usuario(it.vendedorId) }

    /** El carrito solo admite productos de un comercio: indica si agregar este producto exige vaciarlo. */
    fun requiereVaciarCarrito(productoId: String): Boolean {
        val vendedor = producto(productoId)?.vendedorId ?: return false
        return carrito.isNotEmpty() && vendedorDelCarrito()?.id != vendedor
    }

    fun agregarAlCarrito(productoId: String) {
        if (requiereVaciarCarrito(productoId)) carrito.clear()
        val stock = producto(productoId)?.stock ?: 0
        val item = carrito.firstOrNull { it.productoId == productoId }
        if (item == null) carrito.add(ItemCarrito(productoId, 1))
        else if (item.cantidad < stock) item.cantidad++
    }

    fun cambiarCantidad(productoId: String, delta: Int) {
        val item = carrito.firstOrNull { it.productoId == productoId } ?: return
        val stock = producto(productoId)?.stock ?: 0
        item.cantidad = (item.cantidad + delta).coerceIn(1, maxOf(stock, 1))
    }

    fun quitarDelCarrito(productoId: String) {
        carrito.removeAll { it.productoId == productoId }
    }

    fun subtotalCarrito(): Long = carrito.sumOf { (producto(it.productoId)?.precio ?: 0) * it.cantidad }

    fun envioCarrito(): Long = if (carrito.isEmpty()) 0 else vendedorDelCarrito()?.comercio?.costoEnvio ?: 0

    // ---------------------------------------------------------------- Pedidos

    /** Simula el pago aprobado por la pasarela: crea el pedido, descuenta stock y vacía el carrito. */
    fun confirmarPedido(direccion: String): Pedido? {
        val comprador = usuarioActual ?: return null
        val vendedor = vendedorDelCarrito() ?: return null
        val items = carrito.mapNotNull { item ->
            producto(item.productoId)?.let { p ->
                p.stock = (p.stock - item.cantidad).coerceAtLeast(0)
                ItemPedido(p.nombre, item.cantidad, p.precio)
            }
        }
        val pedido = Pedido("NC-${consecutivoPedido++}", comprador.id, vendedor.id, items,
            vendedor.comercio?.costoEnvio ?: 0, direccion, EstadoPedido.PENDIENTE, LocalDate.now())
        pedidos.add(pedido)
        carrito.clear()
        return pedido
    }

    fun pedidosDeComprador(id: String): List<Pedido> = pedidos.filter { it.compradorId == id }.sortedByDescending { it.fecha }

    fun pedidosDeVendedor(id: String, estado: EstadoPedido? = null): List<Pedido> =
        pedidos.filter { it.vendedorId == id && (estado == null || it.estado == estado) }.sortedByDescending { it.fecha }

    /** Avanza el pedido al siguiente estado: Pendiente → Enviado → Entregado (RF-09). */
    fun avanzarPedido(id: String) {
        pedidos.firstOrNull { it.id == id }?.apply {
            estado = when (estado) {
                EstadoPedido.PENDIENTE -> EstadoPedido.ENVIADO
                else -> EstadoPedido.ENTREGADO
            }
        }
    }

    fun todosLosPedidos(): List<Pedido> = pedidos.sortedByDescending { it.fecha }

    // ---------------------------------------------------------------- Reporte de ventas (administrador)

    data class VentaMensual(val mes: YearMonth, val total: Long, val pedidos: Int)
    data class VentaComercio(val comercio: String, val total: Long, val pedidos: Int)
    data class ProductoVendido(val nombre: String, val unidades: Int, val total: Long)

    fun ventasPorMes(meses: Int = 6): List<VentaMensual> {
        val ultimo = YearMonth.from(pedidos.maxOf { it.fecha })
        return (meses - 1 downTo 0).map { atras ->
            val mes = ultimo.minusMonths(atras.toLong())
            val delMes = pedidos.filter { YearMonth.from(it.fecha) == mes }
            VentaMensual(mes, delMes.sumOf { it.total }, delMes.size)
        }
    }

    fun ventasPorComercio(): List<VentaComercio> = pedidos.groupBy { it.vendedorId }
        .map { (id, lista) -> VentaComercio(usuario(id)?.nombreVisible ?: id, lista.sumOf { it.total }, lista.size) }
        .sortedByDescending { it.total }

    fun productosMasVendidos(limite: Int = 3): List<ProductoVendido> = pedidos.flatMap { it.items }
        .groupBy { it.nombre }
        .map { (nombre, items) -> ProductoVendido(nombre, items.sumOf { it.cantidad }, items.sumOf { it.precio * it.cantidad }) }
        .sortedByDescending { it.unidades }
        .take(limite)
}
