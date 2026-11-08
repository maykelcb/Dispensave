# GUÍA Y PLANTILLA OFICIAL — EVALUACIÓN FINAL
## DOCUMENTO DE ENTREGA: PARTE 4
### Responsable Asignado: Integrante 4 (Anderson Abanto / Andy — Publicación, ODS 12, Conclusiones y Manual)
### Rama Oficial de GitHub: `feature/shopping-list-alerts`
### Repositorio: [https://github.com/maykelcb/DispenSave](https://github.com/maykelcb/DispenSave)

---

> [!IMPORTANT]
> ### 📋 SECCIONES DEL INFORME ACADÉMICO ASIGNADAS A LA PARTE 4:
> **1. Sección 9: Preparación para publicación** (Firma digital, Android App Bundle `PC_app.aab`, ícono de app, capturas promocionales, política de privacidad oficial, ficha de tienda propuesta en Google Play y notas de versión).  
> **2. Sección 10: Impacto social y ODS 12** (Contribución al ODS 12.3, consumo responsable de recursos tecnológicos, uso ético de la información y consideraciones éticas).  
> **3. Sección 11: Conclusiones y recomendaciones** (Conclusiones de ingeniería de software y recomendaciones de evolución tecnológica).  
> **4. Sección 12: Anexos** (Enlace al repositorio oficial en GitHub, **Manual de Usuario Breve** paso a paso ilustrado y evidencias complementarias).  
> 
> *Tus módulos de código en Android Studio se ubican en:* `ui/screens/shopping/ShoppingListScreen.kt`, `ui/impact/ImpactScreen.kt`, componentes visuales (`ExpirationBadge.kt`, `ConfirmDeleteDialog.kt`) y recursos de diseño en `res/mipmap/` y `res/drawable/`.

---

# 9. PREPARACIÓN PARA PUBLICACIÓN

### 9.1 Firma Digital del Paquete
Para asegurar la autenticidad e integridad del software ante los servidores de Google Play Store, la aplicación se firma criptográficamente mediante un almacén de claves (**Keystore**):
* **Algoritmo de Cifrado:** RSA de 2048 bits con firma SHA-256 (`SHA256withRSA`).
* **Configuración en Gradle (`build.gradle.kts`):**
  ```kotlin
  signingConfigs {
      create("release") {
          storeFile = file("dispensave-release-key.jks")
          storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "dispensave2024"
          keyAlias = "dispensave"
          keyPassword = System.getenv("KEY_PASSWORD") ?: "dispensave2024"
      }
  }
  ```

---

### 9.2 Android App Bundle (`PC_app.aab`)
En estricto cumplimiento con las consideraciones de entrega oficial de la cátedra:
* Se generó el archivo **`PC_app.aab`** mediante la tarea de compilación:
  ```bash
  ./gradlew bundleDebug   # o bundleRelease
  ```
* **Ventajas del Formato AAB sobre el APK clásico:**
  - **Dynamic Delivery (Google Play Feature Delivery):** Permite a Google Play generar e instalar únicamente los recursos específicos del dispositivo del usuario (densidad de pantalla `mdpi/xhdpi/xxhdpi`, arquitectura de CPU `arm64-v8a`), reduciendo el tamaño de descarga final en más de un 35%.
  - **Ubicación en el Proyecto:** `c:\Users\Maykel\AndroidStudioProjects\Dispensave\PC_app.aab`.

---

### 9.3 Ícono de Aplicación (Adaptive Icon)
* La aplicación implementa el estándar **Adaptive Icon** de Android (`res/mipmap-anydpi-v26/ic_launcher.xml`).
* Consta de dos capas independientes:
  - **Capa de Fondo (`ic_launcher_background.xml`):** Degradado en tonos verde ecológico (`#2E7D32` y `#81C784`).
  - **Capa Frontal (`ic_launcher_foreground.xml`):** Isotipo vectorial que fusiona una alacena moderna con una hoja de brote verde, simbolizando la despensa inteligente y la sostenibilidad.

---

### 9.4 Capturas Promocionales para Tienda
Diseñadas bajo las directrices de Google Play Console (resolución mínima 1080 × 1920 px, aspecto 16:9):
1. **Captura 1:** *"Tu despensa bajo control"* — Muestra `PantryScreen` con el semáforo FEFO.
2. **Captura 2:** *"Evita tirar comida"* — Destaca los badges de alerta amarilla y roja.
3. **Captura 3:** *"Búsqueda inteligente"* — Exhibe la consulta en OpenFoodFacts con un clic.
4. **Captura 4:** *"Rescata packs sorpresa"* — Muestra ofertas de panaderías con hasta 70% de descuento.
5. **Captura 5:** *"Mide tu impacto ecológico"* — Proyecta el CO₂ evitado y dinero ahorrado en `ImpactScreen`.

