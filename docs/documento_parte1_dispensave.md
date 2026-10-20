# INFORME DE PROYECTO DE DESARROLLO DE SOFTWARE
# DISPENSAVE – PLATAFORMA DE RESCATE DE COMIDA

---

## DATOS GENERALES DEL PROYECTO
- **Proyecto:** DispenSave (Plataforma Móvil de Rescate de Excedentes Gastronómicos)
- **Líder Técnico e Integrador:** Maykel Bernaola
- **Equipo de Trabajo:**
  - **Parte 1 (Líder Técnico):** Maykel Bernaola
  - **Parte 2 (Diseño UI/UX e Interacción):** Rubén
  - **Parte 3 (Persistencia y Métricas Ecológicas):** Luis
  - **Parte 4 (Servicios en la Nube y APIs):** Andy
- **Repositorio Oficial en GitHub:** https://github.com/maykelcb/DispenSave

---

# PARTE 1: ARQUITECTURA BASE, GESTIÓN DE ROLES, CATÁLOGO Y PUBLICACIÓN DE EXCEDENTES
**Responsable:** Maykel Bernaola

---

## IV. DISEÑO DE LA SOLUCIÓN

### 4.1 Arquitectura de la Aplicación

#### 4.1.1 Enfoque Arquitectónico Seleccionado
Para el desarrollo de la aplicación móvil **DispenSave** se ha seleccionado el patrón arquitectónico **MVVM (Modelo - Vista - Vista-Modelo)** combinado con principios de separación de responsabilidades (**Arquitectura Limpia / Clean Architecture**), siguiendo la guía oficial de buenas prácticas de desarrollo móvil recomendada por Google para Android moderno con **Jetpack Compose**.

DispenSave es una solución tecnológica orientada a la reducción del desperdicio de alimentos (*Food Waste Reduction*), articulando dos tipos de actores con necesidades diferenciadas:
1. **Comercios Gastronómicos (Restaurantes, Panaderías, Cafeterías, Pastelerías):** Disponen de un panel especializado para gestionar su propio catálogo de productos, controlar su inventario en tiempo real y publicar ofertas de rescate (tanto "Packs de Excedentes" como "Productos Individuales") con descuentos de hasta el 70%.
2. **Clientes / Consumidores:** Acceden a un mercado interactivo para descubrir ofertas cercanas, filtrar por tipo de establecimiento, reservar alimentos frescos y monitorear su ahorro financiero y aporte ecológico (CO₂ evitado).

---

#### 4.1.2 Diagrama de Arquitectura del Sistema

El siguiente diagrama ilustra las capas de la aplicación, sus responsabilidades y las vías de comunicación unidireccional:

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│                             CAPA DE PRESENTACIÓN                                 │
│                     (Vistas e Interfaz con Jetpack Compose)                      │
│                                                                                  │
│   [Actividad Principal: MainActivity]                                            │
│   [Gestor de Navegación Contextual según Rol: AppNavigation]                     │
│                                                                                  │
│   VISTAS DE AUTENTICACIÓN:                                                       │
│   • Pantalla de Bienvenida (OnboardingScreen)                                    │
│   • Pantalla de Registro con Selector de Rol y Tipo (RegisterScreen)             │
│   • Pantalla de Inicio de Sesión Seguro (LoginScreen)                            │
│                                                                                  │
│   VISTAS DEL CLIENTE:              VISTAS DEL COMERCIO:                          │
│   • Explorar Ofertas               • Panel de Control del Local                  │
│   • Mis Rescates (Pedidos)         • Mi Catálogo e Inventario                    │
│   • Impacto Ecológico              • Publicar Oferta (Pack o Individual)         │
│                                                                                  │
│                           │                             ▲                        │
│                   Eventos de Usuario              Emisión de Estado              │
│                  (Clics, Formularios)          (Actualización Reactiva)          │
│                           ▼                             │                        │
└───────────────────────────┼─────────────────────────────┼────────────────────────┘
                            │                             │
