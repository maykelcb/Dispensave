# DispenSave – Documentacion Parte 1

> **Plataforma de rescate de comida** inspirada en Too Good To Go.
> Version: 1.0 - Parte 1 (Autenticacion + Roles + Dashboard de Comercio + Catalogo de Productos)

---

## Distribucion del Equipo

| Parte | Responsable | Modulo |
|-------|-------------|--------|
| Parte 1 | Maykel | Autenticacion, Roles, Registro, Catalogo de Comercio, Dashboard |
| Parte 2 | Ruben  | Pantalla de Exploracion para Clientes, Reservas, Historial de Pedidos |
| Parte 3 | Luis   | Pantalla de Impacto CO2, Estadisticas de Rescate, Gamificacion |
| Parte 4 | Andy   | Integracion Backend/API real, Persistencia en base de datos remota |

---

## Descripcion General

DispenSave es una aplicacion Android nativa (Kotlin + Jetpack Compose) que conecta a comercios gastronomicos
(restaurantes, panaderias, cafeterias y pastelerias) con consumidores que desean adquirir excedentes de comida
a precios de rescate (60-75% de descuento), reduciendo el desperdicio alimentario.

Rubros habilitados en la plataforma:
- Panaderia
- Restaurante
- Cafeteria
- Pasteleria

---

## Arquitectura

Capa de Presentacion (Jetpack Compose UI)
  - ui/auth/        -> LoginScreen, RegisterScreen
  - ui/onboarding/  -> OnboardingScreen
  - ui/home/        -> ExploreScreen (cliente)
  - ui/merchant/    -> StoreDashboardScreen (comercio)
  - ui/orders/      -> MyOrdersScreen (adaptado por rol)
  - ui/impact/      -> ImpactScreen
  - ui/navigation/  -> AppNavigation, ContenedorPrincipal

Capa de Dominio
  - domain/model/   -> User, UserRole, SurplusBag, OfferType,
                       StoreCategory, Order, MerchantProduct, CategoryCatalog

Capa de Datos
  - data/repository/ -> UserRepository, RescueRepository, PantryRepository

Utilidades
  - util/            -> ValidationUtils

---

## Roles de Usuario

### CLIENTE (Consumidor)
- Registrarse con correo y contrasena
- Explorar y filtrar publicaciones por categoria
- Ver el detalle de un Pack del Dia o Producto Individual
- Reservar un rescate y recibir un ticket digital con codigo unico
- Ver historial de rescates activos y pasados
- Confirmar el recojo del rescate

### COMERCIO (Establecimiento Gastronomico)
- Registrarse indicando su RUBRO OBLIGATORIAMENTE (Panaderia / Restaurante / Cafeteria / Pasteleria)
- Gestionar su CATALOGO PROPIO DE PRODUCTOS (agregar, eliminar, controlar stock con botones +/-)
- Publicar Producto Individual: selecciona 1 producto de su catalogo, precio, franja horaria, cantidad
- Publicar Pack de Hoy: multi-seleccion de productos para armar un pack con mayor descuento (60-75%)
- Ver publicaciones activas con precio original tachado, precio rescate, descuento y lista de productos
- Gestionar PEDIDOS DE CLIENTES y confirmar la entrega

---

## Validaciones Implementadas (Parte 1)

### Nombre de Usuario
- Minimo 3 caracteres
- No se permiten 3 letras iguales consecutivas (ej: "aaa", "nnnn")
- Ejemplos validos: "Ana", "Noa", "Luis"

### Correo Electronico
Solo dominios reconocidos:
- @gmail.com, @outlook.com, @hotmail.com, @yahoo.com, @icloud.com
- @universidad.edu.pe, @pucp.edu.pe, @ulima.edu.pe, @usmp.edu.pe, @utp.edu.pe, @upn.edu.pe
- @empresa.com.pe, @empresa.org.pe, @empresa.org

