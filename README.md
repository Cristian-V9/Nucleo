[English](README.md) | [Español](README.es.md)

# Nucleo · Android marketplace

Native Android app that works as a marketplace for desktop computer components (processors, graphics cards, RAM, storage, motherboards and power supplies), sold by legally registered retailers in Bogota.

This version is the complete frontend: every screen, role-based navigation and visual resources. Data is sample data kept in memory; the Firebase and Mercado Pago integration is left for the next version.

## Download and try it

- **APK:** [`apk/Nucleo-v1.0.apk`](apk/Nucleo-v1.0.apk) (Android 8.0 or higher).
- **Test accounts** (password `12345678`):

| Role | Email |
|---|---|
| Buyer | `comprador@nucleo.co` |
| Seller | `vendedor@nucleo.co` |
| Administrator | `admin@nucleo.co` |

Fingerprint login is enabled after the first password login, on a device that has a registered fingerprint.

## Screens by role

| Role | Screens |
|---|---|
| Shared | Splash, login (email and fingerprint), sign up, business registration, password recovery, role-based bottom navigation bar |
| Buyer | Home, catalog, product detail (price comparison across retailers), cart, checkout, profile with orders |
| Seller | My products (CRUD), product form with camera and gallery, incoming orders, seller profile |
| Administrator | Dashboard, users (list, create, edit, delete), products (hide/show), sales report, business applications |

## Tech stack

- Kotlin, XML layouts and **ViewBinding**
- **Navigation Component**: one Activity per role (`activity_comprador.xml`, `activity_vendedor.xml`, `activity_admin.xml`) with fragments and `BottomNavigationView`
- **Material Design 3** with a custom dark theme
- **BiometricPrompt** for login and fingerprint payment confirmation
- **SplashScreen API** for the welcome screen
- `minSdk 26`, `targetSdk 34`

## Structure

```
app/src/main/
├── java/com/nucleo/tienda/
│   ├── data/            Models and repository with sample data
│   ├── util/            Price formatting, session and biometrics
│   └── ui/
│       ├── splash/      Welcome screen
│       ├── auth/        Login, sign up, business registration, password recovery
│       ├── comprador/   Home, catalog, detail, cart, checkout
│       ├── vendedor/    Products, product form, orders
│       ├── admin/       Dashboard, users, products, sales, applications
│       └── comun/       Profile, adapters, dialogs and shared components
└── res/
    ├── layout/          Activities, fragments, list items and reusable includes
    ├── values/          colors, themes, styles, dimens, strings, attrs
    ├── color/           Per-state color selectors (chips, bottom bar, fields)
    ├── drawable/        Shapes (shape, layer-list) and vector icons
    ├── font/            IBM Plex Sans and Space Grotesk
    ├── menu/            Bottom bar menus per role
    └── navigation/      Navigation graphs per role
```

## Visual resources

- **Colors** (`values/colors.xml`): dark background `#0B1220`, primary action `#2563EB` and one accent color per role (buyer blue, seller cyan, administrator violet).
- **Themes** (`values/themes.xml`): `Theme.Nucleo` inherits from Material 3 dark and redefines colors, typography and component styles. `Theme.Nucleo.Comprador`, `.Vendedor` and `.Admin` only change the custom `colorRol` attribute (declared in `values/attrs.xml`).
- **Styles** (`values/styles.xml`): buttons, text fields, cards, chips, navigation bar, status pills and text appearances.
- **Drawables**: rounded shapes, dashed border, chart bars and 46 custom vector icons.

## Build

1. Open the project folder in Android Studio (**File → Open**) and wait for the Gradle Sync.
2. Press **Run** on an emulator or a phone with USB debugging enabled.
3. To generate the APK: **Build → Build App Bundle(s) / APK(s) → Build APK(s)**.

## Scope of this version

Out of scope for now: Firebase (authentication, database and storage), the real Mercado Pago gateway, geolocation and permanent photo storage. The camera works for previewing product, profile and business document photos.

## Notes / pending

- UI text inside the `Rol`, `EstadoCuenta`, `EstadoPedido` and `Categoria` enums (`data/Modelos.kt`) is hardcoded in Kotlin instead of `strings.xml`, unlike the rest of the app.
- Several `ImageView` elements are missing `contentDescription`; decorative ones should declare `android:importantForAccessibility="no"`.
- `compileSdk` is 35 while `targetSdk` is 34.
- A few unused strings and two unused drawables (`ic_escudo`, `ic_filtro`) are still declared.
- The APK is committed to the repository; publishing it through GitHub Releases would keep the history smaller.
- The project has no unit or instrumentation tests.

---
Academic project · Native Mobile Application Development · Fundacion Universitaria Compensar · 2026