┌───────────────────────────▼─────────────────────────────┴────────────────────────┐
│                   CAPA DE LÓGICA DE NEGOCIO Y ESTADO                             │
│                     (Contenedor de Estado / ViewModel)                           │
│                                                                                  │
│   [Controlador de Estado: PantryViewModel]                                       │
│   • Mantiene el estado mutable observable (Flujos de Estado / StateFlow)         │
│   • Administra la sesión y el rol activo (Cliente vs Comercio)                   │
│   • Valida y procesa la publicación de packs y productos individuales            │
│   • Gestiona las altas, bajas y stock en tiempo real del catálogo propio         │
│   • Aplica reglas de validación lógica mediante ValidationUtils                  │
└───────────────────────────┬──────────────────────────────────────────────────────┘
                            │
┌───────────────────────────▼──────────────────────────────────────────────────────┐
│                            CAPA DE DOMINIO                                       │
│                    (Modelos y Entidades del Negocio)                             │
│                                                                                  │
│   • Usuario (User: Cliente / Comercio con categoría comercial)                   │
│   • Oferta de Rescate (SurplusBag: Pack vs Individual, precios y horarios)       │
│   • Producto del Catálogo (MerchantProduct: nombre, unidad, stock)               │
│   • Categoría de Comercio (StoreCategory: Panadería, Cafetería, etc.)           │
│   • Pedido de Rescate (Order: código DS-XXXX, estado, fecha)                     │
└───────────────────────────┬──────────────────────────────────────────────────────┘
                            │
