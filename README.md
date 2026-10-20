# DispenSave

> Plataforma de rescate de comida al estilo Too Good To Go para el mercado peruano.

**DispenSave** conecta a restaurantes, panaderias, cafeterias y pastelerias locales con consumidores
que quieren adquirir excedentes de comida fresca a precios de rescate (60-75% de descuento),
reduciendo el desperdicio alimentario de forma practica y sostenible.

---

## Equipo de Desarrollo

| Parte | Responsable | Modulo |
|-------|-------------|--------|
| Parte 1 | Maykel | Autenticacion, Roles, Registro, Catalogo de Comercio, Dashboard |
| Parte 2 | Ruben  | Exploracion para Clientes, Reservas, Historial |
| Parte 3 | Luis   | Impacto CO2, Estadisticas, Gamificacion |
| Parte 4 | Andy   | Backend, API REST, Base de Datos Remota |

---

## Funcionalidades de Parte 1 (Maykel - COMPLETADO)

### Para Clientes (Consumidores)
- Registro con validacion estricta de correo (solo dominios reales), nombre (min 3 letras, sin repeticiones) y contrasena (mayuscula + numero + especial)
- Login con manejo de errores (correo no registrado vs. contrasena incorrecta)
- Aceptacion obligatoria de Terminos y Condiciones
- Exploracion de packs del dia y productos individuales
- Filtro por categoria: Panaderia, Restaurante, Cafeteria, Pasteleria
- Vista de contenido del pack antes de reservar
- Ticket digital con codigo de recojo
- Badge de descuento resaltado para packs con mayor ahorro (60-75%)

### Para Comercios
- Registro con seleccion OBLIGATORIA de rubro (Panaderia / Restaurante / Cafeteria / Pasteleria)
- Panel del comercio con 3 pestanas:
  - **Publicaciones**: packs y productos publicados con precio original tachado, precio rescate y descuento
  - **Mi Catalogo**: inventario propio con botones +/- de stock, agregar y eliminar productos libremente
  - **Pedidos de Clientes**: pedidos pendientes con codigo del cliente, boton de confirmar entrega
- Publicar **Producto Individual**: selecciona 1 item de su catalogo
- Publicar **Pack de Hoy**: multi-seleccion de varios items de su catalogo, mayor descuento
- Validacion numerica estricta en precios y cantidades (sin letras)
- Franjas horarias predefinidas mediante chips

---

## Tecnologias

- **Android Studio** + Kotlin
- **Jetpack Compose** para UI
- **Navigation Compose** para navegacion
- **ViewModel** + StateFlow para manejo de estado
- **Room Database** (preparado para Parte 4)
- **SharedPreferences** para persistencia local de usuarios registrados

---

## Estructura del Proyecto

```
app/src/main/java/com/example/dispensave/
├── data/repository/         # RescueRepository, UserRepository, PantryRepository
├── domain/model/            # User, SurplusBag, Order, MerchantProduct, etc.
├── ui/
│   ├── auth/                # LoginScreen, RegisterScreen
│   ├── home/                # ExploreScreen (cliente)
│   ├── merchant/            # StoreDashboardScreen (comercio)
│   ├── navigation/          # AppNavigation
│   ├── onboarding/          # OnboardingScreen
│   ├── orders/              # MyOrdersScreen (adaptado por rol)
│   └── impact/              # ImpactScreen
└── util/ValidationUtils.kt
```

---

## Como Ejecutar

1. Clonar el repositorio: `git clone https://github.com/maykelcb/DispenSave.git`
2. Abrir en **Android Studio Hedgehog** o superior
3. Esperar que Gradle sincronice automaticamente
4. Ejecutar en dispositivo o emulador con **API 26+**

### Cuentas de prueba
| Correo | Contrasena | Rol |
|--------|-----------|-----|
| maykel@universidad.edu.pe | Password123* | Cliente |
| labaguette@gmail.com | Password123* | Comercio - Panaderia |
| saborcriollo@gmail.com | Password123* | Comercio - Restaurante |

---

## Documentacion Detallada

Ver [docs/documento_parte1_dispensave.md](docs/documento_parte1_dispensave.md) para documentacion completa de la Parte 1.

---

## Proximos Pasos

- **Ruben (Parte 2)**: Mejorar ExploreScreen con imagenes, busqueda, favoritos y notificaciones
- **Luis (Parte 3)**: Estadisticas de impacto CO2, logros y graficas
- **Andy (Parte 4)**: API REST real, JWT, Firebase y geolocalizacion
