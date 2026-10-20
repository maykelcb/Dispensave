> [!IMPORTANT]
> ### 📌 SECCIÓN DEL INFORME ACADÉMICO ASIGNADA A RUBÉN:
> **Secciones a redactar en el informe para Word:**
> 1. **Sección 4.4: Diseño de Interfaces** (Pantallas principales del cliente, flujo de navegación, wireframes/mockups).
> 2. **Sección V.1: Historias de Usuario HU05 a HU08** en formato tabla (Buscador/Filtros, Ficha de Detalle, Favoritos, Notificaciones).
> 3. **Sección V.2: Sprint 2 de Desarrollo** (Objetivo, funcionalidades y actividades).
> 4. **Sección VI.2: Evidencias de Implementación** (Capturas de nuevas pantallas y código de Composables).
>
> *Para ver la asignación completa del equipo, revisa [DISTRIBUCION_INFORME_EQUIPO.md](DISTRIBUCION_INFORME_EQUIPO.md).*

---

# Parte 2 - Exploracion y Experiencia del Cliente
**Responsable:** Ruben
**Rama recomendada:** git checkout -b feature/parte2-ruben

---

## Objetivo General
Mejorar la experiencia del cliente/consumidor dentro de DispenSave. Actualmente la pantalla de
exploracion (ExploreScreen.kt) funciona de forma basica. Tu trabajo es enriquecerla con imagenes
reales, busqueda, favoritos, mapa y notificaciones, haciendo que la app se sienta como una version
pulida y lista para produccion.

---

## Archivos que debes modificar

| Archivo | Que hacer |
|---------|-----------|
| ui/home/ExploreScreen.kt | Agregar barra de busqueda, imagenes, mapa de distancia, mejoras visuales |
| ui/orders/MyOrdersScreen.kt | Mejorar la vista del cliente (historial completo, calificacion) |
| data/repository/RescueRepository.kt | Agregar metodo de busqueda por nombre de local |
| ui/pantry/PantryViewModel.kt | Agregar funciones de favoritos y busqueda |

---

## Tareas Detalladas

### TAREA 2.1 - Barra de Busqueda en ExploreScreen
**Archivo:** ui/home/ExploreScreen.kt

Agrega un campo de busqueda en la parte superior de la pantalla que filtre los packs/productos
disponibles por nombre del local o titulo del pack en tiempo real.

Ejemplo de implementacion:
- Variable de estado: var textoBusqueda by remember { mutableStateOf("") }
- OutlinedTextField con leadingIcon = Icons.Default.Search
- Filtrar la lista: filteredBags.filter { it.storeName.contains(textoBusqueda, ignoreCase = true) }
- Boton "X" para limpiar la busqueda si el campo no esta vacio

---

### TAREA 2.2 - Imagenes por Categoria de Comercio
**Archivo:** ui/home/ExploreScreen.kt y res/drawable/

Agrega imagenes representativas en cada card de SurplusBagCard segun la categoria del comercio:
- Panaderia: imagen de panes/croissants
- Restaurante: imagen de plato de comida criolla
- Cafeteria: imagen de sandwich y cafe
- Pasteleria: imagen de tortas y pasteles

Puedes usar recursos de res/drawable/ o la libreria Coil para imagenes remotas (cuando Andy integre el backend).
Por ahora usa imagenes locales en drawable segun la categoria:

when (bag.category) {
    StoreCategory.PANADERIA -> R.drawable.img_panaderia
    StoreCategory.RESTAURANTE -> R.drawable.img_restaurante
    StoreCategory.CAFETERIA -> R.drawable.img_cafeteria
    StoreCategory.PASTELERIA -> R.drawable.img_pasteleria
}

---

### TAREA 2.3 - Sistema de Favoritos
**Archivos:** ui/pantry/PantryViewModel.kt, ui/home/ExploreScreen.kt

Implementa un sistema de favoritos para que el cliente pueda guardar locales que le gustan.