┌───────────────────────────▼──────────────────────────────────────────────────────┐
│                             CAPA DE DATOS                                        │
│                   (Repositorios y Fuentes de Persistencia)                       │
│                                                                                  │
│   [Repositorio de Usuarios: UserRepository]                                      │
│   • Persistencia local de cuentas con SharedPreferences                          │
│                                                                                  │
│   [Repositorio de Rescates y Catálogo: RescueRepository]                         │
│   • Gestión reactiva de ofertas activas y catálogo en memoria                    │
│                                                                                  │
│   [Base de Datos Local SQLite: Room / AppDatabase]                               │
│   • Almacenamiento estructurado de productos locales                             │
└──────────────────────────────────────────────────────────────────────────────────┘
```

---

#### 4.1.3 Componentes Principales del Sistema

1. **Capa de Presentación (Interfaz de Usuario):**
   - **`MainActivity.kt`:** Actividad raíz y única de la aplicación (*Single-Activity Architecture*). Inicializa el tema visual global basado en Material Design 3, inyecta los repositorios y aloja el grafo de navegación.
   - **`AppNavigation.kt`:** Administrador central del grafo de navegación. Detecta en tiempo real el tipo de rol (`CLIENTE` o `COMERCIO`) del usuario autenticado y despliega la barra de navegación contextual correspondiente:
     - *Barra de Cliente:* Pestañas de **Explorar**, **Mis Rescates** e **Impacto CO₂**.
     - *Barra de Comercio:* Pestañas de **Mi Local**, **Mis Pedidos** e **Impacto CO₂**.
   - **`RegisterScreen.kt`:** Pantalla de registro de usuarios con selección dinámica de rol. Si el usuario elige "Comercio", se despliega obligatoriamente el selector de tipo de establecimiento comercial (*Panadería, Cafetería, Restaurante, Pastelería u Otro*) y el nombre del local. Integra validaciones estrictas de datos.
   - **`LoginScreen.kt`:** Control de acceso con verificación en el repositorio de cuentas, validación de contraseñas y redirección automática hacia la pantalla principal respectiva según el rol.
   - **`StoreDashboardScreen.kt`:** Panel integral del comercio gastronómico que incluye:
     - Métricas en tiempo real (packs activos, pedidos pendientes, total rescatado).
     - Pestaña **"Mi Catálogo"**: inventario libre donde el comerciante escribe el nombre de cualquier producto propio, define su unidad (unidad, kg, porción) y ajusta el stock instantáneamente con botones `+` y `−`.
     - Pestaña **"Ofertas Publicadas"**: lista de ofertas vigentes con estado y stock restante.
     - Modal **"Publicar Oferta"**: selector entre "Pack de Excedentes" o "Producto Individual", vinculación de productos del catálogo propio, validaciones numéricas estrictas en precios y franja horaria.
   - **`ExploreScreen.kt`:** Mercado para consumidores donde se listan las ofertas activas con visualización simultánea del precio original tachado, el precio de rescate rebajado, el cálculo automático de porcentaje de ahorro y el stock disponible.

2. **Capa de Lógica de Negocio y Estado (ViewModel):**
   - **`PantryViewModel.kt`:** Componente que desacopla la lógica de negocio de la interfaz visual. Conserva su estado ante rotaciones de pantalla y emite datos mediante flujos inmutables (`StateFlow`). Se encarga de:
     - Autenticar y registrar usuarios aplicando validaciones de seguridad.
     - Publicar ofertas comprobando que los campos numéricos contengan valores válidos mayores a cero y que la franja horaria posea el formato correcto.
     - Mantener el catálogo del comercio permitiendo altas libres de productos y control de stock incremental/decremental.

3. **Capa de Dominio (Modelos del Negocio):**
   - **`User.kt`:** Modela el perfil de usuario, definiendo los roles (`CLIENTE`, `COMERCIO`), el tipo de comercio asociado (`StoreCategory`) y los datos de contacto.
   - **`SurplusBag.kt`:** Representa la oferta de rescate, soportando dos modalidades: `PACK` (conjunto de productos variados) o `INDIVIDUAL` (un solo ítem específico). Calcula el porcentaje de descuento y almacena el precio original y de rescate.
   - **`MerchantProduct.kt`:** Modela un producto registrado en el catálogo particular del comercio con su nombre, unidad de medida y cantidad en stock.
   - **`Order.kt`:** Estructura el pedido de rescate con su identificador alfanumérico único (`DS-XXXX`), hora de registro y estado del recojo.

4. **Capa de Datos (Repositorios y Persistencia):**
   - **`UserRepository.kt`:** Implementa la persistencia local de credenciales de acceso y perfiles registrados en el dispositivo mediante almacenamiento clave-valor (`SharedPreferences`), asegurando que las sesiones se mantengan al cerrar la aplicación.
   - **`RescueRepository.kt`:** Repositorio reactivo que gestiona en memoria el listado de ofertas de rescate activas, la lista de pedidos en curso y el inventario de productos por comercio.

---

#### 4.1.4 Flujo General de Información (Flujo Unidireccional de Datos - UDF)

El flujo de información en DispenSave garantiza consistencia y predecibilidad en el estado de la aplicación mediante 5 fases cíclicas:

1. **Disparo de Evento de Usuario:** El usuario interactúa con la interfaz gráfica (por ejemplo, el comerciante presiona *"Publicar Oferta"* tras ingresar precios y seleccionar productos de su catálogo).
2. **Delegación a la Lógica de Negocio:** La vista Compose despacha la solicitud al `PantryViewModel`, ejecutando la función `publishSurplusBag()`.
3. **Validación y Reglas de Negocio:** El ViewModel invoca los métodos de `ValidationUtils` para corroborar que los precios sean estrictamente numéricos positivos, que el precio de rescate sea inferior al precio original y que la franja horaria cumpla el patrón requerido (`HH:mm - HH:mm`).
4. **Actualización en la Capa de Datos:** Si las validaciones son conformes, el ViewModel invoca al repositorio (`RescueRepository`), el cual crea la nueva oferta, descuenta el stock respectivo del catálogo y emite la nueva lista a través de un flujo observable (`StateFlow`).
5. **Recomposición Visual Reactiva:** La interfaz gráfica (tanto en el panel del comercio como en la vista del cliente) observa el cambio de estado y actualiza de manera inmediata los elementos visuales sin necesidad de recargar manualmente la pantalla.

---

#### 4.1.5 Justificación de la Arquitectura Elegida

| Criterio | Justificación Técnica de MVVM en DispenSave |
| :--- | :--- |
| **Separación de Responsabilidades** | El código de la interfaz (Compose) está completamente aislado de las reglas de cálculo de precios, validaciones numéricas y almacenamiento de datos, evitando archivos extensos y sobrecargados (*God Classes*). |
| **Persistencia ante Cambios de Configuración** | `PantryViewModel` sobrevive al ciclo de vida de la actividad; si el usuario rota la pantalla o minimiza la aplicación mientras publica una oferta, los datos no se pierden. |
| **Reactividad Declarativa con Jetpack Compose** | Al utilizar flujos observables (`StateFlow`), Compose recompone únicamente aquellos componentes cuyos datos cambiaron (por ejemplo, solo la tarjeta del stock modificado), optimizando el rendimiento de renderizado en el dispositivo móvil. |
| **Facilidad para Pruebas y Escalabilidad** | Las entidades de dominio (`MerchantProduct`, `SurplusBag`, `User`) y las funciones de validación (`ValidationUtils`) son código Kotlin puro sin dependencias de librerías de Android, lo que permite ejecutar pruebas unitarias automáticas de forma instantánea. |
| **Trabajo en Equipo Modular** | Facilita la división en 4 partes independientes: mientras la Parte 1 establece la arquitectura base, catálogo y publicación, los demás integrantes pueden implementar la interfaz de usuario avanzada (Parte 2), la persistencia en base de datos (Parte 3) y los servicios cloud (Parte 4) sin interferencias. |

---

## V. PLANIFICACIÓN DEL DESARROLLO

### 5.1 Historias de Usuario (Parte 1: Maykel Bernaola)

A continuación se presentan las 4 Historias de Usuario desarrolladas e implementadas en la Parte 1 del proyecto:

| ID | Historia de Usuario | Criterios de Aceptación (Escenarios Estructurados) | Valor de Negocio | Prioridad / Puntos |
| :---: | :--- | :--- | :--- | :---: |
| **HU-01** | **Registro de Usuarios con Selección de Rol y Tipo de Establecimiento**<br><br>*Como* nuevo usuario de la plataforma (consumidor o propietario gastronómico),<br>*quiero* registrarme seleccionando mi rol (Cliente o Comercio) y especificar el tipo de negocio (Panadería, Cafetería, Restaurante, Pastelería),<br>*para* acceder a las funcionalidades y vistas diseñadas específicamente para mi perfil. | **Escenario 1: Registro exitoso como Cliente**<br>• **Dado que** el usuario está en la pantalla de registro y selecciona la opción "Soy Cliente",<br>• **Cuando** completa su nombre completo (mínimo 3 caracteres, sin repeticiones continuas de letras), correo electrónico válido y contraseña segura, y marca la aceptación de términos,<br>• **Entonces** el sistema guarda la cuenta y lo redirige automáticamente a la pantalla de exploración de ofertas (`ExploreScreen`).<br><br>**Escenario 2: Registro exitoso como Comercio**<br>• **Dado que** el usuario selecciona la opción "Soy Comercio",<br>• **Cuando** ingresa el nombre de su establecimiento y selecciona su tipo de negocio del menú desplegable (*Panadería, Cafetería, Restaurante o Pastelería*),<br>• **Entonces** la cuenta se crea con perfil comercial y se redirige al panel de administración del local (`StoreDashboardScreen`).<br><br>**Escenario 3: Rechazo por datos inválidos o repetidos**<br>• **Dado que** el usuario ingresa caracteres de prueba inválidos (ej. *"aaaa"*, números en el nombre o formato de correo incorrecto),<br>• **Cuando** presiona "Registrarse",<br>• **Entonces** el sistema bloquea el registro y muestra un mensaje de error descriptivo en color rojo bajo el campo correspondiente. | **Muy Alto:** Base fundamental de la plataforma; garantiza la correcta segmentación de usuarios y la seguridad en el acceso al sistema. | **Alta**<br>(5 SP) |
| **HU-02** | **Navegación Dinámica Contextual según el Rol de Usuario**<br><br>*Como* usuario autenticado en DispenSave,<br>*quiero* que la barra de navegación inferior y las opciones del sistema se adapten dinámicamente a mi rol activo,<br>*para* navegar exclusivamente por las herramientas pertinentes a mis necesidades sin confusiones. | **Escenario 1: Navegación inferior para el Cliente**<br>• **Dado que** un usuario inicia sesión con el rol de `CLIENTE`,<br>• **Cuando** carga la pantalla principal de la aplicación,<br>• **Entonces** la barra inferior muestra únicamente las pestañas: **Explorar**, **Mis Rescates** e **Impacto CO₂**.<br><br>**Escenario 2: Navegación inferior para el Comercio**<br>• **Dado que** un usuario inicia sesión con el rol de `COMERCIO`,<br>• **Cuando** carga la pantalla principal de la aplicación,<br>• **Entonces** la barra inferior muestra dinámicamente las pestañas: **Mi Local** (gestión del comercio), **Mis Pedidos** (entregas) e **Impacto CO₂**.<br><br>**Escenario 3: Persistencia de sesión**<br>• **Dado que** el usuario cierra y vuelve a abrir la aplicación,<br>• **Cuando** la app inicia,<br>• **Entonces** se reconoce la sesión previamente guardada y se carga el entorno correspondiente sin requerir nuevo inicio de sesión. | **Alto:** Ofrece una experiencia de usuario (UX) limpia, coherente e intuitiva, reduciendo la fricción en el uso diario. | **Media**<br>(3 SP) |
| **HU-03** | **Gestión de Catálogo Propio e Inventario de Productos del Comercio**<br><br>*Como* propietario o administrador de un establecimiento comercial,<br>*quiero* registrar libremente los productos que elaboro o vendo y ajustar su stock diario,<br>*para* disponer de un inventario actualizado que me permita armar packs o vender excedentes rápidamente. | **Escenario 1: Registro libre de un nuevo producto en el catálogo**<br>• **Dado que** el comercio está en la pestaña "Mi Catálogo" dentro del panel de control,<br>• **Cuando** presiona el botón "Agregar Producto", escribe libremente el nombre (ej. *"Pan ciabatta artesanal"*), selecciona la unidad (unidades, kg, porciones) e indica el stock inicial,<br>• **Entonces** el producto queda guardado en la lista del catálogo comercial con sus datos correspondientes.<br><br>**Escenario 2: Ajuste rápido de stock en tiempo real**<br>• **Dado que** el comerciante observa su listado de productos registrados,<br>• **Cuando** presiona los botones interactivos `+` o `−`,<br>• **Entonces** la cantidad disponible se incrementa o decrementa de manera instantánea, reflejándose en el contador de la pantalla sin permitir valores negativos.<br><br>**Escenario 3: Validación de campos obligatorios**<br>• **Dado que** el comerciante intenta agregar un producto dejando el nombre en blanco o con caracteres inválidos,<br>• **Cuando** presiona "Guardar",<br>• **Entonces** el formulario no se envía y solicita un nombre de producto válido. | **Muy Alto:** Permite a cualquier tipo de negocio gastronómico adaptar la aplicación a su menú real sin depender de listas cerradas o predeterminadas. | **Alta**<br>(5 SP) |
| **HU-04** | **Publicación de Ofertas de Rescate (Pack o Individual) con Validaciones Numéricas**<br><br>*Como* comerciante gastronómico,<br>*quiero* publicar excedentes del día seleccionando si es un "Pack de Excedentes" o un "Producto Individual", ingresando precios y horario de recojo estrictamente validados,<br>*para* poner a disposición de los clientes ofertas atractivas con su descuento visible antes del cierre del local. | **Escenario 1: Publicación de un Pack con productos del catálogo**<br>• **Dado que** el comercio selecciona la opción "Pack de Excedentes",<br>• **Cuando** elige los productos incluidos desde su catálogo, digita el precio original (ej. 30.00), el precio de rescate (ej. 9.90), el stock de packs y la franja horaria (ej. 20:00 - 21:00),<br>• **Entonces** el sistema valida que todos los campos sean números válidos, calcula el porcentaje de ahorro (~67% de descuento) y publica la oferta en el mercado.<br><br>**Escenario 2: Publicación de un Producto Individual**<br>• **Dado que** el comercio selecciona la opción "Producto Individual",<br>• **Cuando** selecciona un único producto de su catálogo e ingresa precio original, precio rebajado y horario,<br>• **Entonces** la oferta se publica detallando el nombre específico del producto sin la denominación de pack.<br><br>**Escenario 3: Bloqueo de caracteres no numéricos y horarios erróneos**<br>• **Dado que** el usuario intenta escribir letras o símbolos en los campos de precio o stock,<br>• **Cuando** el usuario teclea en dichos campos,<br>• **Entonces** el sistema solo acepta dígitos numéricos y punto decimal; si la franja horaria no cumple el formato o el precio de rescate es mayor al original, se notifica el error y se bloquea la publicación. | **Muy Alto:** Núcleo transaccional de la solución; garantiza integridad de los precios, horarios de recojo e inventario disponible. | **Alta**<br>(5 SP) |

---

### 5.2 Sprint de Desarrollo (Sprint 1)

- **Objetivo del Sprint (Sprint Goal):** Diseñar y construir la base arquitectónica móvil de DispenSave con soporte para múltiples roles de usuario (Cliente y Comercio), implementar el flujo de bienvenida, registro y acceso seguro con validaciones estrictas, crear el catálogo de productos con ajuste de stock en tiempo real y habilitar el módulo de publicación de excedentes gastronómicos (packs e individuales).
- **Duración:** 1 semana.
- **Funcionalidades del Sprint:**
  1. Configuración de la arquitectura MVVM sobre Jetpack Compose y Kotlin Coroutines.
  2. Sistema de autenticación y registro con discriminación por rol (`CLIENTE` vs `COMERCIO`) y tipo de negocio.
  3. Navegación dinámica desacoplada con barra inferior contextual según el perfil activo.
  4. Módulo de catálogo propio del comercio con control de stock incremental/decremental.
  5. Módulo de publicación de ofertas con soporte para Packs y Productos Individuales, cálculo automático de descuento y validaciones numéricas estrictas.
- **Actividades Ejecutadas:**
  1. Creación del proyecto base en Android Studio con Jetpack Compose BOM y Material 3.
  2. Implementación de las entidades de dominio: `User`, `SurplusBag`, `MerchantProduct`, `StoreCategory` y `Order`.
  3. Desarrollo del repositorio local `UserRepository` para persistencia en `SharedPreferences` y `RescueRepository` para flujos reactivos.
  4. Diseño e implementación de `ValidationUtils.kt` con validaciones de expresiones regulares (bloqueo de nombres ficticios con repetición de letras, correos inválidos y contraseñas débiles).
  5. Construcción de las pantallas `OnboardingScreen`, `RegisterScreen`, `LoginScreen` y `StoreDashboardScreen`.
  6. Modificación y enriquecimiento de `PantryViewModel` para gestionar el inventario del comercio y las ofertas publicadas.

---

### 5.3 Metodología de Evidencias de Seguimiento

Para el seguimiento y control ágil del desarrollo durante el Sprint 1, se adoptó la metodología **Scrum / Kanban** gestionada a través de un tablero de trabajo digital (utilizando **GitHub Projects** o **Trello**):

#### A. Estructura del Tablero Kanban de Trabajo
El tablero se organiza en cuatro columnas de flujo de valor:
1. **Product Backlog (Pila de Producto):** Lista priorizada de todas las Historias de Usuario (HU-01 a HU-04) con sus respectivos Criterios de Aceptación y Puntos de Historia (Story Points).
2. **En Progreso (In Progress):** Tareas técnicas y componentes que se encuentran en fase de codificación activa.
3. **En Pruebas / Revisión (Testing / Code Review):** Módulos finalizados que están siendo sometidos a pruebas funcionales en emulador y validación de criterios de aceptación.
4. **Terminado (Done):** Funcionalidades que cumplen con la Definición de Terminado (*Definition of Done - DoD*): código limpio, validaciones superadas, compilación exitosa sin errores y commit subido al repositorio en GitHub.

#### B. Cómo Presentar la Evidencia de Seguimiento en el Informe
Para documentar esta sección en el informe impreso o digital, se debe incluir:
1. **Captura del Tablero Kanban:** Imagen del tablero de GitHub Projects o Trello donde se aprecien las tarjetas de las Historias de Usuario (HU-01 a HU-04) distribuidas en las columnas de avance.
2. **Registro de Tareas por Historia de Usuario:** Tabla que relacione cada HU con sus subtareas técnicas completadas (ej. creación de composables, pruebas de validación, integración con ViewModel).
3. **Control de Commits en GitHub:** Captura del historial de commits en la rama principal (`main`) con mensajes claros que evidencien el avance progresivo de cada funcionalidad.

---

## VI. EVIDENCIAS DE IMPLEMENTACIÓN (PARTE 1)

### 6.1 Estructura de Paquetes en el Repositorio

La arquitectura del proyecto se refleja en una organización de paquetes modular, limpia y coherente dentro del directorio `app/src/main/java/com/example/dispensave/`:

```text
com.example.dispensave/
│
├── MainActivity.kt                      # Punto de entrada de la app y configuración de tema
│
├── data/                                # Capa de Datos y Persistencia
│   ├── local/                           # Base de datos local SQLite (Room)
│   │   ├── AppDatabase.kt               # Instancia central de Room Database
│   │   ├── ProductDao.kt                # Objeto de acceso a datos de productos
│   │   └── ProductEntity.kt             # Entidad de persistencia en SQLite
│   │
│   └── repository/                      # Repositorios de datos
│       ├── PantryRepository.kt          # Repositorio de despensa general
│       ├── RescueRepository.kt          # Repositorio reactivo de ofertas y pedidos
│       └── UserRepository.kt            # Gestión y persistencia de cuentas y sesiones
│
├── domain/                              # Capa de Dominio (Modelos puros de negocio)
│   └── model/
│       ├── CategoryCatalog.kt           # Catálogos sugeridos por categoría comercial
│       ├── MerchantProduct.kt           # Modelo de producto propio del comercio (stock libre)
│       ├── Order.kt                     # Modelo de pedido de rescate y código DS-XXXX
│       ├── Product.kt                   # Modelo genérico de producto
│       ├── SurplusBag.kt                # Oferta de rescate (Packs y Productos Individuales)
│       └── User.kt                      # Entidad de Usuario, roles (CLIENTE/COMERCIO) y categorías
│
├── ui/                                  # Capa de Presentación (Jetpack Compose UI)
│   ├── auth/                            # Módulo de Autenticación
│   │   ├── LoginScreen.kt               # Pantalla de inicio de sesión seguro
│   │   └── RegisterScreen.kt            # Pantalla de registro con selector de rol y tipo de local
│   ├── home/                            # Módulo de Exploración para Clientes
│   │   ├── ExploreScreen.kt             # Mercado de rescates con cálculo de descuentos y filtros
│   │   └── HomeScreen.kt                # Vista de inicio complementaria
│   ├── merchant/                        # Módulo del Comercio Gastronómico
│   │   └── StoreDashboardScreen.kt      # Panel de comercio, "Mi Catálogo" y publicar oferta
│   ├── navigation/                      # Navegación
│   │   └── AppNavigation.kt             # Grafo NavHost y barra inferior dinámica por rol
│   ├── onboarding/                      # Inducción
│   │   └── OnboardingScreen.kt          # Pantalla de bienvenida con propuesta de valor
│   ├── orders/                          # Módulo de Pedidos
│   │   └── MyOrdersScreen.kt            # Lista de rescates activos y confirmados
│   ├── impact/                          # Módulo Ecológico
│   │   └── ImpactScreen.kt              # Pantalla de métricas de impacto ambiental y CO₂
│   ├── pantry/                          # Gestión de Estado
│   │   ├── AddProductDialog.kt          # Diálogo de inserción de productos
│   │   ├── PantryScreen.kt              # Pantalla de despensa
│   │   └── PantryViewModel.kt           # ViewModel central de lógica de negocio y flujos reactivos
│   └── theme / theme2/                  # Sistema de Diseño y Estilos
│       ├── Color.kt                     # Paleta de colores ecológicos (Verde #2E7D32)
│       ├── Theme.kt                     # Definición de MaterialTheme
│       └── Type.kt                      # Tipografías oficiales
│
└── util/                                # Utilidades transversales
    └── ValidationUtils.kt               # Validaciones estrictas de texto, números y correos
