> [!IMPORTANT]
> ### 📋 SECCIÓN DEL INFORME ACADÉMICO ASIGNADA A ANDERSON (ANDY):
> **Secciones a redactar en el informe para Word:**
> 1. **Sección 4.4: Diseño de Interfaces y Experiencia de Usuario** (Wireflows del flujo de compras, prototipo de alta fidelidad y evaluación de Heurísticas de Usabilidad de Nielsen).
> 2. **Sección V.1: Historias de Usuario HU13 a HU16** en formato tabla (Lista de compras interactiva, traslado automático a despensa, semáforo visual de caducidad y confirmación destructiva).
> 3. **Sección V.2: Sprint 4 de Desarrollo** (Objetivo, cuadro WBS de actividades y tablero Kanban).
> 4. **Sección VI.4: Evidencias de Implementación** (Capturas de `ShoppingListScreen`, badges semafóricos y diálogos de confirmación).
>
> *Documento académico detallado listo para copiar a Word: [docs/parte4_shopping_alerts.md](parte4_shopping_alerts.md).*

---

# Parte 4 — Lista de Compras, Alertas Semafóricas y UX
**Responsable:** Anderson Abanto (`Anderson08102001` / Andy)  
**Rama Oficial en GitHub:** `feature/shopping-list-alerts`  
**Estado:** **IMPLEMENTADO Y SUBIDO A GITHUB** (Commits `99474ee`, `d5a0f4b`, `8fa80c3`, `b04fb3c`)

---

## Resumen del Trabajo Implementado por Anderson

Anderson ya completó la implementación técnica en su rama `feature/shopping-list-alerts`:

1. **`ShoppingItem.kt` (`domain/model/`):**
   - Modelo de dominio para insumos de compra con cantidad, unidad y estado `isPurchased`.

2. **`ShoppingListScreen.kt` (`ui/screens/shopping/`):**
   - Pantalla interactiva con checkboxes animados, filtros por categoría, resumen de compras y snackbars de confirmación.

3. **`ExpirationBadge.kt` (`ui/components/`):**
   - Sistema de semaforización cromática para fechas de caducidad:
     - 🟢 **Vigente:** > 7 días
     - 🟡 **Por Vencer:** ≤ 3 días
     - 🔴 **Vencido:** fecha expirada

4. **`ConfirmDeleteDialog.kt` (`ui/components/`):**
   - Diálogo modal bajo directrices de Material Design 3 para prevenir borrados accidentales de alimentos.

5. **`PantryViewModel.kt` (`ui/pantry/`):**
   - Integración de `toggleShoppingItemPurchased()` que traslada automáticamente los productos comprados al inventario de la despensa.

6. **`AppNavigation.kt` (`ui/navigation/`):**
   - Enlace de la ruta `ItemNavInferior.Compras` en la navegación inferior del cliente.

7. **Documentación Formal de la Entrega:**
   - [docs/parte4_shopping_alerts.md](parte4_shopping_alerts.md): Capítulos 4.4, 5.1 y 5.2 listos con Wireflows, Heurísticas de Nielsen y Sprint Backlog.
