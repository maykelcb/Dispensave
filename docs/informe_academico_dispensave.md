# INFORME DE PROYECTO DE DESARROLLO DE SOFTWARE
# DISPENSAVE – Plataforma de Rescate de Comida

---

**Integrantes:**

| N° | Nombre | Parte del Informe |
|----|--------|-------------------|
| 1 | Maykel | Parte 1 – Sección IV.1, IV.2, V.1 (HU01–HU04), V.2 Sprint 1, VI.1 |
| 2 | Rubén  | Parte 2 – Sección IV.4, V.1 (HU05–HU08), V.2 Sprint 2, VI.2 |
| 3 | Luis   | Parte 3 – Sección IV.3, V.1 (HU09–HU12), V.2 Sprint 3, VI.3 |
| 4 | Andy   | Parte 4 – Sección IV.2 avanzada, V.1 (HU13–HU16), V.2 Sprint 4, VI.4 |

**Repositorio GitHub:** https://github.com/maykelcb/DispenSave

---

---

# ════════════════════════════════════════════
# PARTE 1 – MAYKEL
# ════════════════════════════════════════════

---

## IV. DISEÑO DE LA SOLUCIÓN

### 4.1 Arquitectura de la Aplicación

#### 4.1.1 Arquitectura Seleccionada: MVVM (Model-View-ViewModel)

DispenSave implementa el patrón arquitectónico **MVVM (Model-View-ViewModel)**, que es el estándar recomendado por Google para aplicaciones Android modernas desarrolladas con Jetpack Compose.

Esta arquitectura divide la aplicación en tres capas bien definidas que se comunican de forma unidireccional, facilitando la separación de responsabilidades, la escalabilidad del código y la facilidad de pruebas unitarias.

#### 4.1.2 Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────┐
│                   CAPA DE PRESENTACIÓN                   │
│                   (Jetpack Compose UI)                    │
│                                                          │
│  OnboardingScreen  LoginScreen  RegisterScreen           │
│  ExploreScreen     StoreDashboardScreen                  │
│  MyOrdersScreen    ImpactScreen                          │
│                         │                               │
│              Observa StateFlow / State                   │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│                  CAPA DE LÓGICA (ViewModel)              │
│                                                          │
│              PantryViewModel                             │
│  - currentUserRole      - merchantInventory             │
│  - surplusBags          - orders                        │
│  - registerUser()       - loginUser()                   │
│  - publishSurplusBag()  - addMerchantProduct()          │
│  - calcularImpacto()    - confirmOrderPickup()          │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│                   CAPA DE DOMINIO                        │
│                                                          │
│  User           SurplusBag      Order                   │
│  MerchantProduct  StoreCategory  OfferType              │
│  CategoryCatalog  ValidationUtils                        │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│                   CAPA DE DATOS                          │
│                                                          │
│  UserRepository         RescueRepository                │
│  PantryRepository       AppDatabase (Room)              │
│  ← En Parte 4: Firebase Auth + Firestore →              │
└─────────────────────────────────────────────────────────┘
```

#### 4.1.3 Componentes Principales

**Capa de Presentación (View):**
Implementada con **Jetpack Compose**, es 100% declarativa. Cada pantalla es una función `@Composable` que se re-renderiza automáticamente cuando el estado del ViewModel cambia. No contiene lógica de negocio.

**Capa de Lógica (ViewModel):**
`PantryViewModel` centraliza todo el estado de la aplicación y la lógica de negocio. Se comunica con los repositorios y expone datos a la UI mediante `StateFlow` y `MutableState` de Compose. Sobrevive a los cambios de configuración del dispositivo.

**Capa de Dominio (Model):**
Contiene las entidades puras del negocio (`User`, `SurplusBag`, `Order`, `MerchantProduct`) sin dependencias de Android. Esto permite su uso en pruebas unitarias sin necesidad de emulador.

**Capa de Datos (Repository):**
Los repositorios actúan como única fuente de verdad. `UserRepository` gestiona la autenticación y persistencia de usuarios. `RescueRepository` gestiona las ofertas de rescate y pedidos. Actualmente usan almacenamiento en memoria y `SharedPreferences`; en la Parte 4 serán reemplazados por Firebase.

#### 4.1.4 Flujo General de Información

```
Usuario interactúa con la UI
        │
        ▼
Pantalla Compose llama a función del ViewModel
        │
        ▼
ViewModel ejecuta lógica de negocio y llama al Repository
        │
        ▼
Repository accede a datos (SharedPrefs / Room / Firebase)
        │
        ▼
Repository devuelve resultado al ViewModel
        │
        ▼
ViewModel actualiza StateFlow / MutableState
        │
        ▼
