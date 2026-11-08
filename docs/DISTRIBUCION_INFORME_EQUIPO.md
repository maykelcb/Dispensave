# 📋 PLAN MAESTRO DE DISTRIBUCIÓN COLABORATIVA — EVALUACIÓN FINAL
## Proyecto DispenSave — Aplicación Móvil Funcional e Informe Final

---

> [!CAUTION]
> ### ⚠️ REGLA ESTRICTA DE ENTREGABLES (Penalización de -2 puntos por cada caso):
> Los archivos finales que se suben al aula virtual deben nombrarse **EXACTAMENTE** así:
> 1. `PC_informe.pdf` *(Informe técnico consolidado en PDF)*
> 2. `PC_proyecto.zip` *(Proyecto de Android Studio comprimido en ZIP)*
> 3. `PC_app.aab` *(Android App Bundle firmado para Google Play)*

---

### DATOS GENERALES DEL PROYECTO
- **Proyecto:** DispenSave — Plataforma Inteligente de Rescate de Alimentos y Gestión de Despensa
- **Repositorio Oficial en GitHub:** [https://github.com/maykelcb/DispenSave](https://github.com/maykelcb/DispenSave)
- **Líder Técnico e Integrador:** Maykel Bernaola (`maykelcb`)
- **Estructura del Equipo:** 4 Integrantes con roles equitativos, ramas dedicadas y secciones asignadas según la rúbrica oficial de 12 puntos.

---

## 1. TABLA MAESTRA DE ASIGNACIÓN (ROLES, GITHUB Y SECCIONES)

| Parte | Integrante Asignado | Rol Oficial | Rama Oficial de Git | Secciones Asignadas del Informe Oficial | Módulos Técnicos y Entregables | Archivo de Trabajo |
|:---:|:---|:---|:---:|:---|:---|:---:|
| **PARTE 1** | **Maykel Bernaola** (`maykelcb`) | **Líder Técnico & Arquitecto de Software** | `main` | **Portada Oficial**<br>**1. Resumen ejecutivo**<br>• Descripción del proyecto<br>• Problema abordado<br>• Solución desarrollada<br>• Resultados obtenidos<br>**2. Definición del proyecto**<br>• Contexto y problemática<br>• Objetivos generales y específicos<br>• Alcance final<br>• Usuarios objetivo (Consumidor vs Comercio)<br>**4. Arquitectura de software**<br>• Arquitectura MVVM con UDF<br>• Componentes implementados<br>• Diagrama oficial de arquitectura (Draw.io / PNG)<br>**Sustentación:** Coordinación y guion de demo en vivo (10-15 min). | • Arquitectura base y navegación<br>• Integración de ramas a `main`<br>• Generación de `PC_app.aab`<br>• Empaquetado de `PC_proyecto.zip`<br>• Consolidación de `PC_informe.pdf` | [docs/documento_parte1_dispensave.md](documento_parte1_dispensave.md) |
| **PARTE 2** | **Integrante 2** *(Rubén / APIs)* | **Desarrollador de Análisis, Diseño y Servicios Web** | `feature/retrofit-api-consumption` | **3. Análisis y diseño**<br>• Historias de usuario (HU01-HU16 formato Gherkin)<br>• Requerimientos funcionales (RF01-RF12)<br>• Requerimientos no funcionales (RNF01-RNF06)<br>• Reglas de negocio (RN01-RN05)<br>**5. Implementación técnica**<br>• Persistencia local (Room SQLite nativo)<br>• Servicios REST (Retrofit 2 + OpenFoodFacts)<br>• Firebase (Justificación técnica Offline-First)<br>• Geolocalización (Google Maps API y tiendas) | • `FoodApiService.kt`<br>• `RetrofitClient.kt`<br>• `OpenFoodFactsDto.kt`<br>• `ProductSearchScreen.kt`<br>• Especificación formal de requisitos de software | [docs/parte2_ruben.md](parte2_ruben.md) |
| **PARTE 3** | **Integrante 3** *(Luis / Persistencia)* | **Desarrollador de Persistencia, Pruebas y Calidad** | `feature/room-crud-pantry` | **6. Evidencias funcionales**<br>• Capturas de principales pantallas<br>• Evidencias de funcionalidades implementadas<br>• Evidencias de operaciones CRUD completas<br>**7. Pruebas realizadas**<br>• Pruebas funcionales (Casos de Prueba CP01-CP08)<br>• Correcciones realizadas (Problemas y Soluciones)<br>**8. Calidad y optimización**<br>• Mejoras de rendimiento (Recomposición Compose)<br>• Optimización de recursos (Consultas Room, Red)<br>• Buenas prácticas aplicadas (SOLID, Clean Arch)<br>• Consideraciones de accesibilidad (Contraste, 48dp) | • `AppDatabase.kt`<br>• `ProductDao.kt`<br>• `ProductEntity.kt`<br>• `PantryRepository.kt`<br>• Matriz de Casos de Prueba<br>• Auditoría de Rendimiento y Accesibilidad | [docs/parte3_luis.md](parte3_luis.md) |
| **PARTE 4** | **Integrante 4** *(Anderson Abanto / Andy)* | **Desarrollador de Publicación, UX y Sostenibilidad** | `feature/shopping-list-alerts` | **9. Preparación para publicación**<br>• Firma digital y Android App Bundle (`PC_app.aab`)<br>• Ícono de app y capturas promocionales<br>• Política de privacidad y Ficha de tienda Play Store<br>• Notas de versión v1.0.0<br>**10. Impacto social y ODS 12**<br>• Contribución ODS 12.3, Recursos y Ética<br>**11. Conclusiones y recomendaciones**<br>**12. Anexos**<br>• Enlace al repositorio GitHub<br>• **Manual de usuario breve** (guía paso a paso ilustrada)<br>• Evidencias complementarias | • `ShoppingListScreen.kt`<br>• `ExpirationBadge.kt`<br>• `ImpactScreen.kt`<br>• Ficha de Play Store<br>• Política de Privacidad Oficial<br>• Manual de Usuario Ilustrado | [docs/parte4_andy.md](parte4_andy.md) |

---

## 2. GUÍA DE TRABAJO EN GITHUB POR INTEGRANTE

Para trabajar de manera colaborativa y ordenada:

```bash
# 1. Clonar o actualizar el repositorio
git clone https://github.com/maykelcb/DispenSave.git
cd DispenSave

# PARTE 1 — Maykel Bernaola (Líder Técnico & Integrador):
git checkout main
git pull origin main

# PARTE 2 — Integrante 2 (Análisis, Diseño & APIs):
git checkout feature/retrofit-api-consumption
git pull origin feature/retrofit-api-consumption

# PARTE 3 — Integrante 3 (Persistencia, Pruebas & Calidad):
git checkout feature/room-crud-pantry
git pull origin feature/room-crud-pantry

# PARTE 4 — Integrante 4 (Publicación, ODS 12 & Manual de Usuario):
git checkout feature/shopping-list-alerts
git pull origin feature/shopping-list-alerts
```

---

## 3. CHECKLIST DE ENTREGABLES PARA CADA INTEGRANTE

### Parte 1 — Maykel Bernaola (`main`)
- [x] Portada formal con datos de la universidad, asignatura, proyecto e integrantes.
- [x] **Sección 1: Resumen ejecutivo** (Problema, Solución y Resultados).
- [x] **Sección 2: Definición del proyecto** (Contexto, Objetivos, Alcance y Usuarios objetivo).
- [x] **Sección 4: Arquitectura de software** (MVVM con UDF, Componentes y Diagrama Draw.io/PNG).
- [ ] Validar la compilación del bundle `PC_app.aab` y el comprimido `PC_proyecto.zip`.

### Parte 2 — Integrante 2 (`feature/retrofit-api-consumption`)
- [x] **Sección 3: Análisis y diseño** (Historias de usuario HU01-HU16 Gherkin, Requerimientos Funcionales RF01-RF12, Requerimientos No Funcionales RNF01-RNF06 y Reglas de Negocio RN01-RN05).
- [x] **Sección 5: Implementación técnica** (Room SQLite, Retrofit 2 + OpenFoodFacts REST, justificación técnica frente a Firebase y Google Maps).

### Parte 3 — Integrante 3 (`feature/room-crud-pantry`)
- [x] **Sección 6: Evidencias funcionales** (Capturas de pantallas principales, operaciones CRUD demostradas y semáforo visual).
- [x] **Sección 7: Pruebas realizadas** (Matriz de Casos de Prueba CP01-CP08 y tabla de problemas/soluciones).
- [x] **Sección 8: Calidad y optimización** (Smart Recomposition, optimización de SQLite, Clean Architecture y Accesibilidad Material 3).

### Parte 4 — Integrante 4 (`feature/shopping-list-alerts`)
- [x] **Sección 9: Preparación para publicación** (Firma digital, Android App Bundle `PC_app.aab`, Ficha de tienda Google Play Store, Política de privacidad y Release Notes).
- [x] **Sección 10: Impacto social y ODS 12** (Contribución directa al ODS 12.3, cálculo de CO₂ en `ImpactScreen`, ética y consumo responsable).
- [x] **Sección 11: Conclusiones y recomendaciones** (Conclusiones de ingeniería de software y recomendaciones de evolución).
- [x] **Sección 12: Anexos** (Enlace al repositorio GitHub, **Manual de Usuario Breve** con flujo paso a paso y evidencias complementarias).
