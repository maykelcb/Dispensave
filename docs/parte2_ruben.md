# GUÍA Y PLANTILLA OFICIAL — PROYECTO DISPENSAVE
## DOCUMENTO DE ENTREGA: PARTE 2
### Responsable Asignado: Persona 2 (Rubén / Desarrollador de Servicios Web & Seguridad)
### Rama Oficial de GitHub: `feature/retrofit-api-consumption`
### Repositorio: [https://github.com/maykelcb/DispenSave](https://github.com/maykelcb/DispenSave)

---

> [!IMPORTANT]
> ### 📋 SECCIONES DEL INFORME ACADÉMICO ASIGNADAS A LA PARTE 2:
> **1. Sección 3: Tecnología para el Consumo de Servicios Web** (Retrofit 2, OkHttp, API OpenFoodFacts, Endpoints, JSON DTOs, manejo de respuestas y errores).  
> **2. Sección 7: Privacidad y Seguridad** (Protección de datos en disco y red, validaciones de `ValidationUtils.kt`, permisos de Android y buenas prácticas).  
> 
> *Tu código en Android Studio se ubica en:* `data/remote/`, `data/model/`, `ui/screens/search/` y `util/ValidationUtils.kt`.

---

# 3. TECNOLOGÍA PARA EL CONSUMO DE SERVICIOS WEB

### 3.0 Descripción de la Técnica y Tecnología Empleada

Para satisfacer el requisito de conectividad externa con servicios en la nube y catálogo de alimentos, **DispenSave** utiliza la librería **Retrofit 2 (v2.9.0)** desarrollada por Square, complementada por el cliente de transporte **OkHttp 3 (v4.12.0)** y el conversor de serialización **Gson Converter (v2.9.0)**.

**¿Por qué Retrofit 2 en lugar de KtorClient o Volley?**
1. **Tipado Fuerte y Seguridad en Tiempo de Compilación:** Retrofit traduce interfaces declarativas de Kotlin con anotaciones (`@GET`, `@Query`, `@Path`) en llamadas HTTP totalmente tipadas, eliminando la manipulación propensa a errores de URLs mediante concatenación de cadenas.
2. **Integración Nativa con Corrutinas de Kotlin:** Permite declarar métodos suspendidos (`suspend fun`), ejecutando las peticiones de red fuera del hilo principal de la interfaz (*Main Thread*) de forma fluida y sin bloqueos de UI (*Application Not Responding - ANR*).
3. **Eficiencia en la Capa de Transporte con OkHttp:** OkHttp gestiona la reutilización de conexiones (*Connection Pooling*), compresión transparente Gzip y soporte para HTTP/2, optimizando el consumo de batería y datos móviles.
4. **Inspección y Diagnóstico en Desarrollo:** Permite acoplar un `HttpLoggingInterceptor` para auditar el tráfico HTTP en tiempo real desde el Logcat de Android Studio.

---

### 3.1 Diseño de la API

DispenSave consume la **API REST pública internacional de OpenFoodFacts**, una base de datos colaborativa abierta con más de 3 millones de referencias de alimentos a nivel mundial.

#### 3.1.1 Endpoints Utilizados
* **Base URL:** `https://world.openfoodfacts.org/`
* **Ruta del Endpoint:** `cgi/search.pl`
* **URL Completa de Consulta:** `https://world.openfoodfacts.org/cgi/search.pl?search_terms={termino}&search_simple=1&action=process&json=1&page_size=20`

#### 3.1.2 Métodos HTTP Empleados y Parámetros
* **Método HTTP:** `GET` (Lectura segura e idempotente que no altera el estado del servidor remoto).
* **Parámetros de Consulta (*Query Parameters*):**
  - `search_terms` (`String`): Término o palabra clave del alimento ingresada por el usuario (ej. *"arroz"*, *"leche"*, *"avena"*).
  - `search_simple` (`Int = 1`): Activa la búsqueda simplificada por nombres de producto y marcas comerciales.
  - `action` (`String = "process"`): Solicita el procesamiento del motor de indexación de OpenFoodFacts.
  - `json` (`Int = 1`): Especifica que la respuesta debe ser devuelta en formato estructurado JSON.
  - `page_size` (`Int = 20`): Limita la respuesta a un máximo de 20 resultados por solicitud para optimizar el consumo de ancho de banda y memoria.

**Declaración en Kotlin (`FoodApiService.kt`):**
```kotlin
package com.example.dispensave.data.remote

import com.example.dispensave.data.model.OpenFoodFactsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface FoodApiService {
    @GET("cgi/search.pl")
    suspend fun searchProducts(
        @Query("search_terms") query: String,
        @Query("search_simple") simple: Int = 1,
        @Query("action") action: String = "process",
        @Query("json") json: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): OpenFoodFactsResponseDto
}
```

