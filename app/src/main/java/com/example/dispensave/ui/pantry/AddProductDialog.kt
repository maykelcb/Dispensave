package com.example.dispensave.ui.pantry

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dispensave.ui.theme.AlertOrange
import com.example.dispensave.ui.theme.GreenPrimary
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddProductDialog(
    viewModel: PantryViewModel,
    onDismiss: () -> Unit,
    onAdd: (String, String, Long, Int, String) -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Abarrotes") }
    // Fecha inicial por defecto: dentro de 3 días
    var expirationDate by remember {
        mutableLongStateOf(System.currentTimeMillis() + (3L * 24 * 60 * 60 * 1000))
    }
    var quantityText by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf("unidad") }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }

    val categories = listOf("Abarrotes", "Lácteos", "Frescos", "Carnes", "Bebidas", "Snacks")
    val units = listOf("unidad", "kg", "g", "litros", "paquete", "lata")

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    val nameError = viewModel.validateProductName(name)
    val isNameErrorVisible = (hasAttemptedSubmit || name.isNotEmpty()) && nameError != null

    val qtyParsed = quantityText.toIntOrNull()
    val quantityError = viewModel.validateQuantity(qtyParsed)
    val isQuantityErrorVisible = (hasAttemptedSubmit || quantityText.isNotEmpty()) && quantityError != null

    val isExpired = viewModel.isDateExpired(expirationDate)
    val expirationWarning = viewModel.getExpirationWarning(expirationDate)

    val calendar = Calendar.getInstance().apply { timeInMillis = expirationDate }
    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 23, 59, 59)
                }
                expirationDate = cal.timeInMillis
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Agregar a la Despensa",
                fontWeight = FontWeight.Bold,
                color = GreenPrimary,
                fontSize = 19.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Validación de Nombre del Producto
                // Regla: No se permite registrar productos con nombres vacíos o menores a 2 caracteres.
                // Mensaje: "El nombre del producto debe contener al menos 2 caracteres"
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del alimento") },
                    placeholder = { Text("Ej: Leche Gloria, Arroz, etc.") },
                    singleLine = true,
                    isError = isNameErrorVisible,
                    supportingText = {
                        if (isNameErrorVisible && nameError != null) {
                            Text(
                                text = nameError,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 11.sp
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                // Categoría
                Text(
                    text = "Categoría",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            shape = RoundedCornerShape(14.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GreenPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // 2. Validación de Fecha de Vencimiento
                // Regla: La fecha seleccionada debe ser válida y estar definida. Si la fecha es menor a la fecha actual,
                // el sistema asigna automáticamente el estado "Vencido" al producto.
                // Mensaje de advertencia: "Este producto ya está vencido, consúmelo o deséchalo pronto"
                Text(
                    text = "Fecha de Vencimiento",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF5F5F5),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isExpired) AlertOrange else Color(0xFFDCDCDC)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Fecha seleccionada:",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = dateFormat.format(Date(expirationDate)),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isExpired) AlertOrange else Color.Black
                            )
                        }
                        FilledTonalButton(
                            onClick = { datePickerDialog.show() },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Cambiar fecha",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cambiar", fontSize = 12.sp)
                        }
                    }
                }

                // Atajos rápidos de fecha para probar fácilmente las reglas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SuggestionChip(
                        onClick = {
                            // Fecha pasada (ayer) para activar la advertencia de producto vencido
                            expirationDate = System.currentTimeMillis() - (24 * 60 * 60 * 1000L)
                        },
                        label = { Text("Ayer (Vencido)", fontSize = 11.sp, color = AlertOrange) }
                    )
                    SuggestionChip(
                        onClick = {
                            expirationDate = System.currentTimeMillis()
                        },
                        label = { Text("Hoy", fontSize = 11.sp) }
                    )
                    SuggestionChip(
                        onClick = {
                            expirationDate = System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000L)
                        },
                        label = { Text("En 3 días", fontSize = 11.sp) }
                    )
                    SuggestionChip(
                        onClick = {
                            expirationDate = System.currentTimeMillis() + (15 * 24 * 60 * 60 * 1000L)
                        },
                        label = { Text("En 15 días", fontSize = 11.sp) }
                    )
                }

                // Banner de advertencia si la fecha es menor a la actual
                if (expirationWarning != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Advertencia",
                                tint = AlertOrange,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = expirationWarning,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100),
                                    lineHeight = 16.sp
                                )
                                Text(
                                    text = "Estado asignado: Vencido",
                                    fontSize = 11.sp,
                                    color = Color(0xFFEF6C00)
                                )
                            }
                        }
                    }
                }

                // 3. Validación de Cantidad y Unidad
                // Regla: La cantidad debe ser un número entero mayor a cero (> 0).
                // Mensaje de error: "La cantidad debe ser mayor a 0"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Cantidad") },
                        singleLine = true,
                        isError = isQuantityErrorVisible,
                        supportingText = {
                            if (isQuantityErrorVisible && quantityError != null) {
                                Text(
                                    text = quantityError,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unidad") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Atajos de unidades
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    units.forEach { u ->
                        SuggestionChip(
                            onClick = { unit = u },
                            label = { Text(u, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    hasAttemptedSubmit = true
                    val validation = viewModel.validateProduct(name, expirationDate, qtyParsed, unit)
                    if (validation.isValid) {
                        onAdd(name.trim(), category, expirationDate, qtyParsed!!, unit.trim().ifEmpty { "unidad" })
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        },
        shape = RoundedCornerShape(18.dp)
    )
}