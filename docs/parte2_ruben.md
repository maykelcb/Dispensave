> [!IMPORTANT]
> ### 📋 SECCIÓN DEL INFORME ACADÉMICO ASIGNADA A RUBÉN:
> **Secciones a redactar en el informe para Word:**
> 1. **Sección 4.2: Tecnologías Utilizadas y Servicios API** (Consumo de API REST externa, Retrofit, OkHttp, manejo de estados asíncronos y arquitectura de red).
> 2. **Sección V.1: Historias de Usuario HU05 a HU08** en formato tabla (Búsqueda remota de productos, catálogo externo, validación de conexión y manejo de errores).
> 3. **Sección V.2: Sprint 2 de Desarrollo** (Objetivo, funcionalidades y actividades ejecutadas).
> 4. **Sección VI.2: Evidencias de Implementación** (Código de endpoints Retrofit, DTOs y capturas de respuestas HTTP exitosas).
>
> *Para ver la asignación completa del equipo, revisa [DISTRIBUCION_INFORME_EQUIPO.md](DISTRIBUCION_INFORME_EQUIPO.md).*

---

# Parte 2 — Consumo de Servicio REST / API Externa con Retrofit
**Responsable:** Rubén  
**Rama Oficial en GitHub:** `feature/retrofit-api-consumption`  
**Comando para trabajar:** `git checkout feature/retrofit-api-consumption`

---

## Objetivo General
Cumplir con el **requisito obligatorio de la rúbrica académica del docente**:
> *"Consumo de al menos un servicio REST o API. Restricciones: No se aceptarán aplicaciones únicamente estáticas ni sin consumo de API."*

Tu misión técnica es integrar **Retrofit + OkHttp** en Android Studio para consultar información de alimentos o productos desde una API externa (por ejemplo: Open Food Facts o una API REST pública de catálogo de alimentos), implementando manejo de estados de carga (`Loading`), éxito (`Success`) y errores de red (`Error`).

---

## Archivos que debes crear / modificar

| Archivo | Ubicación | Qué hacer |
|---|---|---|
| `FoodApiService.kt` | `data/remote/` | Definir la interfaz de Retrofit con anotaciones `@GET` |
| `RetrofitClient.kt` | `data/remote/` | Instancia Singleton de Retrofit con baseUrl y converter Gson/Moshi |
| `FoodProductDto.kt` | `data/remote/dto/` | Clases de datos para parsear las respuestas JSON de la API |
| `RemoteFoodRepository.kt` | `data/repository/` | Repositorio que orquesta las llamadas asíncronas con Flow/Coroutines |
| `build.gradle.kts` | `app/` | Dependencias de Retrofit, Converter y OkHttp Logging Interceptor |

---

## Tareas Detalladas

### TAREA 2.1 — Configurar dependencias en `build.gradle.kts (app)`
```kotlin
// Retrofit & OkHttp
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
```

### TAREA 2.2 — Crear el servicio API y cliente HTTP
Crear la interfaz en `data/remote/FoodApiService.kt`:
```kotlin
interface FoodApiService {
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProductByBarcode(@Path("barcode") barcode: String): Response<FoodProductResponseDto>
}
```

### TAREA 2.3 — Conectar con el ViewModel y la UI
Exponer el estado observable de la búsqueda remota en `PantryViewModel.kt` o un `SearchViewModel` para mostrar los resultados en pantalla con soporte para reintento en caso de fallo de conexión.

---

## Flujo de Trabajo en Git

1. Cambia a tu rama asignada:
   ```bash
   git checkout feature/retrofit-api-consumption
   ```
2. Realiza tus commits pequeños y descriptivos:
   ```bash
   git add -A
   git commit -m "feat(api): integracion de Retrofit para busqueda remota de productos"
   ```
3. Sube tus cambios a GitHub:
   ```bash
   git push origin feature/retrofit-api-consumption
   ```
4. Abre un Pull Request hacia `main` y avisa a Maykel para la revisión.