Compose re-renderiza automáticamente la UI
```

#### 4.1.5 Justificación de la Arquitectura

Se eligió **MVVM** por las siguientes razones técnicas:

1. **Compatibilidad nativa con Jetpack Compose**: Google diseñó Compose específicamente para trabajar con ViewModel y StateFlow, lo que reduce el código boilerplate.
2. **Separación de responsabilidades**: La UI no conoce la fuente de los datos (si vienen de memoria, Room o Firebase), lo que facilita la migración a backend real en la Parte 4 sin modificar las pantallas.
3. **Sobrevivencia a rotación de pantalla**: El ViewModel sobrevive a los cambios de configuración, evitando pérdida de estado.
4. **Testabilidad**: El dominio y los repositorios pueden probarse de forma unitaria sin contexto de Android.

---

### 4.2 Tecnologías Utilizadas

#### Lenguaje de Programación: Kotlin

**Justificación:** Kotlin es el lenguaje oficial de Google para el desarrollo Android desde 2019. Ofrece null safety (prevención de NullPointerException), funciones de extensión, coroutines para programación asíncrona y una sintaxis más concisa que Java. Es 100% interoperable con Java y tiene soporte completo en Android Studio.

#### Framework de Desarrollo UI: Jetpack Compose

**Versión:** BOM 2024.02.01

**Justificación:** Compose es el toolkit moderno y declarativo de Google para crear interfaces Android. Elimina la necesidad de archivos XML para layouts, permite crear UI reactiva (se actualiza automáticamente al cambiar el estado) y reduce significativamente las líneas de código en comparación con View System. Es el estándar actual de la industria para desarrollo Android.

#### Gestión de Estado: ViewModel + StateFlow

**Justificación:** `StateFlow` es un flujo de datos que emite el último valor a todos sus colectores, ideal para estados de UI que necesitan ser observados por múltiples pantallas. `ViewModel` gestiona el ciclo de vida del estado de forma independiente a la UI.

#### Persistencia Local: SharedPreferences + Room Database

**SharedPreferences:** Para datos simples como la lista de usuarios registrados (mientras se implementa el backend real). Almacena datos en formato JSON serializado.

**Room Database:** ORM oficial de Android para SQLite. Ya configurado en `AppDatabase.kt` con entidades (`ProductEntity`) y DAOs (`ProductDao`). La Parte 4 lo extenderá para cache offline.

#### Navegación: Navigation Compose

**Versión:** 2.7.7

**Justificación:** Biblioteca oficial de Jetpack para gestión de navegación en Compose. Permite definir rutas tipadas, pasar argumentos entre pantallas y gestionar el backstack de forma declarativa.

---

### V. PLANIFICACIÓN DEL DESARROLLO

### 5.1 Product Backlog – Historias de Usuario (HU01 a HU04)

---

**HU01 – Registro de Usuario**

*Como usuario nuevo, quiero poder registrarme en DispenSave ingresando mis datos personales y eligiendo mi tipo de perfil (Consumidor o Comercio), para poder acceder a las funcionalidades de la plataforma.*

| Criterio | Descripción |
|----------|-------------|
| CA-01 | El sistema valida que el nombre tenga mínimo 3 caracteres y no contenga 3 letras iguales consecutivas |
| CA-02 | El sistema valida que el correo pertenezca a un dominio reconocido (@gmail.com, @outlook.com, dominios .edu.pe, etc.) |
| CA-03 | La contraseña debe tener al menos 6 caracteres, 1 mayúscula, 1 número y 1 carácter especial |
| CA-04 | El usuario debe aceptar los Términos y Condiciones obligatoriamente |
| CA-05 | Si el correo ya está registrado, el sistema muestra un diálogo con opción de iniciar sesión o cancelar |
| CA-06 | Al registrarse exitosamente, el usuario es redirigido directamente al panel principal |

**Valor de Negocio:** ALTO – Sin registro no hay usuarios en la plataforma.

---

**HU02 – Inicio de Sesión**

*Como usuario registrado, quiero poder iniciar sesión con mi correo y contraseña para acceder a mi cuenta y funcionalidades según mi rol.*

| Criterio | Descripción |
|----------|-------------|
| CA-01 | Si el correo no existe en el sistema, mostrar mensaje "Correo no registrado" |
| CA-02 | Si la contraseña es incorrecta, mostrar mensaje "Contraseña incorrecta" (sin revelar si el correo existe) |
| CA-03 | Al iniciar sesión exitosamente, la navegación se adapta automáticamente según el rol del usuario |
| CA-04 | El botón de retroceso regresa a la pantalla de bienvenida, no cierra la app |

**Valor de Negocio:** ALTO – Funcionalidad crítica de acceso a la plataforma.

---

**HU03 – Registro de Comercio con Selección de Rubro**

*Como dueño de un establecimiento gastronómico, quiero registrarme como Comercio e indicar el rubro de mi negocio (Panadería, Restaurante, Cafetería o Pastelería), para que los clientes puedan encontrarme y filtrar por mi tipo de local.*

| Criterio | Descripción |
|----------|-------------|
| CA-01 | Al seleccionar el rol "Comercio", aparece el campo "Nombre del Local" (mínimo 3 caracteres) |
| CA-02 | Aparecen 4 opciones de rubro como chips seleccionables (Panadería, Restaurante, Cafetería, Pastelería) |
| CA-03 | El rubro es obligatorio; sin seleccionarlo no se puede completar el registro |
| CA-04 | El rubro seleccionado se persiste en el perfil del usuario y es visible en su panel |

**Valor de Negocio:** ALTO – Define la categoría del comercio en toda la plataforma.

---

**HU04 – Gestión del Catálogo Propio del Comercio**

*Como dueño de un comercio, quiero gestionar un catálogo de productos que vendo, para poder seleccionarlos al publicar ofertas de rescate sin tener que escribirlos manualmente cada vez.*

| Criterio | Descripción |
|----------|-------------|
| CA-01 | Puedo agregar productos escribiendo libremente el nombre, stock inicial y unidad de medida |
| CA-02 | El sistema rechaza nombres duplicados o con menos de 2 caracteres |
| CA-03 | Puedo aumentar o disminuir el stock con botones + y – visibles en cada producto |
| CA-04 | Puedo eliminar un producto del catálogo |
| CA-05 | Al publicar una oferta, solo aparecen los productos de MI catálogo (no opciones genéricas del sistema) |
| CA-06 | Al publicar, el stock de los productos incluidos se descuenta automáticamente |

**Valor de Negocio:** ALTO – Diferenciador clave: cada comercio gestiona su propio inventario.

---

### 5.2 Sprint 1 – Autenticación, Roles y Panel del Comercio

**Objetivo del Sprint:** Implementar el sistema completo de autenticación, diferenciación de roles, registro de comercios con categoría y el panel de gestión del comercio con catálogo de productos y publicación de ofertas.

**Duración:** 2 semanas

**Funcionalidades Implementadas:**

| N° | Funcionalidad | Estado |
|----|---------------|--------|
| 1 | Pantalla de Bienvenida (OnboardingScreen) | ✅ Completado |
| 2 | Registro de usuario con validaciones estrictas | ✅ Completado |
| 3 | Inicio de sesión con manejo diferenciado de errores | ✅ Completado |
| 4 | Selector de rol (Consumidor / Comercio) en el registro | ✅ Completado |
| 5 | Selector obligatorio de rubro del comercio (4 categorías) | ✅ Completado |
| 6 | Panel del comercio con 3 pestañas (Publicaciones / Mi Catálogo / Pedidos) | ✅ Completado |
| 7 | Catálogo de productos del comercio con control de stock (+/-) | ✅ Completado |
| 8 | Publicación de Producto Individual desde el catálogo | ✅ Completado |
| 9 | Publicación de Pack de Hoy (multi-selección del catálogo, 60-75% descuento) | ✅ Completado |
| 10 | Validación numérica estricta en precios y cantidades | ✅ Completado |
| 11 | Franjas horarias predefinidas mediante chips (sin texto libre) | ✅ Completado |
| 12 | Persistencia de usuarios con SharedPreferences en JSON | ✅ Completado |
| 13 | Navegación adaptada por rol (barra inferior diferente para cliente y comercio) | ✅ Completado |

**Actividades Ejecutadas:**

1. Diseño e implementación de `ValidationUtils.kt` con regex de validación de correos, nombres y contraseñas
2. Creación del modelo de dominio: `User`, `UserRole`, `StoreCategory`, `SurplusBag`, `OfferType`, `Order`, `MerchantProduct`
3. Implementación de `UserRepository` con persistencia en `SharedPreferences` (serialización JSON)
4. Implementación de `RescueRepository` con datos de prueba precargados y operaciones en memoria
5. Creación de `PantryViewModel` como ViewModel centralizado con `StateFlow`
6. Desarrollo de las pantallas: `OnboardingScreen`, `RegisterScreen`, `LoginScreen`, `StoreDashboardScreen`
7. Implementación del sistema de catálogo del comercio con `mutableStateListOf<MerchantProduct>()`
8. Configuración de `AppNavigation` con rutas y navegación bidireccional correcta

**Evidencias de Seguimiento:**

- Repositorio: https://github.com/maykelcb/DispenSave
- Commits del Sprint 1 disponibles en el historial de Git
- La aplicación compila sin errores con `gradlew assembleDebug` en 4-6 segundos

---

### VI. AVANCE DE IMPLEMENTACIÓN – PARTE 1

#### VI.1 Funcionalidades Mínimas Obligatorias (Parte 1)

| Requisito | Estado | Implementación |
|-----------|--------|----------------|
| Navegación entre pantallas | ✅ | Navigation Compose con rutas: onboarding → register/login → main |
| Persistencia local de datos | ✅ | SharedPreferences para usuarios, Room configurado para productos |
| Operaciones CRUD | ✅ | CRUD completo sobre catálogo de productos del comercio (crear, leer, actualizar stock, eliminar) |
| Validación de formularios | ✅ | ValidationUtils: nombre, correo, contraseña, términos obligatorios |
| Consumo de API/servicio | 🔄 | Preparado para Parte 4 (Andy). Repositorios con interfaz lista para Firebase |
| Manejo básico de errores | ✅ | Mensajes diferenciados en login, diálogos de cuenta existente, errores inline en formularios |

#### VI.2 Capturas de Funcionamiento

**Pantalla de Bienvenida:** Muestra el logo de DispenSave, eslogan y botones "Comenzar" e "Iniciar Sesión".

**Registro de Comercio:** Selector de rol → nombre del local → selector de rubro (4 chips) → correo → contraseña → términos obligatorios → botón "Registrar Comercio".

**Panel del Comercio – Pestaña Mi Catálogo:** Lista de productos con nombre, stock y controles +/-, botón "Agregar" que abre diálogo de texto libre.

**Publicar Pack de Hoy:** Checkboxes de multi-selección del catálogo propio, campos de precio (solo números), chips de franja horaria, indicador de % descuento en vivo.

---

---

# ════════════════════════════════════════════
# PARTE 2 – RUBÉN
# ════════════════════════════════════════════

---

## IV. DISEÑO DE LA SOLUCIÓN (continuación)

### 4.4 Diseño de Interfaces

#### 4.4.1 Principios de Experiencia de Usuario Aplicados

DispenSave aplica los siguientes principios de UX en el diseño de sus interfaces:

**1. Visibilidad del estado del sistema:** El usuario siempre sabe en qué parte de la app se encuentra gracias a la barra de navegación inferior resaltada, los títulos de pantalla y los indicadores de carga.

**2. Consistencia y estándares:** Se usa la misma paleta de colores (verde primario `#2E7D32` y naranja de alerta `#E65100`) en toda la aplicación para identificar acciones positivas (rescatar, confirmar) y urgentes (stock bajo, horario próximo).