### Contrasena
- Minimo 6 caracteres
- Al menos 1 letra mayuscula
- Al menos 1 numero
- Al menos 1 caracter especial (!@#$%&*)

### Formularios de Publicacion
- Campos de precio y cantidad: SOLO aceptan digitos, se filtran letras automaticamente
- Franja horaria: chips predefinidos, sin texto libre (18:00-19:30 / 19:00-20:30 / 20:00-21:00 / 21:00-22:00)

---

## Modelos de Dominio

### User
- name: String, email: String, password: String
- role: UserRole (CLIENTE o COMERCIO)
- storeName: String? (solo COMERCIO)
- storeCategory: StoreCategory? (solo COMERCIO: PANADERIA / RESTAURANTE / CAFETERIA / PASTELERIA)

### SurplusBag (Oferta de Rescate)
- id, storeId, storeName, category: StoreCategory
- offerType: OfferType (PACK o INDIVIDUAL)
- title, description, includedItems: List<String>
- originalPrice, rescuePrice, pickupTimeWindow, stockAvailable, rating, address, distanceKm

### MerchantProduct (Catalogo del Comercio)
- id: Int, name: String (escrito libremente por el comerciante)
- stock: Int (ajustable con botones +/-), unit: String

---

## Pantallas Implementadas en Parte 1

| Pantalla          | Archivo                  | Acceso           |
|-------------------|--------------------------|------------------|
| Bienvenida        | OnboardingScreen.kt      | Todos            |
| Iniciar Sesion    | LoginScreen.kt           | Todos            |
| Registro          | RegisterScreen.kt        | Todos            |
| Explorar Rescates | ExploreScreen.kt         | Solo CLIENTE     |
| Panel del Comercio| StoreDashboardScreen.kt  | Solo COMERCIO    |
| Mis Rescates      | MyOrdersScreen.kt        | Ambos (vista dif)|
| Impacto CO2       | ImpactScreen.kt          | Ambos            |

---

## Pendientes para las Otras Partes

### Parte 2 - Ruben (Exploracion y Reservas del Cliente)
- Mejorar el diseno visual de ExploreScreen con imagenes reales por categoria
- Implementar busqueda por nombre de local
- Sistema de favoritos (guardar locales favoritos)
- Notificaciones locales cuando hay nuevos packs disponibles
- Vista de detalle expandida de un pack antes de reservar

### Parte 3 - Luis (Impacto CO2 y Estadisticas)
- Calcular CO2 ahorrado por rescate (promedio: 2.5 kg CO2 por comida rescatada)
- Mostrar estadisticas acumuladas del usuario (total rescatado, dinero ahorrado, CO2 evitado)
- Sistema de logros/insignias por cantidad de rescates
- Graficas de impacto mensual
- Comparativas con la comunidad DispenSave

### Parte 4 - Andy (Backend y Persistencia)
- Integrar API REST real (reemplazar RescueRepository en memoria por llamadas HTTP)
- Autenticacion con JWT tokens
- Base de datos remota para usuarios, productos y pedidos
- Subida de imagenes de productos a servidor
- Sistema de notificaciones push (Firebase Cloud Messaging)
- Geolocalizacion real para calcular distancias a locales

---

## Cuentas de Prueba Precargadas

| Correo                         | Contrasena      | Rol      | Categoria  |
|-------------------------------|-----------------|----------|------------|
| maykel@universidad.edu.pe      | Password123*    | Cliente  | -          |
| maykelcb095@gmail.com          | Password123*    | Cliente  | -          |
| labaguette@gmail.com           | Password123*    | Comercio | Panaderia  |
| saborcriollo@gmail.com         | Password123*    | Comercio | Restaurante|

---

## Como Ejecutar el Proyecto

1. Clonar: git clone https://github.com/maykelcb/DispenSave.git
2. Abrir en Android Studio (version Hedgehog o superior)
3. Sincronizar dependencias de Gradle (automatico)
4. Ejecutar en emulador o dispositivo fisico (API 26+)