#### 3.1.3 Estructuras JSON y Clases DTO

La respuesta JSON devuelta por el servidor es deserializada automáticamente mediante **Gson** en clases de datos de transferencia (**Data Transfer Objects - DTO**):

**Estructura del Payload JSON Retornado:**
```json
{
  "count": 45,
  "page": 1,
  "page_size": 20,
  "products": [
    {
      "code": "7750106001234",
      "product_name": "Arroz Extra Valle del Norte",
      "product_name_es": "Arroz Extra",
      "brands": "Valle del Norte",
      "categories": "Abarrotes, Cereales, Granos",
      "quantity": "1 kg"
    }
  ]
}
```

**Modelos DTO en Kotlin (`OpenFoodFactsDto.kt`):**
```kotlin
package com.example.dispensave.data.model

import com.google.gson.annotations.SerializedName

data class OpenFoodFactsResponseDto(
    @SerializedName("count") val count: Int = 0,
    @SerializedName("page") val page: Int = 1,
    @SerializedName("page_size") val pageSize: Int = 20,
    @SerializedName("products") val products: List<RemoteFoodProductDto> = emptyList()
)

data class RemoteFoodProductDto(
    @SerializedName("code") val barcode: String? = null,
    @SerializedName("product_name") val productName: String? = null,
    @SerializedName("product_name_es") val productNameEs: String? = null,
    @SerializedName("brands") val brands: String? = null,
    @SerializedName("categories") val categories: String? = null,
    @SerializedName("quantity") val quantity: String? = null
) {
    // Propiedades de conveniencia con valores por defecto
    val displayName: String get() = productNameEs ?: productName ?: "Alimento sin nombre"
    val displayBrand: String get() = brands ?: "Marca genérica"
    val displayCategory: String get() = categories?.split(",")?.firstOrNull()?.trim() ?: "Despensa"
    val displayUnit: String get() = if (quantity?.contains("kg", true) == true) "kg" else "uds"
}
```

---

### 3.2 Consumo de Servicios

#### 3.2.1 Configuración de Retrofit (`RetrofitClient.kt`)

La inicialización del cliente de red se implementa bajo el patrón **Singleton**, garantizando una única instancia compartida durante el ciclo de vida de la aplicación:

```kotlin
package com.example.dispensave.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://world.openfoodfacts.org/"

    // 1. Configuración del cliente OkHttp con tiempos de espera e interceptores
    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Registra URL, encabezados y payloads
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS) // Tiempo máximo para establecer el socket
            .readTimeout(15, TimeUnit.SECONDS)    // Tiempo máximo para recibir paquetes
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    // 2. Construcción de la instancia Retrofit con conversor Gson
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: FoodApiService by lazy {
        retrofit.create(FoodApiService::class.java)
    }
}
```

#### 3.2.2 Manejo de Respuestas y Estados Asíncronos

En `PantryViewModel.kt`, el flujo de consumo web se modela mediante una máquina de estados reactiva con una clase sellada (**Sealed Class**):

```kotlin
sealed class ProductSearchUiState {
    object Idle : ProductSearchUiState()
    object Loading : ProductSearchUiState()
    data class Success(val products: List<RemoteFoodProductDto>) : ProductSearchUiState()
    data class Error(val message: String) : ProductSearchUiState()
}
```

#### 3.2.3 Manejo de Errores y Tolerancia a Fallos

Las solicitudes HTTP se encapsulan dentro de un bloque estructurado `try-catch` dentro de una corrutina lanzada en `viewModelScope`. Si el dispositivo pierde la señal Wi-Fi/4G o el servidor remoto experimenta saturación, el sistema captura la excepción y notifica a la vista sin provocar un cierre inesperado (*Crash*):

```kotlin
fun searchRemoteProducts(query: String) {
    val cleanQuery = query.trim()
    if (cleanQuery.length < 2) {
        searchUiState = ProductSearchUiState.Error("Ingresa al menos 2 caracteres para buscar en el catálogo.")
        return
    }

    searchUiState = ProductSearchUiState.Loading

    viewModelScope.launch {
        try {
            // Llamada asíncrona suspendida con Retrofit
            val response = RetrofitClient.apiService.searchProducts(query = cleanQuery)
            val validProducts = response.products.filter {
                !it.productName.isNullOrBlank() || !it.productNameEs.isNullOrBlank()
            }

            if (validProducts.isEmpty()) {
                searchUiState = ProductSearchUiState.Error("No se encontraron alimentos para \"$cleanQuery\".")
            } else {
                searchUiState = ProductSearchUiState.Success(validProducts)
            }
        } catch (e: java.net.UnknownHostException) {
            searchUiState = ProductSearchUiState.Error("Sin conexión a Internet. Verifica tu red móvil o Wi-Fi.")
        } catch (e: java.net.SocketTimeoutException) {
            searchUiState = ProductSearchUiState.Error("El servidor tardó demasiado en responder (Tiempo de espera agotado).")
        } catch (e: Exception) {
            searchUiState = ProductSearchUiState.Error("Error inesperado en el servicio: ${e.localizedMessage}")
        }
    }
}
```

