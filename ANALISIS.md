# ANALISIS.md — Taller 8 Rick & Morty

## 1. Predicción inicial (antes de programar)

**Pregunta:** Si el Repository llama a Retrofit sin `try/catch` desde `viewModelScope.launch`, y desactivo Internet antes de volver a cargar, ¿qué ocurre y por qué?

**Predicción:**

La petición realizada mediante Retrofit no podrá establecer la conexión con el servidor de Rick & Morty porque el dispositivo no tendrá acceso a Internet. Como consecuencia, se producirá una excepción relacionada con la conexión, como `UnknownHostException`, `ConnectException` o `SocketTimeoutException`.

Si el Repository no tiene un manejo de excepciones, el error llegará a la corrutina del ViewModel y no se podrá convertir correctamente en un estado de error para la interfaz. Por lo tanto, la aplicación puede presentar un fallo durante la operación en lugar de mostrar un mensaje controlado al usuario.

## 2. JSON y diferencia con PokéAPI

La API de Rick & Morty devuelve un objeto JSON que contiene información general de la consulta y una colección llamada `results`. Dentro de esta colección se encuentran los personajes, cada uno con campos como:

- `id`: identificador del personaje.
- `name`: nombre del personaje.
- `status`: estado del personaje, por ejemplo, Alive o Dead.
- `species`: especie a la que pertenece.
- `image`: dirección de la imagen del personaje.

A diferencia de PokéAPI, que utiliza una estructura de datos orientada a Pokémon, Rick & Morty maneja información propia de sus personajes. Por esta razón, no es suficiente con cambiar únicamente la dirección de la API: también es necesario adaptar los modelos de datos, las consultas y la forma en que se presenta la información en la aplicación.

En el proyecto se creó el modelo `Personaje` para representar los datos recibidos y permitir que la aplicación muestre la información correspondiente.

## 3. Excepción observada

Durante la prueba de la versión temporal sin manejo de errores, se desactivó la conexión a Internet y se intentó realizar nuevamente la petición a la API.

El error identificado en Logcat fue:

**Excepción:** `java.net.UnknownHostException`

**Mensaje:**

`Unable to resolve host "rickandmortyapi.com": No address associated with hostname`

**Dispositivo:** Teléfono Android utilizado para ejecutar las pruebas.

**Causa inmediata:** El dispositivo no pudo resolver el nombre del dominio de la API porque no tenía conexión a Internet.

**Captura:** `capturas/03_logcat_sin_manejo.png`

Esta excepción confirmó que el problema se produjo durante la resolución del dominio del servidor y no por un error en los modelos de datos o en la interfaz de la aplicación.

## 4. Causa técnica

La aplicación utiliza Retrofit para realizar solicitudes HTTP a la API de Rick & Morty. Estas solicitudes se ejecutan mediante funciones suspendidas, que permiten realizar operaciones de red sin bloquear el hilo principal.

Cuando se desactiva Internet, la aplicación no puede establecer comunicación con el servidor. En consecuencia, la operación genera una excepción de conexión.

En la primera versión de prueba, el Repository no capturaba esta excepción mediante `try/catch`. Por esta razón, el error no podía convertirse en un estado controlado para la interfaz.

Después de implementar el manejo de excepciones, el Repository devuelve un resultado de tipo `Result.failure` cuando ocurre un error. El ViewModel recibe ese resultado y actualiza el estado de la pantalla a `Error`, permitiendo que Jetpack Compose muestre un mensaje y la opción de volver a intentar la petición.

## 5. Corrección implementada

Para solucionar el problema se realizaron los siguientes cambios:

- Se implementó un bloque `try/catch` en los métodos del archivo `PersonajesRepository.kt`.
- Se agregó el manejo especial de `CancellationException`, que se vuelve a lanzar para respetar la cancelación normal de las corrutinas.
- Las demás excepciones se convierten en `Result.failure`, evitando que los errores de conexión interrumpan el flujo normal de la interfaz.
- En `PersonajesViewModel.kt` se administran los estados `Loading`, `Success` y `Error`.
- Se agregó la opción **Reintentar** cuando ocurre un error de conexión.
- Se implementó la opción **Recargar** para volver a solicitar la lista de personajes.
- Se utilizó `DetalleViewModel` para consultar la información individual de cada personaje mediante el endpoint `character/{id}`.

Con estos cambios, la aplicación puede informar al usuario cuando no existe conexión y permitirle realizar nuevamente la petición sin necesidad de cerrar la aplicación.

## 6. Diferencia entre error HTTP y fallo de conexión

Existen diferencias entre un error HTTP y un fallo de conexión:

- **`HttpException`:** ocurre cuando el servidor recibe la solicitud y responde con un código HTTP no exitoso, como 404 (recurso no encontrado) o 500 (error interno del servidor). Esto significa que sí se obtuvo una respuesta HTTP, pero esta indica que la solicitud no fue exitosa.
- **`IOException`:** representa problemas relacionados con la entrada y salida de datos, como fallos de conexión, tiempos de espera o problemas de comunicación con el servidor.
- **`UnknownHostException`:** es un tipo específico de `IOException` que aparece cuando no se puede resolver el nombre del dominio del servidor mediante DNS.

En este ejercicio se observó una `UnknownHostException`, debido a que el dispositivo no pudo resolver el dominio `rickandmortyapi.com` después de desactivar Internet.

Es importante aclarar que esta excepción no significa necesariamente que falte el permiso `INTERNET` en el archivo `AndroidManifest.xml`; también puede producirse por problemas de conectividad o resolución DNS.

## 7. Decisión de diseño del detalle

Se implementó la opción **A**: la pantalla de detalle recibe únicamente el identificador del personaje mediante Navigation Compose.

Después, `DetalleViewModel` utiliza ese identificador para consultar directamente el endpoint `character/{id}` de la API.

Esta decisión permite que la pantalla de detalle obtenga la información del personaje sin depender de que previamente se haya cargado la lista. También mantiene separadas las responsabilidades, evitando realizar las consultas directamente desde los componentes visuales de Compose.

## 8. Evidencias

Las siguientes capturas permiten comprobar el funcionamiento de la aplicación y el manejo de errores:

1.  muestra la lista de personajes obtenida desde la API, incluyendo sus nombres, estados e imágenes.
2.  muestra la pantalla de detalle de un personaje con su información correspondiente.
3.  evidencia la excepción `UnknownHostException` durante la prueba temporal sin manejo de errores.
4.  muestra la pantalla de error controlado cuando se desactiva la conexión a Internet.
5.  muestra la lista de personajes después de recuperar la conexión y utilizar la opción Reintentar.

