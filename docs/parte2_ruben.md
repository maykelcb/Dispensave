# GUÍA Y PLANTILLA OFICIAL — EVALUACIÓN FINAL
## DOCUMENTO DE ENTREGA: PARTE 2
### Responsable Asignado: Integrante 2 (Rubén / Especialista de Análisis, Diseño y APIs)
### Rama Oficial de GitHub: `feature/retrofit-api-consumption`
### Repositorio: [https://github.com/maykelcb/DispenSave](https://github.com/maykelcb/DispenSave)

---

> [!IMPORTANT]
> ### 📋 SECCIONES DEL INFORME ACADÉMICO ASIGNADAS A LA PARTE 2:
> **1. Sección 3: Análisis y diseño** (Historias de usuario HU01-HU16 formato Gherkin, Requerimientos Funcionales RF01-RF12, Requerimientos No Funcionales RNF01-RNF06 y Reglas de Negocio RN01-RN05).  
> **2. Sección 5: Implementación técnica** (Persistencia local Room SQLite, Consumo REST con Retrofit 2 + OpenFoodFacts, Justificación Offline-First frente a Firebase y Geolocalización con Google Maps).  
> 
> *Tus módulos de código en Android Studio se ubican en:* `data/remote/` (`FoodApiService.kt`, `RetrofitClient.kt`), `data/model/` (`OpenFoodFactsDto.kt`), `ui/screens/search/` (`ProductSearchScreen.kt`) y `domain/model/`.

---

# 3. ANÁLISIS Y DISEÑO

### 3.1 Historias de Usuario (Product Backlog con Criterios Gherkin)

A continuación se presentan las **16 Historias de Usuario** que componen el backlog del sistema DispenSave:

| ID | Historia de Usuario | Criterios de Aceptación (Gherkin) | Prioridad | Estimación |
|:---:|:---|:---|:---:|:---:|
| **HU01** | **Como** usuario nuevo, **quiero** registrarme seleccionando mi rol (Consumidor o Comercio) **para** acceder a funciones según mi perfil. | **Dado** que estoy en `RegisterScreen`, **cuando** ingreso correo válido y clave ≥ 6 caracteres y elijo mi rol, **entonces** el sistema almacena mi usuario y redirige al panel principal. | Must | 5 SP |
| **HU02** | **Como** usuario registrado, **quiero** iniciar sesión con mis credenciales **para** acceder a mi despensa guardada. | **Dado** que introduzco usuario y clave correctos, **cuando** pulso "Ingresar", **entonces** la app valida la sesión; si falla, muestra advertencia visual. | Must | 3 SP |
| **HU03** | **Como** usuario, **quiero** navegar entre secciones mediante una barra inferior **para** moverme fluidamente en la aplicación. | **Dado** que me autentico, **cuando** toco un ícono de la `NavigationBar`, **entonces** la pantalla conmuta conservando el estado previo. | Must | 5 SP |
| **HU04** | **Como** usuario consumidor, **quiero** registrar un alimento en mi despensa **para** llevar control de mis existencias. | **Dado** el diálogo `AddProductDialog`, **cuando** completo nombre, categoría, fecha y cantidad y guardo, **entonces** el alimento se inserta en Room SQLite. | Must | 5 SP |
| **HU05** | **Como** usuario, **quiero** ver mis alimentos ordenados cronológicamente por vencimiento **para** consumir primero lo que antes vence (FEFO). | **Dado** que abro `PantryScreen`, **cuando** la lista se renderiza, **entonces** los productos aparecen ordenados en orden ascendente por fecha de expiración. | Must | 5 SP |
| **HU06** | **Como** usuario, **quiero** ver alertas semafóricas de color en mis alimentos **para** reconocer de un vistazo el riesgo de caducidad. | **Dado** un producto en despensa, **cuando** faltan > 3 días muestra Verde; si faltan ≤ 3 días muestra Amarillo; si ya venció muestra Rojo. | Must | 5 SP |
| **HU07** | **Como** usuario, **quiero** editar la cantidad y fecha de vencimiento de un alimento **para** actualizar mi inventario cuando consumo parte de él. | **Dado** un alimento en `ProductDetailScreen`, **cuando** modifico valores y guardo, **entonces** la base de datos Room actualiza el registro. | Must | 5 SP |
| **HU08** | **Como** usuario, **quiero** eliminar un alimento con confirmación previa **para** no perder registros por toques accidentales. | **Dado** que presiono eliminar, **cuando** el modal `ConfirmDeleteDialog` aparece y confirmo, **entonces** se borra permanentemente de SQLite. | Must | 3 SP |
| **HU09** | **Como** usuario, **quiero** buscar alimentos en un catálogo externo online **para** autocompletar la información sin escribir manualmente. | **Dado** que ingreso a `ProductSearchScreen`, **cuando** escribo un término (ej. "arroz"), **entonces** la app consulta la API REST de OpenFoodFacts y muestra resultados. | Must | 8 SP |
| **HU10** | **Como** usuario, **quiero** visualizar estados de carga (Loading) y error en la búsqueda **para** saber si la red está ocupada o falló. | **Dado** que busco online, **cuando** la petición está en curso se muestra un spinner; si no hay internet se muestra un error amigable con botón de reintento. | Should | 3 SP |
| **HU11** | **Como** usuario, **quiero** importar un alimento de la API directamente a mi despensa **para** registrar compras rápidamente. | **Dado** un resultado de búsqueda, **cuando** toco "Añadir a Despensa", **entonces** se inserta automáticamente en Room SQLite con fecha sugerida. | Must | 5 SP |
| **HU12** | **Como** usuario, **quiero** gestionar una lista de compras con casillas de verificación **para** recordar los víveres por comprar. | **Dado** que visito `ShoppingListScreen`, **cuando** toco un ítem, **entonces** la casilla cambia a marcado con tachado visual. | Must | 5 SP |
| **HU13** | **Como** usuario, **quiero** trasladar los artículos comprados a mi despensa en un solo paso **para** no registrarlos uno por uno. | **Dado** que marqué ítems como comprados, **cuando** se ejecuta el auto-traslado, **entonces** se insertan como alimentos activos en Room SQLite. | Must | 5 SP |
| **HU14** | **Como** comercio, **quiero** publicar ofertas de paquetes sorpresa de excedentes **para** evitar tirar comida fresca al final del día. | **Dado** que el usuario es `COMERCIO`, **cuando** publica un pack con precio de rescate y horario, **entonces** la oferta se publica en el marketplace. | Should | 5 SP |
| **HU15** | **Como** consumidor, **quiero** explorar y reservar paquetes sorpresa cercanos **para** comprar comida de calidad con 50%-70% de descuento. | **Dado** que visito `ExploreScreen`, **cuando** elijo un pack y pulso reservar, **entonces** se descuenta del stock y se genera un comprobante de orden. | Should | 5 SP |
| **HU16** | **Como** usuario, **quiero** visualizar mi impacto ecológico y dinero ahorrado **para** conocer mi contribución al planeta. | **Dado** que rescato o consumo alimentos a tiempo, **cuando** abro `ImpactScreen`, **entonces** visualizo los kilogramos de CO₂ evitados y el ahorro acumulado. | Could | 3 SP |