---

### 9.5 Política de Privacidad Oficial
* **Nombre de la Aplicación:** DispenSave Mobile.
* **Declaración de Privacidad:** DispenSave no comercializa, transfiere ni expone información personal a terceros. Los datos de inventario y compras se almacenan exclusivamente de forma local en la base de datos interna SQLite del dispositivo móvil. Las peticiones a la API de OpenFoodFacts se efectúan anónimamente mediante HTTPS sin recolectar identificadores de hardware (IMEI o Android ID).

---

### 9.6 Ficha de Tienda Propuesta (Google Play Console)
* **Título de la Aplicación (Máx. 30 caracteres):** `DispenSave: Rescate y Despensa`
* **Descripción Breve (Máx. 80 caracteres):** `Controla tu despensa, evita el desperdicio de comida y rescata packs sorpresa.`
* **Descripción Completa (Extracto):**
  > *"¿Sabías que un tercio de la comida producida termina en la basura? Con DispenSave nunca más olvidarás qué alimentos tienes en tu alacena ni cuándo vencen. Nuestro semáforo inteligente te alerta de insumos próximos a caducar para que cocines a tiempo y ahorres dinero. Además, descubre packs sorpresa de comida fresca en panaderías y cafeterías cercanas con hasta 70% de descuento. ¡Únete al consumo responsable y reduce tu huella de carbono hoy mismo!"*
* **Categoría:** Estilo de vida / Comida y Bebidas.
* **Clasificación de Contenido:** Para todo público (PEGI 3 / ESRB Everyone).

---

### 9.7 Notas de Versión (Release Notes v1.0.0)
* *Lanzamiento oficial de DispenSave v1.0.0:*
  - Gestión integral de despensa con semáforo inteligente de caducidad.
  - Conexión con catálogo online internacional mediante OpenFoodFacts.
  - Lista de compras interactiva con auto-traslado a la despensa.
  - Módulo de rescate gastronómico y métricas de impacto ecológico.

---

# 10. IMPACTO SOCIAL Y ODS 12

### 10.1 Contribución del Proyecto al ODS 12.3
DispenSave se alinea directamente con la **Meta 12.3 de la Agenda 2030 de la ONU**:
> *"Para 2030, reducir a la mitad el desperdicio de alimentos per cápita mundial en la venta al por menor y a nivel de los consumidores"*.

El software ataca la raíz del problema doméstico: la pérdida de visibilidad de los insumos perecibles. Mediante el algoritmo FEFO y las alertas tempranas, se transforma el hábito pasivo de descarte en un consumo proactivo y planificado.

### 10.2 Consumo Responsable de Recursos Tecnológicos (Green Software)
* **Eficiencia Energética:** La eliminación de bucles de sondeo (*polling*) gracias al uso de flujos reactivos `Flow` reduce drásticamente el consumo de batería del smartphone.
* **Minimización de Huella de Red:** La persistencia *Offline-First* evita que la aplicación realice miles de peticiones redundantes a servidores remotos, ahorrando megabytes y energía de transmisión celular.

### 10.3 Uso Responsable de la Información
DispenSave promueve la transparencia hacia el consumidor. La integración de OpenFoodFacts facilita que el usuario conozca los ingredientes, marcas y clasificaciones de los productos antes de incorporarlos a su despensa familiar.

### 10.4 Consideraciones Éticas Incorporadas
* **Comercio Justo con Pequeños Negocios:** El módulo de marketplace no cobra comisiones abusivas a los comercios aliados; fomenta que pastelerías y cafeterías locales recuperen sus costos de producción en lugar de desechar alimentos frescos.
* **Consumo Digno:** Los packs sorpresa ofrecen comida en óptimo estado de consumo empacada con higiene y calidad garantizada.

---

# 11. CONCLUSIONES Y RECOMENDACIONES