---

# 7. PRIVACIDAD Y SEGURIDAD

### 7.1 Protección de Datos

#### 7.1.1 Datos Almacenados en la Aplicación
DispenSave gestiona exclusivamente datos orientados a la operatividad del inventario doméstico y reservas comerciales:
1. **Datos de Despensa:** Nombre del producto, categoría alimenticia, fecha estimada de caducidad (timestamp `Long`), cantidad física y unidad de medida.
2. **Datos de Usuario y Sesión:** Nombre completo o comercial, correo electrónico institucional/personal y rol de acceso (`CLIENTE` o `COMERCIO`).
3. **Datos de Rescate:** Identificador de tienda, títulos de ofertas sorpresa y códigos de reserva generados.

#### 7.1.2 Medidas de Protección Implementadas
* **Almacenamiento Local Aislado (*Application Sandbox*):** La base de datos SQLite gestionada por Room (`dispensave_database`) y las preferencias compartidas se ubican en el directorio privado del paquete: `/data/data/com.example.dispensave/databases/`. Ninguna otra aplicación instalada en el dispositivo móvil puede acceder a estos archivos sin permisos de superusuario (*Root*).
* **Tránsito Cifrado Estricto (HTTPS / TLS 1.3):** Todas las consultas dirigidas al servicio web de OpenFoodFacts se efectúan exclusivamente a través de conexiones seguras HTTPS con certificados digitales válidos, previniendo ataques de interceptación de tráfico (*Man-in-the-Middle - MitM*).
* **Exclusión de Información Sensible:** DispenSave **no** almacena números de tarjetas de crédito, claves bancarias ni datos biométricos en la memoria persistente del teléfono.

---

### 7.2 Seguridad de la Aplicación

#### 7.2.1 Validaciones Realizadas (`ValidationUtils.kt`)
Para prevenir la introducción de datos anómalos, inyecciones de código o estados inconsistentes, se implementó el módulo centralizado `ValidationUtils.kt`:

1. **Validación Estricta de Correo Electrónico:**
   ```kotlin
   fun validateEmailStrict(email: String): String? {
       val trimmed = email.trim()
       if (trimmed.isEmpty()) return "El correo es requerido"
       val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
       if (!emailRegex.matches(trimmed)) return "Formato de correo inválido"
       return null
   }
   ```
2. **Validación de Formularios de Alimentos:**
   - Longitud mínima de nombre (al menos 2 caracteres no vacíos).
   - Fechas de expiración positivas y numéricas (evita *overflows* temporales).
   - Cantidades físicas estrictamente mayores a cero (`quantity > 0`).
3. **Prevención de Inyección SQL:**
   - La librería Room compila y parametriza todas las sentencias SQL en tiempo de diseño, impidiendo ataques de inyección a través de campos de texto.

#### 7.2.2 Gestión de Permisos en Android
Siguiendo el principio de **Mínimo Privilegio (*Least Privilege Principle*)**, el manifiesto de la aplicación (`AndroidManifest.xml`) únicamente solicita los permisos indispensables para la funcionalidad de red:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- Permiso esencial para realizar consultas HTTP con Retrofit -->
    <uses-permission android:name="android.permission.INTERNET" />
    <!-- Permiso para verificar si existe red activa antes de consultar la API -->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
</manifest>
```

#### 7.2.3 Buenas Prácticas de Codificación Segura
* **Seguridad ante Nulos de Kotlin (*Null Safety*):** Uso sistemático de tipos no anulables y operadores seguros (`?.`, `?:`) que mitigan completamente excepciones `NullPointerException`.
* **Inmutabilidad de Estados:** Exposición de flujos observables públicos como `StateFlow` inmutables, reteniendo la mutabilidad (`MutableStateFlow`) estrictamente en el interior del ViewModel.
* **Corrutinas Estructuradas:** Uso de `viewModelScope` para cancelar automáticamente peticiones de red activas cuando la pantalla se destruye, eliminando fugas de memoria (*Memory Leaks*).
