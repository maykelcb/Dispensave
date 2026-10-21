package com.example.dispensave.ui.screens.shopping

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dispensave.domain.model.ShoppingItem
import com.example.dispensave.ui.components.ConfirmDeleteDialog
import com.example.dispensave.ui.pantry.PantryViewModel
import com.example.dispensave.ui.theme.GreenPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    viewModel: PantryViewModel,
    onBackClick: () -> Unit = {},
    onGoToPantryClick: () -> Unit = {}
) {
    val shoppingItems by viewModel.shoppingItems.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<ShoppingItem?>(null) }
    var selectedFilter by remember { mutableStateOf("Todos") } // Todos, Pendientes, Comprados

    val pendingCount = shoppingItems.count { !it.isPurchased }
    val purchasedCount = shoppingItems.count { it.isPurchased }

    val filteredList = when (selectedFilter) {
        "Pendientes" -> shoppingItems.filter { !it.isPurchased }
        "Comprados" -> shoppingItems.filter { it.isPurchased }
        else -> shoppingItems
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Lista de Compras",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "$pendingCount pendientes • $purchasedCount comprados",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = onGoToPantryClick) {
                        Icon(
                            Icons.Default.Kitchen,
                            contentDescription = "Ir a Despensa",
                            tint = GreenPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = GreenPrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar producto a la lista")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Resumen de estado rápido
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$pendingCount",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                        Text(text = "Por comprar", fontSize = 11.sp, color = Color.Gray)
                    }
                    Divider(
                        modifier = Modifier
                            .height(28.dp)
                            .width(1.dp),
                        color = Color(0xFFE0E0E0)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$purchasedCount",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        )
                        Text(text = "Comprados", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filtros de pestaña
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Todos", "Pendientes", "Comprados").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color.DarkGray
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contenido de la lista
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(54.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedFilter == "Todos") "Tu lista de compras está vacía"
                            else "No hay productos en esta sección",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Presiona el botón (+) para agregar insumos faltantes",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        ShoppingItemRow(
                            item = item,
                            onTogglePurchased = {
                                val wasPurchased = item.isPurchased
                                viewModel.toggleShoppingItemPurchased(item)
                                if (!wasPurchased) {
                                    // Al marcarlo comprado, se traslada automáticamente a la despensa
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "¡'${item.name}' marcado como comprado y trasladado a tu Despensa!",
                                            actionLabel = "OK",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            },
                            onDeleteClick = {
                                itemToDelete = item
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal de confirmación para eliminar
    itemToDelete?.let { item ->
        ConfirmDeleteDialog(
            show = true,
            itemName = item.name,
            title = "Eliminar de la lista",
            message = "¿Deseas eliminar '${item.name}' de tu lista de compras?",
            onConfirm = {
                viewModel.deleteShoppingItem(item)
                itemToDelete = null
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "'${item.name}' eliminado de la lista.",
                        duration = SnackbarDuration.Short
                    )
                }
            },
            onDismiss = { itemToDelete = null }
        )
    }

    // Diálogo para agregar producto
    if (showAddDialog) {
        AddShoppingItemDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, category, quantity, unit ->
                viewModel.addShoppingItem(name, category, quantity, unit)
                showAddDialog = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Producto añadido a la lista de compras",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        )
    }
}

@Composable
fun ShoppingItemRow(
    item: ShoppingItem,
    onTogglePurchased: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isPurchased) Color(0xFFF1F8E9) else Color.White
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox de marcado rápido
            Checkbox(
                checked = item.isPurchased,
                onCheckedChange = { onTogglePurchased() },
                colors = CheckboxDefaults.colors(
                    checkedColor = GreenPrimary,
                    uncheckedColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (item.isPurchased) Color.Gray else Color.Black
                )
                Text(
                    text = "${item.category} • ${item.quantity} ${item.unit}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                if (item.isPurchased) {
                    Text(
                        text = "✓ Trasladado a Despensa (vigente por ${item.defaultShelfDays} días)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                }
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFD32F2F)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShoppingItemDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, category: String, quantity: Int, unit: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Abarrotes") }
    var quantityText by remember { mutableStateOf("1") }
    var selectedUnit by remember { mutableStateOf("unidad") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Frescos", "Lácteos", "Abarrotes", "Carnes", "Bebidas")
    val units = listOf("unidad", "kg", "litro", "paquete", "botella")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Agregar a Lista de Compras",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    label = { Text("Nombre del insumo") },
                    placeholder = { Text("Ej. Leche entera, Manzanas...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = errorMessage != null
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("Categoría:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Cantidad") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = selectedUnit,
                        onValueChange = { selectedUnit = it },
                        label = { Text("Unidad") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmed = name.trim()
                    if (trimmed.length < 2) {
                        errorMessage = "El nombre debe tener al menos 2 caracteres"
                        return@Button
                    }
                    val qty = quantityText.toIntOrNull() ?: 1
                    onAdd(trimmed, selectedCategory, qty, selectedUnit.trim().ifEmpty { "unidad" })
                },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
