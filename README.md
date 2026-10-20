# DispenSave

> Plataforma de rescate de comida al estilo Too Good To Go para el mercado peruano.

**DispenSave** conecta a restaurantes, panaderias, cafeterias y pastelerias locales con consumidores
que quieren adquirir excedentes de comida fresca a precios de rescate (60-75% de descuento),
reduciendo el desperdicio alimentario de forma practica y sostenible.

---

## Equipo de Desarrollo

| Parte | Responsable | Estado | Documentacion |
|-------|-------------|--------|---------------|
| Parte 1 | Maykel  | COMPLETADO | [docs/documento_parte1_dispensave.md](docs/documento_parte1_dispensave.md) |
| Parte 2 | Ruben   | PENDIENTE  | [docs/parte2_ruben.md](docs/parte2_ruben.md) |
| Parte 3 | Luis    | PENDIENTE  | [docs/parte3_luis.md](docs/parte3_luis.md) |
| Parte 4 | Andy    | PENDIENTE  | [docs/parte4_andy.md](docs/parte4_andy.md) |

---

## Como empezar (para Ruben, Luis y Andy)

1. Clona el repositorio:
   git clone https://github.com/maykelcb/DispenSave.git

2. Abre en Android Studio (version Hedgehog o superior recomendada)

3. Lee TU documentacion especifica antes de tocar cualquier codigo:
   - Ruben: docs/parte2_ruben.md
   - Luis:  docs/parte3_luis.md
   - Andy:  docs/parte4_andy.md

4. Crea tu propia rama de trabajo:
   git checkout -b feature/parte2-ruben      (Ruben)
   git checkout -b feature/parte3-luis       (Luis)
   git checkout -b feature/parte4-andy       (Andy)

5. Haz tus cambios, commitea frecuentemente y sube tu rama:
   git add -A
   git commit -m "feat(parte2): descripcion del cambio"
   git push origin feature/parte2-ruben

6. Cuando termines, crea un Pull Request en GitHub hacia main y avisa a Maykel

---

## IMPORTANTE: Reglas para no romper el proyecto

- NO modificar PantryViewModel.kt sin coordinar con Maykel
- NO cambiar las firmas de registerUser(), loginUser(), publishSurplusBag()
- NO modificar los modelos en domain/model/ sin coordinar con todo el equipo
- SIEMPRE trabajar en tu propia rama, nunca pushear directo a main
- Hacer commits pequenos y descriptivos (no un solo commit con todo)

---

## Cuentas de prueba para testear

| Correo | Contrasena | Rol | Categoria |
|--------|-----------|-----|-----------|
| maykel@universidad.edu.pe | Password123* | Cliente | - |
| maykelcb095@gmail.com | Password123* | Cliente | - |
| labaguette@gmail.com | Password123* | Comercio | Panaderia |
| saborcriollo@gmail.com | Password123* | Comercio | Restaurante |

---

## Funcionalidades de Parte 1 (COMPLETADO)

- Registro con validacion estricta (correo real, nombre min 3 letras, contrasena segura)
- Seleccion obligatoria de rubro al registrar comercio (Panaderia / Restaurante / Cafeteria / Pasteleria)
- Catalogo propio del comercio: agrega productos libremente, control de stock con +/-
- Publicar Producto Individual: selecciona 1 producto del catalogo
- Publicar Pack de Hoy: multi-seleccion con mayor descuento (60-75%)
- Validacion numerica estricta en precios y cantidades
- Franjas horarias predefinidas (chips, sin texto libre)
- Vista del cliente: packs con contenido visible, badge de descuento, precio original tachado
- Pedidos de clientes en el panel del comercio con confirmacion de entrega
- Pantalla de pedidos adaptada segun rol (cliente vs comercio)

---

## Estructura del Proyecto

app/src/main/java/com/example/dispensave/
  data/repository/         RescueRepository, UserRepository, PantryRepository
  domain/model/            User, SurplusBag, Order, MerchantProduct, etc.
  ui/
    auth/                  LoginScreen, RegisterScreen
    home/                  ExploreScreen (cliente)
    merchant/              StoreDashboardScreen (comercio)
    navigation/            AppNavigation
    onboarding/            OnboardingScreen
    orders/                MyOrdersScreen (adaptado por rol)
    impact/                ImpactScreen
    pantry/                PantryScreen, PantryViewModel
  util/ValidationUtils.kt
