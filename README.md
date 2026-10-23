# DispenSave

> Plataforma de rescate de comida al estilo Too Good To Go para el mercado peruano.

**DispenSave** conecta a restaurantes, panaderías, cafeterías y pastelerías locales con consumidores
que quieren adquirir excedentes de comida fresca a precios de rescate (60-75% de descuento),
reduciendo el desperdicio alimentario de forma práctica y sostenible.

---

## Equipo de Desarrollo y Ramas Oficiales en GitHub

| Parte | Integrante | Rol | Rama Oficial de Git | Estado | Documentación |
|:---:|:---|:---|:---:|:---:|:---|
| **Parte 1** | **Maykel Bernaola** (`maykelcb`) | Líder Técnico & Arquitectura MVVM | `main` | **COMPLETADO** | [docs/documento_parte1_dispensave.md](docs/documento_parte1_dispensave.md) |
| **Parte 2** | **Rubén** | Desarrollador APIs & Retrofit REST | `feature/retrofit-api-consumption` | PENDIENTE | [docs/parte2_ruben.md](docs/parte2_ruben.md) |
| **Parte 3** | **Luis** | Desarrollador SQLite & Room Database | `feature/room-crud-pantry` | PENDIENTE | [docs/parte3_luis.md](docs/parte3_luis.md) |
| **Parte 4** | **Anderson Abanto** (`Anderson08102001`) | Desarrollador UI/UX & Lista de Compras | `feature/shopping-list-alerts` | **IMPLEMENTADO** | [docs/parte4_shopping_alerts.md](docs/parte4_shopping_alerts.md) |

---

## Cómo empezar (para cada integrante del equipo)

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/maykelcb/DispenSave.git
   cd DispenSave
   ```

2. **Abrir en Android Studio** (versión Ladybug / Hedgehog o superior).

3. **Cambiar a TU rama asignada antes de programar:**
   ```bash
   # Para Maykel (Líder / Integración):
   git checkout main

   # Para Anderson (Compras y Alertas UX):
   git checkout feature/shopping-list-alerts

   # Para Luis (Persistencia Room CRUD):
   git checkout feature/room-crud-pantry

   # Para Rubén (Consumo de API REST Retrofit):
   git checkout feature/retrofit-api-consumption
   ```

4. **Leer tu documentación específica:**
   - Asignación maestra: [docs/DISTRIBUCION_INFORME_EQUIPO.md](docs/DISTRIBUCION_INFORME_EQUIPO.md)
   - Maykel: [docs/documento_parte1_dispensave.md](docs/documento_parte1_dispensave.md)
   - Rubén: [docs/parte2_ruben.md](docs/parte2_ruben.md)
   - Luis: [docs/parte3_luis.md](docs/parte3_luis.md)
   - Anderson: [docs/parte4_shopping_alerts.md](docs/parte4_shopping_alerts.md)

5. **Guardar cambios y subir a GitHub:**
   ```bash
   git add -A
   git commit -m "feat(modulo): descripcion clara del cambio"
   git push origin <tu-rama-asignada>
   ```

6. **Crear Pull Request en GitHub hacia `main`** para que Maykel revise y fusione.

---

## IMPORTANTE: Reglas para no romper el proyecto

- NO modificar `PantryViewModel.kt` sin coordinar con Maykel.
- NO cambiar las firmas de `registerUser()`, `loginUser()`, `publishSurplusBag()`.
- NO modificar los modelos en `domain/model/` sin coordinar con el equipo.
- SIEMPRE trabajar en tu propia rama asignada, nunca hacer push directo a `main`.
- Hacer commits atómicos y descriptivos siguiendo Conventional Commits (`feat:`, `fix:`, `docs:`).

---

## Cuentas de prueba para testear

| Correo | Contraseña | Rol | Categoría |
|--------|------------|-----|-----------|
| maykel@universidad.edu.pe | Password123* | Cliente | - |
| maykelcb095@gmail.com | Password123* | Cliente | - |
| labaguette@gmail.com | Password123* | Comercio | Panadería |
| saborcriollo@gmail.com | Password123* | Comercio | Restaurante |

---

## Funcionalidades Implementadas

### Parte 1 (Maykel - `main`):
- Registro con validación estricta (correo, nombre min 3 letras, contraseña segura).
- Selección obligatoria de rubro al registrar comercio (Panadería, Restaurante, Cafetería, Pastelería).
- Catálogo propio del comercio: agregar productos libremente y control de stock.
- Publicar Producto Individual y Publicar Pack de Hoy (60-75% de descuento).
- Contenedor de navegación dinámico por rol (`AppNavigation.kt`).

### Parte 4 (Anderson - `feature/shopping-list-alerts`):
- Módulo interactivo de Lista de Compras (`ShoppingListScreen.kt`) con checkbox reactivo.
- Traslado automatizado de compras completadas a la despensa virtual.
- Semáforo visual de caducidad (`ExpirationBadge.kt`: 🟢 >7d, 🟡 ≤3d, 🔴 vencido).
- Diálogo modal de confirmación destructiva (`ConfirmDeleteDialog.kt`).

---

## Estructura de Paquetes

```
app/src/main/java/com/example/dispensave/
  data/
    local/                 AppDatabase, ProductDao, ProductEntity (Parte 3 - Luis)
    remote/                Retrofit client, API service, DTOs (Parte 2 - Rubén)
    repository/            RescueRepository, UserRepository, PantryRepository
  domain/model/            User, SurplusBag, Order, MerchantProduct, ShoppingItem
  ui/
    auth/                  LoginScreen, RegisterScreen
    components/            ExpirationBadge, ConfirmDeleteDialog
    home/                  ExploreScreen (cliente)
    merchant/              StoreDashboardScreen (comercio)
    navigation/            AppNavigation
    onboarding/            OnboardingScreen
    orders/                MyOrdersScreen (adaptado por rol)
    impact/                ImpactScreen
    pantry/                PantryScreen, PantryViewModel
    screens/shopping/      ShoppingListScreen
  util/                    ValidationUtils
```