---

### 3.2 Requerimientos Funcionales (RF)
* **RF01:** El sistema debe permitir el registro de usuarios diferenciando los roles `CLIENTE` y `COMERCIO`.
* **RF02:** El sistema debe autenticar las credenciales del usuario y mantener la sesión persistente en el dispositivo.
* **RF03:** El sistema debe permitir crear, listar, modificar y eliminar alimentos de la despensa de forma reactiva (CRUD).
* **RF04:** El sistema debe calcular y mostrar el semáforo visual de caducidad (Verde, Amarillo y Rojo) según la fecha actual.
* **RF05:** El sistema debe ordenar la lista de alimentos en orden ascendente por fecha de expiración (*FEFO*).
* **RF06:** El sistema debe consultar la API REST de OpenFoodFacts mediante HTTP GET y mostrar los resultados paginados.
* **RF07:** El sistema debe permitir agregar un alimento retornado por la API REST directamente al inventario de Room SQLite.
* **RF08:** El sistema debe gestionar una lista de compras interactiva con casillas de verificación.
* **RF09:** El sistema debe migrar de manera automatizada los ítems de compras marcados como adquiridos hacia la despensa local.
* **RF10:** El sistema debe desplegar un diálogo modal de confirmación antes de ejecutar la eliminación física de un registro.
* **RF11:** El sistema debe permitir a comercios aliados publicar ofertas de rescate con precios de descuento y franjas de retiro.
* **RF12:** El sistema debe cuantificar y proyectar el impacto ecológico (CO₂ evitado) y ahorro monetario en `ImpactScreen`.

---

### 3.3 Requerimientos No Funcionales (RNF)
* **RNF01 (Rendimiento):** El tiempo de renderizado de la lista de despensa en Jetpack Compose no debe superar los 16 ms por cuadro (*60 FPS*), garantizando fluidez sin *jank*.
* **RNF02 (Persistencia Offline-First):** Las operaciones locales de despensa y compras deben funcionar al 100% sin conexión a Internet mediante SQLite y Room.
* **RNF03 (Seguridad):** Los datos almacenados deben permanecer aislados en el almacenamiento interno privado de la aplicación (`/data/data/com.example.dispensave/`). Todas las peticiones HTTP externas deben utilizar HTTPS/TLS 1.3 obligatorio.
* **RNF04 (Usabilidad y Accesibilidad):** Los botones principales de interacción deben poseer un área táctil mínima de 48 × 48 dp conforme a las pautas de Material Design 3.
* **RNF05 (Disponibilidad y Tolerancia a Fallos):** Las peticiones de red a la API de OpenFoodFacts deben contar con *timeouts* de 15 segundos y captura estructurada de excepciones de red sin provocar cierres inesperados (*crashes*).
* **RNF06 (Compatibilidad):** La aplicación debe operar de forma nativa desde Android 8.0 Oreo (API 26) hasta Android 15 (API 35).