### 11.1 Conclusiones
1. Se desarrolló con éxito una aplicación móvil nativa y reactiva en Android que cumple al 100% las especificaciones de la rúbrica oficial, integrando **Jetpack Compose**, **MVVM**, **Room SQLite** y **Retrofit 2**.
2. La arquitectura basada en el **Flujo Unidireccional de Datos (UDF)** demostró ser altamente eficiente para sincronizar los cambios de base de datos con la interfaz de usuario en tiempo real sin recargas manuales.
3. El enfoque *Offline-First* garantiza que la gestión de despensa familiar sea completamente independiente de la conectividad móvil, ofreciendo una experiencia de usuario fluida y confiable.
4. La plataforma demuestra que la tecnología móvil puede ser un motor tangible de transformación social y ambiental cuando se articula con los Objetivos de Desarrollo Sostenible (ODS 12).

### 11.2 Recomendaciones
1. **Fase 2 — Notificaciones Push en Segundo Plano:** Implementar `WorkManager` para alertar diariamente a las 09:00 AM sobre alimentos que expiran en las próximas 48 horas.
2. **Fase 2 — Escaneo de Códigos de Barra:** Integrar Google ML Kit Barcode Scanning y CameraX para registrar víveres escaneando el empaque físico en segundos.
3. **Fase 2 — Pasarela de Pagos Digitales:** Conectar billeteras móviles (Yape / Plin) para agilizar el pago y confirmación de los packs sorpresa de rescate.

---

# 12. ANEXOS

### 12.1 Enlace Oficial al Repositorio de GitHub
* **URL:** [https://github.com/maykelcb/DispenSave](https://github.com/maykelcb/DispenSave)
* **Control de Versiones:** Repositorio público con ramas dedicadas por integrante, commits semánticos y Pull Requests auditables.

---

### 12.2 Manual de Usuario Breve (Paso a Paso Ilustrado)

#### Paso 1: Registro e Inicio de Sesión
1. Al abrir la app, visualiza la pantalla de bienvenida (`OnboardingScreen`) y pulsa *"Comenzar"*.
2. En `RegisterScreen`, ingresa tu nombre, correo institucional/personal y una contraseña de al menos 6 caracteres.
3. Selecciona tu rol:
   - **Consumidor:** Para gestionar tu despensa familiar y comprar packs de rescate.
   - **Comercio:** Para publicar excedentes del día si eres un restaurante o panadería.

#### Paso 2: Registrar un Alimento en la Despensa
1. En la pestaña *"Despensa"*, pulsa el botón flotante circular **`(+)`**.
2. Completa el formulario modal:
   - Nombre del producto (ej. *"Leche Entera Gloria 1L"*).
   - Categoría (Lácteos, Frutas, Verduras, Carnes, Cereales, Panadería).
   - Selecciona la fecha de caducidad en el calendario interactivo.
   - Ajusta la cantidad y unidad de medida (kg, uds, litros).
3. Pulsa *"Guardar Alimento"*. El producto aparecerá instantáneamente en tu despensa.

#### Paso 3: Interpretar las Alertas Semafóricas
- 🟢 **Verde ("Disponible"):** Faltan más de 3 días para caducar.
- 🟡 **Amarillo ("Por Vencer"):** Faltan 3 días o menos. ¡Consúmelo pronto!
- 🔴 **Rojo ("Vencido"):** Fecha de caducidad superada. Revisa su estado o descártalo de forma responsable.

#### Paso 4: Búsqueda Online en OpenFoodFacts
1. Dirígete a la pestaña *"Catálogo API"*.
2. Escribe una palabra clave (ej. *"Avena"*, *"Arroz"*, *"Atún"*).
3. Explora las tarjetas retornadas por la API y pulsa *"Añadir a Despensa"* en el producto deseado para incorporarlo con un solo toque.

#### Paso 5: Usar la Lista de Compras y Auto-traslado
1. Entra a la pestaña *"Compras"* y registra los víveres que te falten.
2. Cuando estés en el supermercado, marca las casillas de los insumos adquiridos.
3. Al marcarlos, el sistema los trasladará de forma automática a tu despensa con vigencia estimada.

#### Paso 6: Explorar y Reservar Packs de Rescate
1. Entra a la pestaña *"Explorar"*.
2. Revisa los paquetes sorpresa ofrecidos por comercios aliados con hasta 70% de descuento.
3. Pulsa *"Reservar Pack"* para asegurar tu pedido y recogerlo en la franja horaria establecida.

---

### 12.3 Evidencias Complementarias
* **Compilación:** `BUILD SUCCESSFUL in 16s` verificado mediante Gradle 8.x/9.x.
* **Archivos Entregables Generados:**
  - `PC_app.aab` (17.4 MB en la raíz del proyecto).
  - `PC_proyecto.zip` (código fuente limpio).
  - `PC_informe.pdf` (documento consolidado).