**3. Prevención de errores:** Los formularios validan en tiempo real. Los campos de precio solo aceptan números. Las franjas horarias son chips predefinidos, eliminando la posibilidad de ingresar datos inválidos.

**4. Reconocimiento sobre recuerdo:** Los iconos acompañan siempre al texto en la barra inferior y los botones. Los tipos de oferta tienen badges visuales (📦 Pack del Día / 🍽️ Individual).

**5. Diseño estético y minimalista:** Fondo gris claro (`#F8F9FA`), tarjetas blancas con elevación suave, tipografía jerarquizada con pesos Bold/SemiBold/Regular.

#### 4.4.2 Pantallas Principales del Cliente

**Pantalla de Exploración (ExploreScreen)**

Pantalla principal del cliente. Contiene:
- Cabecera con saludo personalizado y acceso al perfil
- Banner informativo de la plataforma
- Chips de filtro horizontal por categoría (Todos / Panadería / Restaurante / Cafetería / Pastelería)
- Contador de resultados disponibles
- Lista de cards de ofertas con toda la información relevante

Cada card de oferta muestra:
- Badge de categoría (verde) y badge de tipo de oferta (Pack del Día naranja / Individual azul)
- Nombre del establecimiento, calificación con estrellas y distancia en km
- Título de la oferta y descripción
- Cuadro verde con contenido detallado del pack (lista de productos incluidos)
- Insignia de "Mayor descuento" con emoji 🎉 para packs con ≥65% de descuento
- Horario de recojo con ícono de reloj (naranja) y stock disponible
- Precio original tachado + precio de rescate en grande + badge rojo con porcentaje de ahorro
- Botón "Rescatar" (verde) o "Agotado" (deshabilitado) según disponibilidad