```

---

### 6.2 Evidencia de Compilación Exitosa

El proyecto compila sin errores ni advertencias bloqueantes utilizando el compilador oficial de Gradle con Kotlin 1.9+ y Android Gradle Plugin:

```text
> Task :app:compileDebugKotlin UP-TO-DATE
> Task :app:compileDebugJavaWithJavac UP-TO-DATE
> Task :app:processDebugResources UP-TO-DATE
> Task :app:packageDebug UP-TO-DATE
> Task :app:assembleDebug UP-TO-DATE

BUILD SUCCESSFUL in 678ms
Configuration cache entry reused.
```

---

### 6.3 Guía Paso a Paso para Obtener las Capturas de Evidencia en Android Studio

Para insertar las imágenes oficiales en el documento de Word final, sigue estos sencillos pasos:

#### A. Captura de la Estructura de Paquetes del Proyecto
1. **Abrir Android Studio:** Abre el proyecto ubicado en `C:\Users\Maykel\AndroidStudioProjects\DispenSave`.
2. **Configurar la Vista del Proyecto:**
   - En el panel lateral izquierdo, ubica el selector desplegable superior (que usualmente dice *Android*) y cámbialo a la vista **Project** (o mantén la vista **Android** expandida).
   - Despliega la siguiente ruta haciendo clic en las flechas:  
     `app` ➔ `src` ➔ `main` ➔ `java` ➔ `com.example.dispensave`.
   - Despliega las subcarpetas: `data` (con `local` y `repository`), `domain` (`model`), `ui` (con `auth`, `home`, `merchant`, `navigation`, `pantry`) y `util`.
3. **Tomar la Captura:**
   - Presiona en tu teclado el atajo de Windows: **`Win + Shift + S`** (Herramienta Recortes).
   - Selecciona el recuadro que cubra el panel lateral izquierdo con todo el árbol de paquetes desplegado.
   - Pega la imagen directamente en Microsoft Word (`Ctrl + V`) debajo de la sección *6.1 Estructura de Paquetes en el Repositorio*.

#### B. Captura de la Evidencia de Compilación Exitosa (`BUILD SUCCESSFUL`)
1. **Abrir la Terminal integrada en Android Studio:**
   - En la parte inferior de Android Studio, haz clic en la pestaña **Terminal** (o presiona `Alt + F12`).
2. **Ejecutar el comando de compilación:**
   - Escribe el comando:
     ```bash
     gradlew.bat assembleDebug
     ```
     *(o si usas PowerShell: `./gradlew assembleDebug`)* y presiona `Enter`.
   - Espera a que finalice la ejecución de tareas. En pantalla aparecerá el mensaje en color verde o blanco:  
     `BUILD SUCCESSFUL in ...`
3. **Tomar la Captura:**
   - Presiona **`Win + Shift + S`**.
   - Recorta el área inferior de la terminal donde se visualiza el comando ejecutado y la línea final de `BUILD SUCCESSFUL`.
   - Pégala en el documento de Word en la sección *6.2 Evidencia de Compilación Exitosa*.

#### C. Capturas de Pantalla de la Aplicación en Ejecución (Emulador o Celular Físico)
Para respaldar el funcionamiento real de las pantallas desarrolladas en la Parte 1, se recomienda incluir capturas de:
1. **Pantalla de Registro:** Mostrando el selector de "Soy Comercio" con la lista desplegable de categorías comerciales (*Panadería, Cafetería, etc.*).
2. **Panel de Control del Comercio:** Mostrando la pestaña *"Mi Catálogo"* con los productos creados libremente y sus botones `+` y `−` de control de stock.
3. **Modal de Publicación de Oferta:** Mostrando la selección entre *Pack de Excedentes* o *Producto Individual* con los campos numéricos de precios y horario debidamente llenados.
4. **Pantalla de Exploración del Cliente:** Mostrando las ofertas publicadas con el precio original tachado, el precio de rescate y la etiqueta de descuento calculada.

*(Para capturar directamente desde el emulador de Android Studio, haz clic en el ícono de la **Cámara de Fotos** ubicado en la barra de herramientas lateral del emulador).*
