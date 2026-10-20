> [!IMPORTANT]
> ### 📌 SECCIÓN DEL INFORME ACADÉMICO ASIGNADA A LUIS:
> **Secciones a redactar en el informe para Word:**
> 1. **Sección 4.3: Diseño de Base de Datos** (Modelo Entidad-Relación, Diccionario de datos de usuarios, ofertas, pedidos, catálogo e impacto, y justificación de Room).
> 2. **Sección V.1: Historias de Usuario HU09 a HU12** en formato tabla (Cálculo de CO₂/Agua/Dinero, Gamificación e insignias, Historial de pedidos, Compartir en redes).
> 3. **Sección V.2: Sprint 3 de Desarrollo** (Objetivo, funcionalidades y actividades).
> 4. **Sección VI.3: Evidencias de Implementación** (Código de entidades Room, DAOs y capturas de métricas).
>
> *Para ver la asignación completa del equipo, revisa [DISTRIBUCION_INFORME_EQUIPO.md](DISTRIBUCION_INFORME_EQUIPO.md).*

---

# Parte 3 - Impacto CO2 y Estadisticas de Rescate
**Responsable:** Luis
**Rama recomendada:** git checkout -b feature/parte3-luis

---

## Objetivo General
Crear el modulo de Impacto Medioambiental de DispenSave. Esta pantalla debe mostrar al usuario
(tanto cliente como comercio) el impacto positivo que han generado al rescatar o publicar comida,
usando datos de CO2 evitado, agua ahorrada, dinero rescatado y logros desbloqueados.
Debe ser visualmente impactante y motivacional.

---

## Archivos que debes modificar/crear

| Archivo | Que hacer |
|---------|-----------|
| ui/impact/ImpactScreen.kt | Reforma completa con estadisticas reales, graficas y logros |
| ui/pantry/PantryViewModel.kt | Agregar calculos de impacto (no tocar la logica existente) |
| domain/model/ImpactoEstadisticas.kt | Nuevo modelo con los datos de impacto |

---

## Formulas de Calculo de Impacto (Estandar internacional)

Por cada comida rescatada:
- CO2 evitado: 2.5 kg de CO2 equivalente
- Agua ahorrada: 1,000 litros de agua
- Equivalente arboles: 1 comida = 0.1 arbol plantado (aprox)

Por el dinero:
- Ahorro del cliente: originalPrice - rescuePrice (por cada rescate)
- Ingresos del comercio: suma de rescuePrice de todos sus pedidos entregados

---

## Tareas Detalladas

### TAREA 3.1 - Nuevo Modelo de Estadisticas
**Archivo nuevo:** domain/model/ImpactoEstadisticas.kt

Crea un data class con:

data class ImpactoEstadisticas(
    val totalRescates: Int,
    val co2EvitadoKg: Double,       // totalRescates * 2.5
    val aguaAhorradaLitros: Int,    // totalRescates * 1000
    val arbolesEquivalentes: Double, // totalRescates * 0.1
    val dineroAhorradoTotal: Double, // suma de (originalPrice - rescuePrice) de todos los pedidos
    val nivelRescatista: String,     // segun totalRescates: Principiante, Rescatista, Heroe, Leyenda
    val porcentajeAlSiguienteNivel: Int // 0-100
)

Niveles:
- 0-2 rescates: "Principiante Verde"
- 3-9 rescates: "Rescatista Activo"
- 10-24 rescates: "Heroe del Planeta"
- 25+ rescates: "Leyenda DispenSave"

---

### TAREA 3.2 - Funcion de Calculo en ViewModel
**Archivo:** ui/pantry/PantryViewModel.kt

IMPORTANTE: No elimines ni modifiques funciones existentes. Solo AGREGA al final de la clase:

fun calcularImpacto(): ImpactoEstadisticas {
    val pedidosCompletos = orders.value.filter { it.status.name == "RECOGIDO" }
    val totalRescates = pedidosCompletos.size
    // calcular dinero ahorrado necesita acceso a los bags para ver originalPrice
    // puedes obtenerlo de surplusBags.value filtrando por bagId
    val dineroAhorrado = pedidosCompletos.sumOf { order ->
        val bag = surplusBags.value.firstOrNull { it.id == order.bagId }
        if (bag != null) bag.originalPrice - bag.rescuePrice else 0.0
    }
    val nivel = when {
        totalRescates >= 25 -> "Leyenda DispenSave"
        totalRescates >= 10 -> "Heroe del Planeta"
        totalRescates >= 3  -> "Rescatista Activo"
        else -> "Principiante Verde"
    }
    val porcentaje = when {
        totalRescates >= 25 -> 100
        totalRescates >= 10 -> ((totalRescates - 10) * 100 / 15)
        totalRescates >= 3  -> ((totalRescates - 3) * 100 / 7)
        else -> (totalRescates * 100 / 3)
    }
    return ImpactoEstadisticas(
        totalRescates = totalRescates,
        co2EvitadoKg = totalRescates * 2.5,
        aguaAhorradaLitros = totalRescates * 1000,
        arbolesEquivalentes = totalRescates * 0.1,
        dineroAhorradoTotal = dineroAhorrado,
        nivelRescatista = nivel,
        porcentajeAlSiguienteNivel = porcentaje
    )
}

---

### TAREA 3.3 - Reforma Completa de ImpactScreen
**Archivo:** ui/impact/ImpactScreen.kt

La pantalla actual es un placeholder. Implementa una pantalla completa con:

**Seccion 1 - Tarjeta de Nivel del Rescatista:**
- Fondo degradado verde oscuro a verde claro
- Emoji y nombre del nivel (ej: "🌿 Rescatista Activo")
- Barra de progreso animada al siguiente nivel
- Texto: "Te faltan X rescates para ser Heroe del Planeta"
- Firma: "DispenSave - Rescue Food, Save the Planet"

**Seccion 2 - Estadisticas Visuales (4 tarjetas):**
- Tarjeta CO2: numero grande animado en kg + icono nube
- Tarjeta Agua: litros ahorrados + icono gota
- Tarjeta Arboles: equivalente en arboles + icono arbol
- Tarjeta Dinero: S/. ahorrado + icono moneda

Usa animaciones de contador (animateFloatAsState) para que los numeros "cuenten" al abrir la pantalla.

**Seccion 3 - Logros / Insignias:**
Muestra una grilla de insignias (algunas desbloqueadas, otras en gris/bloqueadas):
- Primer Rescate: desbloqueado al primer rescate
- Rescatador x5: desbloqueado al quinto rescate
- Semana Activa: 3 rescates en una misma semana
- Diversificado: rescates de al menos 3 categorias distintas
- Panadero Honorario: 3 rescates de panaderias
- Eco Guerrero: 10 kg de CO2 evitado
- Leyenda: 25+ rescates

**Seccion 4 - Datos Globales de DispenSave (datos simulados):**
- "La comunidad DispenSave ha rescatado 15,234 comidas este mes"
- "Equivalente a plantar 1,523 arboles"
- Barra de progreso hacia meta mensual

**Seccion 5 - Mensaje de Motivacion:**
- Frase motivacional segun el nivel del usuario
- Boton "Compartir mi Impacto" (usa shareIntent de Android para compartir como texto)

---

### TAREA 3.4 - Vista de Impacto para el Comercio
El comercio tambien deberia ver sus estadisticas de impacto desde el tab "Impacto CO2":
- Total de packs publicados y entregados
- CO2 evitado gracias a su establecimiento
- Total de ingresos por rescates
- Comparativo con el promedio de comercios DispenSave

Detectar el rol en ImpactScreen:
- Si viewModel.currentUserRole == UserRole.COMERCIO, mostrar estadisticas de comercio
- Si es CLIENTE, mostrar estadisticas de rescates del cliente

---

### Criterios de Aceptacion (Revision de Maykel)
- [ ] Los calculos de CO2, agua y arboles son correctos segun las formulas
- [ ] La barra de progreso de nivel se anima correctamente
- [ ] Los contadores de numeros tienen animacion de entrada
- [ ] Al menos 5 insignias implementadas (desbloqueadas y bloqueadas)
- [ ] El boton de compartir abre el selector de apps del sistema
- [ ] La vista cambia segun el rol (cliente vs comercio)

---

### Como integrar tu trabajo
1. git checkout -b feature/parte3-luis
2. Hacer tus cambios
3. git add -A && git commit -m "feat(parte3): [descripcion]"
4. git push origin feature/parte3-luis
5. Crear Pull Request hacia main
6. Avisar a Maykel para revision