**Flujo de Reserva**

Al presionar "Rescatar", se muestra un diálogo de confirmación con:
- Título de la oferta y establecimiento
- Lista de productos incluidos (para packs)
- Precio original tachado y precio de rescate con comparativa
- Horario de recojo resaltado
- Botones "Confirmar y Reservar" / "Cancelar"

Tras confirmar, aparece un diálogo de ticket con:
- Ícono de check verde animado
- Código único de reserva (formato DS-XXXX)
- Nombre del establecimiento y horario de recojo
- Instrucción de presentación del código

#### 4.4.3 Flujo de Navegación Completo

```
┌─────────────────┐
│   Bienvenida    │ ← Inicio de la app
└────────┬────────┘
         │
    ┌────┴────┐
    │         │
    ▼         ▼
┌───────┐ ┌───────┐
│Registro│ │ Login │
└───┬───┘ └───┬───┘
    │         │
    └────┬────┘
         │
    ┌────▼────────────────────────────┐
    │       PANEL PRINCIPAL           │
    │  (barra inferior adaptada)      │
    └────────────────────────────────┘
         │
    ┌────┴──────────────────────────┐
    │                               │
    ▼ (rol CLIENTE)    ▼ (rol COMERCIO)
┌──────────┐         ┌──────────────┐
│ Explorar │         │ Mi Comercio  │
│ Rescates │         │ (3 pestañas) │
└──────────┘         └──────────────┘
    │
    ▼
┌──────────────────┐
│ Card de Oferta   │
│ (Pack/Individual)│
└────────┬─────────┘
         │ toca "Rescatar"
         ▼
┌──────────────────┐
│ Diálogo Confirmar│
│ Rescate          │
└────────┬─────────┘
         │ confirma
         ▼
┌──────────────────┐
│ Ticket Digital   │
│ (Código DS-XXXX) │
└──────────────────┘
```

#### 4.4.4 Prototipo de Alta Fidelidad

El diseño implementado puede considerarse un prototipo de alta fidelidad dado que:
- Usa colores finales de la marca DispenSave (verde `#2E7D32`, naranja `#E65100`)
- Tipografía con jerarquía visual real (Bold para títulos, SemiBold para subtítulos, Regular para cuerpo)
- Componentes interactivos reales (no imágenes estáticas)
- Animaciones y transiciones nativas de Compose
- Datos reales precargados que simulan el funcionamiento real

---

### 5.1 Product Backlog – Historias de Usuario (HU05 a HU08)

---

**HU05 – Exploración y Filtrado de Ofertas**

*Como cliente/consumidor, quiero explorar los packs y productos disponibles y filtrar por tipo de establecimiento, para encontrar rápidamente lo que me interesa rescatar.*

| Criterio | Descripción |
|----------|-------------|
| CA-01 | La pantalla muestra todas las ofertas activas al cargar |
| CA-02 | Los chips de filtro (Todos / Panadería / Restaurante / Cafetería / Pastelería) filtran en tiempo real |
| CA-03 | Cada oferta muestra claramente si es "Pack del Día" o "Producto Individual" con badge visual |
| CA-04 | Si no hay ofertas en una categoría, se muestra un mensaje informativo |
| CA-05 | El contador de resultados se actualiza según el filtro activo |

**Valor de Negocio:** ALTO – Es la pantalla más importante para la retención de clientes.

---

**HU06 – Ver Contenido del Pack Antes de Reservar**

*Como cliente, quiero ver exactamente qué productos incluye un pack antes de confirmarlo, para tomar una decisión informada de compra.*

| Criterio | Descripción |
|----------|-------------|
| CA-01 | Las cards de packs muestran la lista de productos incluidos con viñetas |
| CA-02 | El diálogo de confirmación también muestra la lista de productos |
| CA-03 | Los packs con ≥65% de descuento muestran una insignia especial "Mayor Descuento 🔥" |
| CA-04 | Se muestra el precio original tachado junto al precio de rescate y el % de ahorro |

**Valor de Negocio:** ALTO – Aumenta la confianza del cliente y reduce las cancelaciones.

---

**HU07 – Reservar un Rescate y Recibir Ticket**

*Como cliente, quiero reservar una oferta disponible y recibir un código único de retiro para presentarlo en el establecimiento.*

| Criterio | Descripción |
|----------|-------------|
| CA-01 | Al confirmar la reserva, el stock disponible disminuye en 1 |
| CA-02 | Se genera un código único en formato DS-XXXX |
| CA-03 | El diálogo del ticket muestra el código, nombre del local y horario de recojo |
| CA-04 | Si el stock es 0, el botón cambia a "Agotado" y se deshabilita automáticamente |
| CA-05 | La reserva aparece inmediatamente en "Mis Rescates" |

**Valor de Negocio:** MUY ALTO – Es el núcleo de la transacción de la plataforma.

---

**HU08 – Historial de Rescates del Cliente**

*Como cliente, quiero ver mis rescates activos y completados en una sección dedicada, para hacer seguimiento de mis pedidos y poder validar la entrega en el local.*

| Criterio | Descripción |
|----------|-------------|
| CA-01 | La pantalla "Mis Rescates" muestra los pedidos del cliente con estado (Pendiente/Recogido) |
| CA-02 | Los pedidos pendientes tienen el botón "Validar en el Local" |
| CA-03 | Al validar, el cliente muestra el código y confirma que recibió el rescate |
| CA-04 | Los pedidos recogidos muestran el estado "¡Rescatado!" con ícono de check |
| CA-05 | Si no hay rescates, se muestra un mensaje motivacional para explorar |

**Valor de Negocio:** ALTO – La trazabilidad de pedidos fideliza al cliente.