---

### 3.4 Reglas de Negocio (RN)
* **RN01 (Algoritmo FEFO de Caducidad):** La criticidad del producto se evalúa numéricamente restando el timestamp de vencimiento con el timestamp actual:
  - Faltan > 3 días (72 hrs): Estado `🟢 DISPONIBLE` (Vigente).
  - Faltan entre 0 y 3 días: Estado `🟡 POR VENCER` (Consumo prioritario).
  - Fecha superada (< 0): Estado `🔴 VENCIDO` (Atención inmediata / descarte responsable).
* **RN02 (Descuento de Rescate de Excedentes):** Todo pack publicado por un comercio debe tener un precio de rescate estrictamente menor al precio original de lista (`rescuePrice < originalPrice`), recomendándose descuentos entre el 50% y 70%.
* **RN03 (Deducción Automática de Stock):** Cada reserva de pack sorpresa efectuada por un cliente descuenta de forma transaccional una unidad del stock disponible (`stockAvailable - 1`). Si el stock llega a 0, la oferta no permite nuevas reservas.
* **RN04 (Auto-traslado de Compras):** Al marcar un artículo como comprado en la lista de compras, se programa una vigencia estimada por defecto (ej. 14 días) y se traslada a la tabla `products` de Room SQLite sin duplicidades.
* **RN05 (Protección Contra Eliminación Accidental):** Ningún alimento puede ser eliminado de la base de datos de forma directa; debe mediar la aceptación explícita del usuario en `ConfirmDeleteDialog`.

---

# 5. IMPLEMENTACIÓN TÉCNICA

### 5.1 Persistencia Local (Room Persistence Library sobre SQLite)
DispenSave utiliza **Room Database (v2.6.1)** como motor relacional nativo en el dispositivo:
* **Entidad `ProductEntity`:** Mapea la tabla `products` con clave primaria autonumérica, nombre, categoría, timestamp numérico de caducidad (`Long`), cantidad física y unidad de medida.
* **DAO `ProductDao`:** Expone sentencias SQL verificadas en compilación. La consulta de lectura `getAllProductsSortedByExpiration()` retorna un `Flow<List<ProductEntity>>` reactivo que emite eventos instantáneos ante cualquier mutación.
* **Base de Datos `AppDatabase`:** Implementada como Singleton de acceso seguro a hilos mediante `synchronized(this)` con soporte para migraciones controladas.

### 5.2 Servicios REST (Retrofit 2 + OkHttp + OpenFoodFacts)
* **Librerías:** Retrofit 2.9.0 + OkHttp 4.12.0 + Gson Converter 2.9.0.
* **Endpoint Consumido:** `@GET("cgi/search.pl")` hacia `https://world.openfoodfacts.org/`.
* **Parámetros:** `search_terms`, `search_simple=1`, `action=process`, `json=1`, `page_size=20`.
* **Modelos DTO:** `OpenFoodFactsResponseDto` y `RemoteFoodProductDto` deserializan automáticamente las cargas JSON en objetos Kotlin de tipo seguro.
* **Manejo de Estados de Red:** Modelado con la Sealed Class `ProductSearchUiState` (`Idle`, `Loading`, `Success`, `Error`), capturando `UnknownHostException` y `SocketTimeoutException`.

### 5.3 Justificación Técnica de Persistencia Local frente a Firebase
Aunque la rúbrica contempla Firebase como alternativa, en DispenSave se adoptó estratégicamente **Room SQLite + Retrofit REST** por sólidas razones de ingeniería de software:
1. **Filosofía Offline-First Permanente:** El usuario debe auditar y modificar su despensa en cualquier momento (incluso dentro de sótanos o refrigeradores sin señal móvil), lo cual SQLite garantiza al 100% sin latencias ni consumo de datos.
2. **Privacidad de Datos Domésticos:** Los patrones de consumo y alimentos almacenados en el hogar no se transmiten a servidores de terceros, protegiendo la intimidad del usuario.
3. **Cero Costos de Infraestructura y Mantenimiento:** Elimina dependencias de cuotas de lectura/escritura en la nube (*Billing limits* de Firestore).

### 5.4 Geolocalización (Google Maps API y Comercios Aliados)
* **Librerías:** `com.google.maps.android:maps-compose:4.3.3` y Google Play Services Location (`play-services-location:21.2.0`).
* **Funcionalidad:** En `ExploreScreen.kt`, permite al usuario visualizar comercios aliados en un mapa interactivo con marcadores geográficos que exhiben el horario de recogida y los packs sorpresa disponibles.
* **Gestión de Permisos:** Se solicitan dinámicamente los permisos `ACCESS_FINE_LOCATION` y `ACCESS_COARSE_LOCATION` en tiempo de ejecución.