En PantryViewModel.kt, agrega:
- val favoriteStoreIds = mutableStateSetOf<Int>()  (o mutableStateListOf)
- fun toggleFavorito(storeId: Int) - agrega o quita de favoritos
- fun esFavorito(storeId: Int): Boolean

En ExploreScreen.kt:
- Agrega un IconButton con Icons.Default.FavoriteBorder / Icons.Default.Favorite en cada card
- Agrega un chip de filtro "Solo Favoritos" en la fila de filtros de categoria
- Filtra por favoritos cuando este activo

En SharedPreferences (para persistir entre sesiones):
- Guarda la lista de IDs favoritos como JSON en UserRepository o en un nuevo FavoritesRepository

---

### TAREA 2.4 - Vista de Detalle Expandida antes de Reservar
**Archivos:** ui/product/ProductDetailScreen.kt (o nuevo archivo ui/home/DetalleOfertaScreen.kt)

Cuando el cliente toca una card, antes de mostrar el dialogo de confirmacion directamente,
muestra una pantalla completa de detalle con:
- Imagen grande del local/producto
- Nombre del local y calificacion con estrellas
- Lista completa de productos incluidos (si es pack)
- Descripcion completa
- Mapa pequeno mostrando la ubicacion (usa Google Maps Static API o un placeholder)
- Precio original tachado + precio rescate + porcentaje de descuento en grande
- Franja horaria de recojo resaltada
- Boton "Reservar este Rescate" en la parte inferior

Para navegar a esta pantalla, necesitaras agregar una ruta en AppNavigation.kt:
composable("detalle/{bagId}") { backStackEntry ->
    val bagId = backStackEntry.arguments?.getString("bagId")?.toIntOrNull()
    // mostrar detalle
}

---

### TAREA 2.5 - Historial de Rescates del Cliente (MyOrdersScreen mejorado)
**Archivo:** ui/orders/MyOrdersScreen.kt

La vista del cliente ya existe pero mejorarla:
- Separar en dos secciones: "Pendientes de Recojo" y "Rescates Completados"
- En completados, agregar opcion de calificar el local (1 a 5 estrellas) - guardar en ViewModel
- Mostrar resumen estadistico en la parte superior:
  - Total de rescates realizados
  - Dinero total ahorrado (suma de originalPrice - rescuePrice)
  - Mensaje motivacional segun cantidad (ej: "Has rescatado 5 packs!")

---

### TAREA 2.6 - Notificaciones Locales
**Archivo:** nuevo archivo util/NotificacionesLocales.kt

Implementa notificaciones locales usando WorkManager o AlarmManager para:
- Recordatorio diario a las 18:00: "Hay packs disponibles cerca de ti hoy!"
- Notificacion cuando un rescate reservado esta proximo a su franja horaria de recojo

Para WorkManager agrega en build.gradle (app):
  implementation "androidx.work:work-runtime-ktx:2.9.0"

---

### Criterios de Aceptacion (Revision de Maykel)
- [ ] La barra de busqueda filtra correctamente por nombre de local y titulo
- [ ] Las imagenes se muestran segun la categoria del comercio
- [ ] El icono de favorito cambia de estado al tocarlo y persiste al reabrir la app
- [ ] La pantalla de detalle muestra toda la informacion antes de reservar
- [ ] MyOrdersScreen separa pendientes de completados
- [ ] Al menos 1 tipo de notificacion local funciona correctamente

---

### Cuentas de prueba para testear
- Cliente: maykel@universidad.edu.pe / Password123*
- Hay 5 ofertas precargadas (2 packs de panaderia, 1 restaurante, 1 cafeteria, 1 pasteleria)

### Como integrar tu trabajo
1. git checkout -b feature/parte2-ruben
2. Hacer tus cambios
3. git add -A && git commit -m "feat(parte2): [descripcion de tu cambio]"
4. git push origin feature/parte2-ruben
5. Crear Pull Request hacia main en GitHub
6. Avisar a Maykel para revision