---

### 5.2 Sprint 2 – Experiencia del Cliente y Reservas

**Objetivo del Sprint:** Implementar la experiencia completa del cliente: exploración de ofertas con filtros, vista de contenido de packs, sistema de reservas con generación de ticket digital e historial de rescates.

**Duración:** 2 semanas

**Funcionalidades a Implementar (Rubén):**

| N° | Funcionalidad | Prioridad |
|----|---------------|-----------|
| 1 | Barra de búsqueda en tiempo real por nombre de local o título | ALTA |
| 2 | Imágenes representativas por categoría de establecimiento | ALTA |
| 3 | Sistema de favoritos (guardar locales con persistencia) | MEDIA |
| 4 | Pantalla de detalle expandida antes de confirmar reserva | ALTA |
| 5 | Historial separado: pendientes vs. completados | MEDIA |
| 6 | Estadísticas de ahorro personal en "Mis Rescates" | BAJA |
| 7 | Notificaciones locales (recordatorio diario, aviso de franja horaria) | BAJA |

---

### VI. AVANCE DE IMPLEMENTACIÓN – PARTE 2 (Rubén)

**Funcionalidades base entregadas por Parte 1 (disponibles para Rubén):**

| Funcionalidad | Estado |
|---------------|--------|
| ExploreScreen con filtros por categoría | ✅ Disponible |
| SurplusBagCard con contenido visible y badges | ✅ Disponible |
| Sistema de reservas (reserveBag + generación de código) | ✅ Disponible |
| MyOrdersScreen con vista de cliente | ✅ Disponible |
| PantryViewModel con surplusBags StateFlow | ✅ Disponible |

**Evidencias de colaboración:** El estudiante Rubén deberá subir sus commits desde la rama `feature/parte2-ruben` con al menos 5 commits descriptivos mostrando el progreso incremental de su trabajo.

---

---

# ════════════════════════════════════════════
# PARTE 3 – LUIS
# ════════════════════════════════════════════

---

## IV. DISEÑO DE LA SOLUCIÓN (continuación)

### 4.3 Diseño de Base de Datos

#### 4.3.1 Justificación del Diseño de Datos

DispenSave utiliza dos estrategias de persistencia de datos:

1. **Room Database (SQLite local):** Para los datos que se generan en el dispositivo del usuario y que deben persistir sin necesidad de conexión a internet (historial de productos de la despensa personal).

2. **SharedPreferences:** Para datos simples como los usuarios registrados durante la sesión actual (será reemplazado por Firebase Firestore en la Parte 4).

3. **En memoria (MutableStateFlow):** Para el estado de ofertas de rescate, pedidos y el catálogo del comercio durante la sesión activa.

El diseño relacional fue pensado para soportar la futura migración a Firebase Firestore, donde cada entidad corresponderá a una colección de documentos.

#### 4.3.2 Modelo Entidad-Relación

```
┌─────────────────┐          ┌──────────────────────┐
│    USUARIO      │          │   OFERTA_RESCATE      │
├─────────────────┤          ├──────────────────────┤
│ PK id_usuario   │          │ PK id_oferta          │
│    nombre       │          │ FK id_local            │
│    correo       │          │    categoria_local     │
│    contrasena   │          │    tipo_oferta         │
│    rol          │          │    titulo              │
│ FK id_local (?) │──────────│    descripcion         │
│    fecha_reg    │  1     N │    precio_original     │
└─────────────────┘          │    precio_rescate      │
                              │    franja_horaria      │
                              │    stock_disponible    │
                              │    calificacion        │
                              └────────────┬───────────┘
                                           │
                              ┌────────────▼───────────┐
                              │    ITEM_OFERTA          │
                              ├─────────────────────────┤
                              │ PK id_item              │
                              │ FK id_oferta            │
                              │    nombre_producto      │
                              └─────────────────────────┘

┌─────────────────┐          ┌──────────────────────┐
│    PEDIDO       │          │  PRODUCTO_CATALOGO    │
├─────────────────┤          ├──────────────────────┤
│ PK id_pedido    │          │ PK id_producto        │
│ FK id_usuario   │          │ FK id_usuario_comercio│
│ FK id_oferta    │          │    nombre             │
│    precio_pag   │          │    stock              │
│    franja_rec   │          │    unidad             │
│    codigo_res   │          └──────────────────────┘
│    estado       │
│    timestamp    │
└─────────────────┘

┌─────────────────┐
│  LOCAL_COMERCIO │
├─────────────────┤
│ PK id_local     │
│ FK id_usuario   │
│    nombre_local │
│    categoria    │
│    direccion    │
│    latitud      │
│    longitud     │
└─────────────────┘
```

#### 4.3.3 Diccionario de Datos

**Tabla: USUARIO**

| Campo | Tipo | Restricción | Descripción |
|-------|------|-------------|-------------|
| id_usuario | UUID / String | PK, NOT NULL | Identificador único del usuario |
| nombre | VARCHAR(100) | NOT NULL, MIN 3 | Nombre completo del usuario |
| correo | VARCHAR(150) | NOT NULL, UNIQUE | Correo electrónico (dominio válido) |
| contrasena | VARCHAR(255) | NOT NULL | Contraseña hasheada (SHA-256 en implementación futura) |
| rol | ENUM | NOT NULL | Valores: CLIENTE, COMERCIO |
| fecha_registro | BIGINT | NOT NULL | Timestamp de registro en milisegundos |

**Tabla: OFERTA_RESCATE**

