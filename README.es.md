[English](README.md) | [Español](README.es.md)

# Núcleo · Tienda virtual Android

Aplicación móvil nativa para Android que funciona como marketplace de componentes para computadores de escritorio (procesadores, tarjetas gráficas, memoria RAM, almacenamiento, tarjetas madre y fuentes de poder), vendidos por comercios legalmente constituidos de Bogotá.

Esta versión corresponde al frontend completo de la aplicación: todas las pantallas, la navegación por rol y los recursos visuales. Los datos son de ejemplo y se guardan en memoria; la conexión con Firebase y Mercado Pago queda para la siguiente versión.

## Descargar y probar

- **APK:** [`apk/Nucleo-v1.0.apk`](apk/Nucleo-v1.0.apk) (Android 8.0 o superior).
- **Cuentas de prueba** (contraseña `12345678`):

| Rol | Correo |
|---|---|
| Comprador | `comprador@nucleo.co` |
| Vendedor | `vendedor@nucleo.co` |
| Administrador | `admin@nucleo.co` |

El ingreso con huella se habilita después del primer ingreso con contraseña, en un dispositivo que tenga una huella registrada.

## Pantallas por rol

| Rol | Pantallas |
|---|---|
| Transversal | Splash, inicio de sesión (correo y huella), registro, registro de comercio, recuperar contraseña, barra de navegación inferior por rol |
| Comprador | Inicio, catálogo, detalle de producto (comparación entre comercios), carrito, pago, perfil con pedidos |
| Vendedor | Mis productos (CRUD), formulario de producto con cámara y galería, pedidos recibidos, perfil del vendedor |
| Administrador | Dashboard, usuarios (listado, creación, edición, eliminación), productos (ocultar/mostrar), reporte de ventas, solicitudes de comercios |

## Tecnología

- Kotlin, layouts XML y **ViewBinding**
- **Navigation Component**: una Activity por rol (`activity_comprador.xml`, `activity_vendedor.xml`, `activity_admin.xml`) con fragments y `BottomNavigationView`
- **Material Design 3** con tema oscuro propio
- **BiometricPrompt** para el ingreso y la confirmación del pago con huella
- **SplashScreen API** para la pantalla de bienvenida
- `minSdk 26`, `targetSdk 34`

## Estructura

```
app/src/main/
├── java/com/nucleo/tienda/
│   ├── data/            Modelos y repositorio con datos de ejemplo
│   ├── util/            Formato de precios, sesión y biometría
│   └── ui/
│       ├── splash/      Pantalla de bienvenida
│       ├── auth/        Login, registro, registro de comercio, recuperar contraseña
│       ├── comprador/   Inicio, catálogo, detalle, carrito, pago
│       ├── vendedor/    Productos, formulario de producto, pedidos
│       ├── admin/       Dashboard, usuarios, productos, ventas, solicitudes
│       └── comun/       Perfil, adaptadores, diálogos y componentes compartidos
└── res/
    ├── layout/          Activities, fragments, ítems de lista e includes reutilizables
    ├── values/          colors, themes, styles, dimens, strings, attrs
    ├── color/           Selectores de color por estado (chips, barra inferior, campos)
    ├── drawable/        Formas (shape, layer-list) e íconos vectoriales
    ├── font/            IBM Plex Sans y Space Grotesk
    ├── menu/            Menús de la barra inferior por rol
    └── navigation/      Grafos de navegación por rol
```

## Recursos visuales

- **Colores** (`values/colors.xml`): fondo oscuro `#0B1220`, acción principal `#2563EB` y un color de acento por rol (comprador azul, vendedor cian, administrador violeta).
- **Temas** (`values/themes.xml`): `Theme.Nucleo` hereda de Material 3 oscuro y redefine colores, tipografía y estilos de componentes. `Theme.Nucleo.Comprador`, `.Vendedor` y `.Admin` solo cambian el atributo propio `colorRol` (declarado en `values/attrs.xml`).
- **Estilos** (`values/styles.xml`): botones, campos de texto, tarjetas, chips, barra de navegación, píldoras de estado y apariencias de texto.
- **Drawables**: formas redondeadas, borde punteado, barras de la gráfica y 46 íconos vectoriales propios.

## Compilar

1. Abrir la carpeta del proyecto en Android Studio (**File → Open**) y esperar el Gradle Sync.
2. Ejecutar con **Run** en un emulador o un celular con depuración USB.
3. Para generar el APK: **Build → Build App Bundle(s) / APK(s) → Build APK(s)**.

## Alcance de esta versión

Fuera del alcance por ahora: Firebase (autenticación, base de datos y almacenamiento), la pasarela real de Mercado Pago, la geolocalización y el almacenamiento permanente de fotos. La cámara funciona para la vista previa de fotos de productos, del perfil y de los documentos del comercio.

## Notas / pendientes

- El texto de interfaz dentro de los enums `Rol`, `EstadoCuenta`, `EstadoPedido` y `Categoria` (`data/Modelos.kt`) está escrito en Kotlin en lugar de `strings.xml`, a diferencia del resto de la aplicación.
- Varios `ImageView` no tienen `contentDescription`; los decorativos deberían declarar `android:importantForAccessibility="no"`.
- `compileSdk` está en 35 mientras que `targetSdk` está en 34.
- Quedan algunos strings sin usar y dos drawables sin usar (`ic_escudo`, `ic_filtro`).
- El APK está versionado en el repositorio; publicarlo con GitHub Releases mantendría el historial más liviano.
- El proyecto no tiene pruebas unitarias ni instrumentadas.

---
Proyecto académico · Desarrollo de Aplicaciones Móviles Nativas · Fundación Universitaria Compensar · 2026
