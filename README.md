# Taller 8 — De la Pokédex guiada a Rick & Morty

Proyecto Android Kotlin + Jetpack Compose que consume la API pública de Rick & Morty.

## Cómo usar
1. Android Studio: **File > New > Import Project** y selecciona esta carpeta (o copia sus archivos a un proyecto Compose existente).
2. Revisa `app/build.gradle.kts`: conserva/ajusta el `plugins` y las versiones del proyecto según tu Android Studio. Sincroniza Gradle.
3. Ejecuta en emulador/dispositivo con Internet.
4. Completa las evidencias reales en `capturas/` (ver `capturas/README.md`).

> Este paquete incluye los archivos de aplicación y una configuración Gradle base. Si tu proyecto de clase ya tiene versiones de AGP/Kotlin/Compose, es preferible conservar sus versiones y agregar las dependencias indicadas.

## Funcionalidad
- Consulta `https://rickandmortyapi.com/api/character`.
- Lista real con nombre, estado y especie.
- Pantallas Loading, Success y Error; botón Reintentar y recargar.
- Navegación Compose al detalle enviando solamente el ID.
- Repository convierte fallos a `Result`, relanzando `CancellationException`.
- Detalle consulta `character/{id}` desde su ViewModel.