| Campo | Tipo | Restricción | Descripción |
|-------|------|-------------|-------------|
| id_oferta | INT | PK, AUTO_INCREMENT | Identificador único de la oferta |
| id_local | INT | FK → LOCAL_COMERCIO | Comercio que publica la oferta |
| categoria_local | ENUM | NOT NULL | PANADERIA, RESTAURANTE, CAFETERIA, PASTELERIA |
| tipo_oferta | ENUM | NOT NULL | INDIVIDUAL, PACK |
| titulo | VARCHAR(200) | NOT NULL | Título descriptivo de la oferta |
| descripcion | TEXT | NULLABLE | Descripción detallada |
| precio_original | DECIMAL(8,2) | NOT NULL, > 0 | Precio de venta normal del producto |
| precio_rescate | DECIMAL(8,2) | NOT NULL, > 0, < precio_original | Precio de rescate (60-75% menos) |
| franja_horaria | VARCHAR(50) | NOT NULL | Ej: "20:00 - 21:00" |
| stock_disponible | INT | NOT NULL, >= 0 | Unidades disponibles |
| calificacion | DECIMAL(2,1) | DEFAULT 4.8 | Calificación promedio del local |
| activo | BOOLEAN | DEFAULT TRUE | Si la oferta está activa |

**Tabla: PEDIDO**

| Campo | Tipo | Restricción | Descripción |
|-------|------|-------------|-------------|
| id_pedido | INT | PK, AUTO_INCREMENT | Identificador del pedido |
| id_usuario | UUID | FK → USUARIO | Cliente que realizó la reserva |
| id_oferta | INT | FK → OFERTA_RESCATE | Oferta reservada |
| precio_pagado | DECIMAL(8,2) | NOT NULL | Precio al momento de la reserva |
| franja_recojo | VARCHAR(50) | NOT NULL | Franja horaria de recojo |
| codigo_reserva | VARCHAR(10) | NOT NULL, UNIQUE | Código único (ej: DS-7429) |
| estado | ENUM | NOT NULL | PENDIENTE_RECOJO, RECOGIDO |
| timestamp | BIGINT | NOT NULL | Momento de la reserva |

**Tabla: PRODUCTO_CATALOGO**

| Campo | Tipo | Restricción | Descripción |
|-------|------|-------------|-------------|
| id_producto | INT | PK, AUTO_INCREMENT | Identificador del producto |
| id_comercio | UUID | FK → USUARIO | Comercio propietario |
| nombre | VARCHAR(200) | NOT NULL, MIN 2 | Nombre libre del producto |
| stock | INT | NOT NULL, >= 0 | Unidades en inventario |
| unidad | VARCHAR(50) | DEFAULT 'unidad' | Unidad de medida |

#### 4.3.4 Relaciones entre Entidades

| Relación | Tipo | Descripción |
|----------|------|-------------|
| USUARIO → LOCAL_COMERCIO | 1:1 | Un usuario de tipo COMERCIO tiene un único local asociado |
| LOCAL_COMERCIO → OFERTA_RESCATE | 1:N | Un local puede publicar múltiples ofertas activas |
| OFERTA_RESCATE → ITEM_OFERTA | 1:N | Una oferta de tipo PACK contiene N productos incluidos |
| USUARIO → PEDIDO | 1:N | Un cliente puede tener múltiples pedidos |
| OFERTA_RESCATE → PEDIDO | 1:N | Una oferta puede ser reservada por múltiples clientes |
| USUARIO (COMERCIO) → PRODUCTO_CATALOGO | 1:N | Un comercio gestiona N productos en su catálogo |

---

### 5.1 Product Backlog – Historias de Usuario (HU09 a HU12)

---

**HU09 – Ver Estadísticas de Impacto CO2**

*Como usuario (cliente o comercio), quiero ver el impacto medioambiental acumulado de mis acciones en DispenSave, expresado en CO2 evitado, agua ahorrada y equivalentes en árboles plantados.*

| Criterio | Descripción |
|----------|-------------|
| CA-01 | Se muestra el total de rescates realizados |
| CA-02 | Se calcula y muestra el CO2 evitado: total_rescates × 2.5 kg |
| CA-03 | Se calcula y muestra el agua ahorrada: total_rescates × 1,000 litros |
| CA-04 | Se calcula y muestra el equivalente en árboles: total_rescates × 0.1 |
| CA-05 | Los números se animan (contador) al entrar a la pantalla |

**Valor de Negocio:** MEDIO – Genera engagement emocional y motivación para seguir usando la app.

---

**HU10 – Sistema de Niveles del Rescatista**

*Como cliente, quiero ver mi nivel actual dentro de la plataforma y cuánto me falta para subir al siguiente nivel, para sentirme motivado a realizar más rescates.*

| Criterio | Descripción |
|----------|-------------|
| CA-01 | Existen 4 niveles: Principiante Verde (0-2), Rescatista Activo (3-9), Héroe del Planeta (10-24), Leyenda DispenSave (25+) |
| CA-02 | Se muestra el nombre del nivel actual con su emoji representativo |
| CA-03 | Una barra de progreso animada indica el avance hacia el siguiente nivel |
| CA-04 | Se muestra el mensaje "Te faltan X rescates para ser [siguiente nivel]" |

**Valor de Negocio:** ALTO – La gamificación aumenta la retención y frecuencia de uso.

---

**HU11 – Sistema de Logros e Insignias**

*Como cliente, quiero desbloquear insignias al cumplir hitos de rescate, para tener un reconocimiento por mi contribución medioambiental.*

| Criterio | Descripción |
|----------|-------------|
| CA-01 | Existen al menos 6 insignias desbloqueables |
| CA-02 | Las insignias no obtenidas se muestran en gris/bloqueadas |
| CA-03 | Las insignias obtenidas se muestran a color con animación |
| CA-04 | Cada insignia tiene un nombre y criterio claro visible al tocarla |

**Insignias mínimas:**
- Primer Rescate (1 rescate), Rescatador x5 (5 rescates), Semana Activa (3 en una semana), Diversificado (3 categorías distintas), Eco Guerrero (10 kg CO2 evitado), Leyenda (25+ rescates)

**Valor de Negocio:** MEDIO – Fomenta la variedad de uso y la frecuencia.

---

**HU12 – Compartir Impacto en Redes Sociales**

*Como usuario, quiero compartir mi impacto medioambiental en mis redes sociales, para difundir la causa de DispenSave y motivar a otros.*

| Criterio | Descripción |
|----------|-------------|
| CA-01 | Un botón "Compartir mi Impacto" genera un texto con las estadísticas del usuario |
| CA-02 | Al tocar el botón, se abre el selector de apps de Android (WhatsApp, Instagram, etc.) |
| CA-03 | El texto incluye: rescates realizados, CO2 evitado y nivel actual |

**Valor de Negocio:** BAJO-MEDIO – Actúa como marketing orgánico de la plataforma.

---

### 5.2 Sprint 3 – Impacto CO2 y Gamificación

**Objetivo del Sprint:** Implementar la pantalla de Impacto CO2 completa con estadísticas calculadas, sistema de niveles, insignias animadas y función de compartir.

**Duración:** 2 semanas

**Funcionalidades a Implementar (Luis):**

| N° | Funcionalidad | Prioridad |
|----|---------------|-----------|
| 1 | Modelo `ImpactoEstadisticas` con fórmulas de cálculo | ALTA |
| 2 | Función `calcularImpacto()` en PantryViewModel | ALTA |
| 3 | Tarjetas animadas de CO2, agua, árboles y dinero ahorrado | ALTA |
| 4 | Sistema de niveles con barra de progreso animada | ALTA |
| 5 | Grilla de 6+ insignias (desbloqueadas y bloqueadas) | MEDIA |
| 6 | Vista diferenciada para comercios (ingresos e impacto del local) | MEDIA |
| 7 | Botón de compartir con Android ShareIntent | BAJA |

---

### VI. AVANCE DE IMPLEMENTACIÓN – PARTE 3 (Luis)

**Funcionalidades base entregadas por Parte 1:**

| Funcionalidad | Estado |
|---------------|--------|
| ImpactScreen (placeholder) | ✅ Disponible para reforma |
| orders StateFlow (para calcular rescates del usuario) | ✅ Disponible |
| surplusBags StateFlow (para calcular ahorros) | ✅ Disponible |
| currentUserRole para diferenciación de vistas | ✅ Disponible |

---

---

# ════════════════════════════════════════════
# PARTE 4 – ANDY
# ════════════════════════════════════════════

---

## IV. DISEÑO DE LA SOLUCIÓN (continuación)

### 4.2 Tecnologías Utilizadas – Servicios y Backend

#### Base de Datos Remota: Firebase Firestore

**Justificación:** Firebase Firestore es una base de datos NoSQL orientada a documentos, en tiempo real y serverless. Es ideal para DispenSave porque:
- Las ofertas de rescate necesitan actualizarse en tiempo real cuando el stock baja
- No requiere servidor propio, reduciendo costos de infraestructura
- Escala automáticamente según la demanda
- Las transacciones atómicas garantizan que el stock nunca quede en estado inconsistente cuando múltiples clientes reservan simultáneamente

**Estructura de colecciones en Firestore:**
```
usuarios/ {uid}
  - nombre, correo, rol, nombreLocal, categoriaLocal

locales/ {localId}
  - nombre, categoria, direccion, latitud, longitud, idUsuario

ofertas/ {ofertaId}
  - todos los campos de SurplusBag
  - itemsIncluidos: array de strings

pedidos/ {pedidoId}
  - idUsuario, idOferta, precioRescate, codigoReserva, estado

catalogoProductos/ {productoId}
  - idComercio, nombre, stock, unidad
```

#### Autenticación: Firebase Authentication

**Justificación:** Firebase Auth ofrece autenticación segura con email/password, gestión de tokens JWT automática, y manejo del ciclo de vida de sesiones. Elimina la necesidad de implementar un sistema de autenticación propio, reduciendo riesgos de seguridad.

#### Almacenamiento de Imágenes: Firebase Storage

**Justificación:** Permite subir y servir imágenes de productos y logos de locales de forma escalable con URLs públicas seguras.

#### Notificaciones Push: Firebase Cloud Messaging (FCM)

**Justificación:** FCM es el servicio de notificaciones push estándar de Android. Es gratuito, altamente confiable y se integra nativamente con Firebase. Permite enviar notificaciones a segmentos de usuarios (ej: notificar a todos los clientes que hay un nuevo pack de panadería).

#### Carga de Imágenes: Coil

**Versión:** 2.6.0

**Justificación:** Coil (Coroutine Image Loader) está diseñado específicamente para Kotlin y Jetpack Compose. Soporta caché automático de imágenes en disco y memoria, carga asíncrona con coroutines y placeholder/error images.

#### Geolocalización: Google Play Services Location

**Versión:** play-services-location 21.1.0

**Justificación:** Permite obtener la ubicación GPS real del usuario para calcular distancias precisas a los establecimientos, reemplazando las distancias hardcodeadas de la Parte 1.

---

### 5.1 Product Backlog – Historias de Usuario (HU13 a HU16)

---

**HU13 – Autenticación Real con Firebase**

*Como usuario, quiero que mi cuenta esté almacenada de forma segura en la nube para que pueda acceder desde cualquier dispositivo con mis mismas credenciales.*

| Criterio | Descripción |
|----------|-------------|
| CA-01 | El registro crea una cuenta en Firebase Authentication |
| CA-02 | Los datos del perfil se guardan en Firestore colección "usuarios" |
| CA-03 | El inicio de sesión usa el token JWT de Firebase (sesión persistente) |
| CA-04 | Al desinstalar y reinstalar la app, el usuario puede volver a iniciar sesión con su correo y contraseña |
| CA-05 | La interfaz no cambia: mismo flow de pantallas definido en Parte 1 |

**Valor de Negocio:** MUY ALTO – Requisito crítico para producción real.

---

**HU14 – Publicación de Ofertas en Tiempo Real**

*Como comercio, quiero que mis ofertas publicadas aparezcan en tiempo real para todos los clientes de la plataforma, y que el stock se actualice automáticamente cuando alguien reserva.*

| Criterio | Descripción |
|----------|-------------|
| CA-01 | Al publicar una oferta, se crea un documento en Firestore colección "ofertas" |
| CA-02 | Los clientes ven la nueva oferta sin necesidad de refrescar manualmente |
| CA-03 | Al reservar, se usa una transacción atómica de Firestore para decrementar el stock |
| CA-04 | Si el stock llega a 0, la oferta se marca automáticamente como "Agotada" |

**Valor de Negocio:** MUY ALTO – Garantiza consistencia de datos entre múltiples usuarios.

---

**HU15 – Notificaciones Push**

*Como cliente, quiero recibir notificaciones cuando haya nuevos packs disponibles o cuando se acerque mi franja horaria de recojo, para no perder rescates ni llegar tarde.*

| Criterio | Descripción |
|----------|-------------|
| CA-01 | El dispositivo recibe notificaciones push de Firebase Cloud Messaging |
| CA-02 | Al publicar un pack, se envía una notificación a los clientes de esa categoría |
| CA-03 | 30 minutos antes de la franja horaria de recojo, el cliente recibe un recordatorio |
| CA-04 | El comercio recibe una notificación cuando un cliente reserva en su local |

**Valor de Negocio:** ALTO – Las notificaciones aumentan significativamente la tasa de uso activo.

---

**HU16 – Modo Sin Conexión**

*Como usuario, quiero poder ver mis datos recientes aunque no tenga internet, para no quedarme sin información si la conexión falla.*

| Criterio | Descripción |
|----------|-------------|
| CA-01 | Las ofertas cargadas previamente se guardan en caché local con Room |
| CA-02 | Si no hay conexión, se muestran los datos del caché con un banner "Sin conexión" |
| CA-03 | Cuando se recupera la conexión, los datos se sincronizan automáticamente |
| CA-04 | Los pedidos realizados sin conexión se sincronizan al recuperar internet |

**Valor de Negocio:** MEDIO – Mejora la experiencia en zonas con conectividad intermitente.

---

### 5.2 Sprint 4 – Backend, API y Persistencia Real

**Objetivo del Sprint:** Reemplazar todos los repositorios en memoria por servicios reales de Firebase, implementar notificaciones push, geolocalización real y modo sin conexión con caché Room.

**Duración:** 2 semanas

**Funcionalidades a Implementar (Andy):**

| N° | Funcionalidad | Prioridad |
|----|---------------|-----------|
| 1 | Configurar Firebase en el proyecto (google-services.json) | CRÍTICA |
| 2 | Reemplazar UserRepository con Firebase Auth + Firestore | CRÍTICA |
| 3 | Reemplazar RescueRepository con Firestore (tiempo real) | CRÍTICA |
| 4 | Subida de imágenes de productos a Firebase Storage | ALTA |
| 5 | Implementar FCM con ServicioNotificaciones.kt | ALTA |
| 6 | Geolocalización real con FusedLocationProviderClient | MEDIA |
| 7 | Caché offline con Room (OfertaEntity, PedidoEntity) | MEDIA |
| 8 | Carga de imágenes remotas con Coil en ExploreScreen | ALTA |

---

### VI. AVANCE DE IMPLEMENTACIÓN – PARTE 4 (Andy)

#### VI.4 Funcionalidades Mínimas Obligatorias – Parte 4

| Requisito | Implementación |
|-----------|----------------|
| Consumo de servicio REST/API | Firebase Firestore SDK (API oficial de Google) |
| Persistencia de datos | Firebase Firestore (remoto) + Room Database (local/caché) |
| Navegación entre pantallas | Heredada de Parte 1, sin modificaciones |
| Operaciones CRUD | CRUD completo en Firestore para usuarios, ofertas y pedidos |
| Validación de formularios | Heredada de Parte 1 (ValidationUtils) |
| Manejo de errores | Try-catch en todas las llamadas a Firebase con mensajes en pantalla |

#### VI.5 Estructura de Datos en Firebase Firestore

```
Colección: usuarios
  Documento: {uid}
    nombre: "Maykel Bernaola"
    correo: "maykel@universidad.edu.pe"
    rol: "CLIENTE"
    fechaRegistro: 1729385234000

Colección: ofertas
  Documento: {auto-id}
    idLocal: "labaguette"
    categoria: "PANADERIA"
    tipoOferta: "PACK"
    titulo: "Pack del Día: Panadería Variada"
    itemsIncluidos: ["Pan Ciabatta x6", "Croissant x2", "Empanada de carne"]
    precioOriginal: 30.00
    precioRescate: 9.90
    franjaHoraria: "20:00 - 21:00"
    stockDisponible: 3

Colección: pedidos
  Documento: {auto-id}
    idUsuario: {uid_cliente}
    idOferta: {id_oferta}
    codigoReserva: "DS-7429"
    estado: "PENDIENTE_RECOJO"
    timestamp: 1729388834000
```

---

## VI. EVIDENCIAS GENERALES DEL PROYECTO

### Repositorio GitHub
- **URL:** https://github.com/maykelcb/DispenSave
- **Rama principal:** `main`
- **Ramas de colaboradores:**
  - `feature/parte2-ruben` (Rubén)
  - `feature/parte3-luis` (Luis)
  - `feature/parte4-andy` (Andy)

### Historial de Commits (Parte 1 – Maykel)
Los commits del proyecto están disponibles en el historial de GitHub e incluyen:
- Implementación de modelos de dominio
- Sistema de validaciones
- Repositorios de datos
- Pantallas de autenticación y panel de comercio
- Catálogo de productos con control de stock
- Sistema de publicación de ofertas

### Restricciones Cumplidas

| Restricción | Estado |
|-------------|--------|
| No solo interfaces estáticas | ✅ App funcional con navegación, lógica y estado reactivo |
| Con persistencia de datos | ✅ SharedPreferences + Room configurado |
| Evidencias de trabajo colaborativo | ✅ 4 ramas por integrante en GitHub |
| Historial de versiones | ✅ Múltiples commits en GitHub con mensajes descriptivos |

---

*DispenSave – Documento de Proyecto de Desarrollo de Software*
*Secciones IV, V y VI distribuidas entre los 4 integrantes del equipo*
