package com.example.dispensave.ui.merchant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dispensave.domain.model.MerchantProduct
import com.example.dispensave.domain.model.OfferType
import com.example.dispensave.domain.model.StoreCategory
import com.example.dispensave.ui.pantry.PantryViewModel
import com.example.dispensave.ui.theme.AlertRed
import com.example.dispensave.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDashboardScreen(
    viewModel: PantryViewModel,
    onLogoutClick: () -> Unit
) {
    val bags by viewModel.surplusBags.collectAsState()
    val allOrders by viewModel.orders.collectAsState()
    val merchantInventory = viewModel.merchantInventory

    var selectedTab by remember { mutableStateOf(0) }
    var showIndividualDialog by remember { mutableStateOf(false) }
    var showPackDialog by remember { mutableStateOf(false) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var productToEditStock by remember { mutableStateOf<MerchantProduct?>(null) }

    val merchantCategory = viewModel.currentStoreCategory ?: StoreCategory.PANADERIA
    val businessName = viewModel.currentStoreName ?: viewModel.userName
    val myStoreName = viewModel.currentStoreName ?: viewModel.userName
    val merchantOrders = allOrders.filter { it.storeName.equals(myStoreName, ignoreCase = true) }

    val defaultPickupSlots = listOf("18:00 - 19:30", "19:00 - 20:30", "20:00 - 21:00", "21:00 - 22:00")

    fun sanitizeDecimal(input: String): String {
        val filtered = input.filter { it.isDigit() || it == '.' }
        val parts = filtered.split('.')
        return when {
            parts.size == 1 -> filtered
            else -> parts[0] + "." + parts[1].take(2)
        }
    }
    fun sanitizeInteger(input: String): String = input.filter { it.isDigit() }.take(4)

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))
    ) {
        // Cabecera del Comercio
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(businessName, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(color = GreenPrimary.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp)) {
                        Text(
                            text = "Rubro: " + merchantCategory.displayName,
                            fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                IconButton(onClick = { showLogoutConfirm = true }) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión", tint = AlertRed)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tabs de navegación interna del comercio
            val tabs = listOf("Publicaciones", "Mi Catálogo", "Pedidos Clientes")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tabs.forEachIndexed { index, label ->
                    val isSelected = selectedTab == index
                    Surface(
                        onClick = { selectedTab = index },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) GreenPrimary else Color(0xFFF0F0F0),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color.DarkGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Contenido según tab seleccionado
        when (selectedTab) {
            // ---- TAB 0: PUBLICACIONES ----
            0 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Métricas rápidas
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Publicaciones Activas", fontSize = 11.sp, color = Color.Gray)
                                Text("" + bags.size, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                            }
                        }
                        Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Pedidos Pendientes", fontSize = 11.sp, color = Color.Gray)
                                Text("" + merchantOrders.size, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (merchantInventory.isEmpty()) {
                        Surface(
                            color = Color(0xFFFFF8E1), shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFFFE082)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Primero agrega tus productos", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFE65100))
                                    Text("Ve a la pestaña 'Mi Catálogo' y agrega los productos que vendes antes de publicar.", fontSize = 11.sp, color = Color(0xFFBF360C), lineHeight = 14.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Botones de publicación
                    Text("¿Qué deseas publicar hoy?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showIndividualDialog = true },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Producto Individual", fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.Center)
                            }
                        }
                        Button(
                            onClick = { showPackDialog = true },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Publicar Pack de Hoy", fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.Center)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Publicaciones en Vivo:", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (bags.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("Sin publicaciones aún. ¡Publica tu primer producto o pack del día!", color = Color.Gray, textAlign = TextAlign.Center, fontSize = 13.sp)
                        }
                    }

                    bags.forEach { bag ->
                        Card(
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = if (bag.offerType == OfferType.PACK) GreenPrimary.copy(alpha = 0.15f) else Color(0xFFE3F2FD),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (bag.offerType == OfferType.PACK) "📦 " + bag.offerType.displayName else "🍽️ " + bag.offerType.displayName,
                                            color = if (bag.offerType == OfferType.PACK) GreenPrimary else Color(0xFF1976D2),
                                            fontWeight = FontWeight.Bold, fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(color = Color(0xFFFFEBEE), shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            text = "-" + bag.discountPercent + "% OFF",
                                            color = AlertRed, fontWeight = FontWeight.Bold, fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(bag.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                                if (bag.includedItems.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    bag.includedItems.forEach { item ->
                                        Text("• " + item, fontSize = 11.sp, color = Color.DarkGray)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column {
                                        Text(
                                            text = "S/. " + String.format("%.2f", bag.originalPrice),
                                            style = androidx.compose.ui.text.TextStyle(textDecoration = TextDecoration.LineThrough),
                                            color = Color.Gray, fontSize = 12.sp
                                        )
                                        Text("Rescate: S/. " + String.format("%.2f", bag.rescuePrice), fontWeight = FontWeight.Bold, color = GreenPrimary, fontSize = 15.sp)
                                    }
                                    Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp)) {
                                        Text("Stock: " + bag.stockAvailable, fontWeight = FontWeight.Bold, color = GreenPrimary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                }
                                Text("⏰ Recojo: " + bag.pickupTimeWindow, fontSize = 11.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Medium)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            // ---- TAB 1: MI CATÁLOGO ----
            1 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Mi Catálogo de Productos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                            Text("Gestiona los productos que vendes y su stock actual", fontSize = 12.sp, color = Color.Gray)
                        }
                        Button(
                            onClick = { showAddProductDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Agregar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (merchantInventory.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(180.dp),
                            shape = RoundedCornerShape(16.dp), color = Color.White
                        ) {
                            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Aún no has agregado productos", color = Color.Gray, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                Text("Toca el botón 'Agregar' para empezar", color = Color.LightGray, fontSize = 12.sp)
                            }
                        }
                    } else {
                        merchantInventory.forEach { product ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(product.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.DarkGray)
                                        Text("Stock actual: " + product.stock + " " + product.unit + "(s)", fontSize = 12.sp, color = if (product.stock <= 2) AlertRed else Color.Gray)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        // Botón disminuir stock
                                        Surface(
                                            onClick = { if (product.stock > 0) viewModel.updateMerchantProductStock(product.id, product.stock - 1) },
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFFFEBEE)
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "Restar", tint = AlertRed, modifier = Modifier.padding(4.dp).size(18.dp))
                                        }
                                        Surface(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(6.dp)) {
                                            Text("" + product.stock, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                                        }
                                        // Botón aumentar stock
                                        Surface(
                                            onClick = { viewModel.updateMerchantProductStock(product.id, product.stock + 1) },
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFE8F5E9)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Sumar", tint = GreenPrimary, modifier = Modifier.padding(4.dp).size(18.dp))
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        // Botón eliminar producto
                                        Surface(
                                            onClick = { viewModel.removeMerchantProduct(product.id) },
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFFAFAFA),
                                            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                                        ) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = "Eliminar", tint = Color.Gray, modifier = Modifier.padding(4.dp).size(18.dp))
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // ---- TAB 2: PEDIDOS DE CLIENTES ----
            2 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Pedidos de Clientes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Text("Rescates reservados en tu establecimiento", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(14.dp))

                    if (merchantOrders.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(160.dp),
                            shape = RoundedCornerShape(16.dp), color = Color.White
                        ) {
                            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Receipt, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(42.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No hay pedidos pendientes por ahora", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    } else {
                        merchantOrders.forEach { order ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = if (order.status.name == "RECOGIDO") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                order.status.displayName,
                                                fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                                color = if (order.status.name == "RECOGIDO") GreenPrimary else Color(0xFFE65100),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        Text("Código: " + order.reservationCode, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GreenPrimary)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(order.bagTitle, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.DarkGray)
                                    Text("Recojo: " + order.pickupTimeWindow, fontSize = 12.sp, color = Color(0xFFE65100))
                                    Text("Precio rescate pagado: S/. " + String.format("%.2f", order.rescuePrice), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)

                                    if (order.status.name == "PENDIENTE_RECOJO") {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Surface(
                                            color = Color(0xFFF3E5F5),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text("Instrucción para el comerciante:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
                                                Text("Verifica el código del cliente y entrégale el producto al presentarlo.", fontSize = 11.sp, color = Color(0xFF4A148C), lineHeight = 15.sp)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = { viewModel.confirmMerchantOrderPickup(order.id) },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Confirmar Entrega al Cliente", fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Entregado correctamente", color = GreenPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }

    // DIÁLOGO: AGREGAR PRODUCTO AL CATÁLOGO
    if (showAddProductDialog) {
        var prodName by remember { mutableStateOf("") }
        var prodStock by remember { mutableStateOf("10") }
        var prodUnit by remember { mutableStateOf("unidad") }
        var nameError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            title = { Text("Agregar producto a tu catálogo", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = prodName,
                        onValueChange = { prodName = it; nameError = null },
                        label = { Text("Nombre del Producto") },
                        placeholder = { Text("Ej: Pan con jamón y queso") },
                        isError = nameError != null,
                        supportingText = { if (nameError != null) Text(nameError!!, color = MaterialTheme.colorScheme.error, fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = prodStock,
                            onValueChange = { prodStock = it.filter { c -> c.isDigit() }.take(4) },
                            label = { Text("Stock inicial") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = prodUnit,
                            onValueChange = { prodUnit = it },
                            label = { Text("Unidad") },
                            placeholder = { Text("unidad / kg / porción") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val stock = prodStock.toIntOrNull() ?: 0
                        val added = viewModel.addMerchantProduct(prodName, stock, prodUnit.trim().ifEmpty { "unidad" })
                        if (added) {
                            showAddProductDialog = false
                        } else {
                            nameError = if (prodName.trim().length < 2) "El nombre debe tener al menos 2 caracteres"
                            else "Ya existe un producto con ese nombre"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) { Text("Agregar al Catálogo") }
            },
            dismissButton = {
                TextButton(onClick = { showAddProductDialog = false }) { Text("Cancelar", color = Color.Gray) }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // DIÁLOGO: PUBLICAR PRODUCTO INDIVIDUAL (1 solo producto del catálogo)
    if (showIndividualDialog) {
        val inventory = viewModel.merchantInventory
        var selectedProduct by remember { mutableStateOf(inventory.firstOrNull()?.name ?: "") }
        var origPriceInput by remember { mutableStateOf("15.00") }
        var rescuePriceInput by remember { mutableStateOf("7.50") }
        var selectedPickupSlot by remember { mutableStateOf(defaultPickupSlots[0]) }
        var quantityInput by remember { mutableStateOf("3") }
        var hasTriedSubmit by remember { mutableStateOf(false) }

        val origPrice = origPriceInput.toDoubleOrNull() ?: 0.0
        val rescuePrice = rescuePriceInput.toDoubleOrNull() ?: 0.0
        val qty = quantityInput.toIntOrNull() ?: 0

        val priceError = when {
            origPrice <= 0.0 -> "Ingresa el precio original"
            rescuePrice <= 0.0 -> "Ingresa el precio de rescate"
            rescuePrice >= origPrice -> "El precio de rescate debe ser menor al original"
            else -> null
        }
        val qtyError = if (qty <= 0) "La cantidad debe ser mayor a 0" else null
        val productError = if (selectedProduct.isBlank()) "Selecciona o escribe un producto" else null

        AlertDialog(
            onDismissRequest = { showIndividualDialog = false },
            title = {
                Column {
                    Text("Publicar Producto Individual", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("1 producto excedente del día", fontSize = 12.sp, color = Color.Gray)
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {

                    if (inventory.isEmpty()) {
                        Surface(color = Color(0xFFFFF8E1), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "⚠️ Agrega productos a tu catálogo primero (pestaña 'Mi Catálogo') para poder seleccionarlos aquí.",
                                fontSize = 12.sp, color = Color(0xFFE65100),
                                modifier = Modifier.padding(10.dp), lineHeight = 16.sp
                            )
                        }
                        // Igual permitir escribir manualmente si no hay inventario
                        OutlinedTextField(
                            value = selectedProduct,
                            onValueChange = { selectedProduct = it },
                            label = { Text("Nombre del producto a publicar") },
                            placeholder = { Text("Ej: Pan con jamón y queso") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = hasTriedSubmit && productError != null
                        )
                    } else {
                        Text("Selecciona 1 producto de tu catálogo:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            inventory.forEach { prod ->
                                val isChosen = selectedProduct == prod.name
                                Surface(
                                    onClick = { selectedProduct = prod.name },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isChosen) Color(0xFFE3F2FD) else Color.White,
                                    border = BorderStroke(1.dp, if (isChosen) Color(0xFF1976D2) else Color(0xFFE0E0E0)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = isChosen,
                                            onClick = { selectedProduct = prod.name },
                                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF1976D2))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(prod.name, fontSize = 12.sp, fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal, color = if (isChosen) Color(0xFF1976D2) else Color.DarkGray)
                                            Text("Stock disponible: " + prod.stock + " " + prod.unit + "(s)", fontSize = 10.sp, color = if (prod.stock <= 2) AlertRed else Color.Gray)
                                        }
                                    }
                                }
                            }
                        }
                        if (hasTriedSubmit && productError != null) Text(productError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }

                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = origPriceInput,
                            onValueChange = { origPriceInput = it.filter { c -> c.isDigit() || c == '.' } },
                            label = { Text("Original S/.") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f), singleLine = true
                        )
                        OutlinedTextField(
                            value = rescuePriceInput,
                            onValueChange = { rescuePriceInput = it.filter { c -> c.isDigit() || c == '.' } },
                            label = { Text("Rescate S/.") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f), singleLine = true
                        )
                    }
                    if (hasTriedSubmit && priceError != null) Text(priceError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)

                    Text("Franja Horaria de Recojo:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        defaultPickupSlots.chunked(2).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                row.forEach { slot ->
                                    FilterChip(selected = selectedPickupSlot == slot, onClick = { selectedPickupSlot = slot }, label = { Text(slot, fontSize = 10.sp) }, modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = quantityInput,
                        onValueChange = { quantityInput = it.filter { c -> c.isDigit() }.take(4) },
                        label = { Text("Unidades disponibles") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        isError = hasTriedSubmit && qtyError != null,
                        supportingText = { if (hasTriedSubmit && qtyError != null) Text(qtyError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        hasTriedSubmit = true
                        if (priceError == null && qtyError == null && selectedProduct.isNotBlank()) {
                            viewModel.publishSurplusBag(
                                title = selectedProduct.trim(),
                                category = merchantCategory,
                                description = "Producto individual excedente fresco del día.",
                                originalPrice = origPrice, rescuePrice = rescuePrice,
                                pickupWindow = selectedPickupSlot, quantity = qty,
                                offerType = OfferType.INDIVIDUAL,
                                includedItems = listOf(selectedProduct.trim())
                            )
                            showIndividualDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) { Text("Publicar Producto") }
            },
            dismissButton = { TextButton(onClick = { showIndividualDialog = false }) { Text("Cancelar", color = Color.Gray) } },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // DIÁLOGO: PUBLICAR PACK DE HOY (multi-selección del catálogo propio)
    if (showPackDialog) {
        val inventory = viewModel.merchantInventory
        val selectedItems = remember { mutableStateListOf<String>().apply { addAll(inventory.take(3).map { it.name }) } }
        var packTitle by remember { mutableStateOf("Pack del Día: " + merchantCategory.displayName + " Variada") }
        var origPriceInput by remember { mutableStateOf("32.00") }
        var rescuePriceInput by remember { mutableStateOf("9.90") }
        var selectedPickupSlot by remember { mutableStateOf(defaultPickupSlots[1]) }
        var quantityInput by remember { mutableStateOf("4") }
        var hasTriedSubmit by remember { mutableStateOf(false) }

        val origPrice = origPriceInput.toDoubleOrNull() ?: 0.0
        val rescuePrice = rescuePriceInput.toDoubleOrNull() ?: 0.0
        val qty = quantityInput.toIntOrNull() ?: 0
        val discountCalc = if (origPrice > 0 && rescuePrice < origPrice) (((origPrice - rescuePrice) / origPrice) * 100).toInt() else 0

        val priceError = when {
            origPrice <= 0.0 -> "Ingresa el valor total del pack"
            rescuePrice <= 0.0 -> "Ingresa el precio de rescate"
            rescuePrice >= origPrice -> "El precio de rescate debe ser menor al original"
            else -> null
        }
        val itemsError = if (selectedItems.isEmpty()) "Selecciona al menos 1 producto para el Pack" else null
        val qtyError = if (qty <= 0) "La cantidad debe ser mayor a 0" else null

        AlertDialog(
            onDismissRequest = { showPackDialog = false },
            title = {
                Column {
                    Text("Publicar Pack de Hoy", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Mayor descuento combinando varios productos", fontSize = 12.sp, color = GreenPrimary)
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {

                    Surface(
                        color = Color(0xFFFFF8E1), shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFFFE082)), modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Celebration, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Los Packs permiten mayor descuento (60-75% OFF) animando a los clientes a rescatar más volumen.", fontSize = 11.sp, color = Color(0xFFE65100), lineHeight = 15.sp)
                        }
                    }

                    OutlinedTextField(
                        value = packTitle, onValueChange = { packTitle = it },
                        label = { Text("Título del Pack de Hoy") },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )

                    if (inventory.isEmpty()) {
                        Surface(color = Color(0xFFFFF8E1), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Text("⚠️ Agrega primero productos en 'Mi Catálogo'. Sin inventario no puedes armar un Pack.", fontSize = 12.sp, color = Color(0xFFE65100), modifier = Modifier.padding(10.dp), lineHeight = 16.sp)
                        }
                    } else {
                        Text("Selecciona los productos del Pack (" + selectedItems.size + " elegidos):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            inventory.forEach { prod ->
                                val isChecked = selectedItems.contains(prod.name)
                                Surface(
                                    onClick = { if (isChecked) selectedItems.remove(prod.name) else selectedItems.add(prod.name) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isChecked) GreenPrimary.copy(alpha = 0.12f) else Color.White,
                                    border = BorderStroke(1.dp, if (isChecked) GreenPrimary else Color(0xFFE0E0E0)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = { if (isChecked) selectedItems.remove(prod.name) else selectedItems.add(prod.name) },
                                            colors = CheckboxDefaults.colors(checkedColor = GreenPrimary)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Column {
                                            Text(prod.name, fontSize = 12.sp, fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal, color = if (isChecked) GreenPrimary else Color.DarkGray)
                                            Text("Stock: " + prod.stock + " " + prod.unit + "(s)", fontSize = 10.sp, color = if (prod.stock <= 2) AlertRed else Color.Gray)
                                        }
                                    }
                                }
                            }
                        }
                        if (hasTriedSubmit && itemsError != null) Text(itemsError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = origPriceInput,
                            onValueChange = { origPriceInput = it.filter { c -> c.isDigit() || c == '.' } },
                            label = { Text("Valor Total S/.") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f), singleLine = true
                        )
                        OutlinedTextField(
                            value = rescuePriceInput,
                            onValueChange = { rescuePriceInput = it.filter { c -> c.isDigit() || c == '.' } },
                            label = { Text("Precio Pack S/.") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f), singleLine = true
                        )
                    }
                    if (discountCalc > 0) {
                        Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(6.dp)) {
                            Text("🎉 Descuento del Pack: -$discountCalc% OFF", color = GreenPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                    if (hasTriedSubmit && priceError != null) Text(priceError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)

                    Text("Franja Horaria de Recojo:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        defaultPickupSlots.chunked(2).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                row.forEach { slot ->
                                    FilterChip(selected = selectedPickupSlot == slot, onClick = { selectedPickupSlot = slot }, label = { Text(slot, fontSize = 10.sp) }, modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = quantityInput,
                        onValueChange = { quantityInput = it.filter { c -> c.isDigit() }.take(4) },
                        label = { Text("Cantidad de Packs Disponibles") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        isError = hasTriedSubmit && qtyError != null,
                        supportingText = { if (hasTriedSubmit && qtyError != null) Text(qtyError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        hasTriedSubmit = true
                        if (priceError == null && itemsError == null && qtyError == null) {
                            viewModel.publishSurplusBag(
                                title = packTitle.trim().ifEmpty { "Pack del Día: " + merchantCategory.displayName },
                                category = merchantCategory,
                                description = "Pack del día variado. Incluye: " + selectedItems.joinToString(", "),
                                originalPrice = origPrice, rescuePrice = rescuePrice,
                                pickupWindow = selectedPickupSlot, quantity = qty,
                                offerType = OfferType.PACK,
                                includedItems = selectedItems.toList()
                            )
                            showPackDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) { Text("Publicar Pack") }
            },
            dismissButton = { TextButton(onClick = { showPackDialog = false }) { Text("Cancelar", color = Color.Gray) } },
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("¿Estás seguro de que deseas salir de tu panel de comercio?") },
            confirmButton = {
                Button(onClick = { showLogoutConfirm = false; onLogoutClick() }, colors = ButtonDefaults.buttonColors(containerColor = AlertRed)) { Text("Cerrar Sesión") }
            },
            dismissButton = { TextButton(onClick = { showLogoutConfirm = false }) { Text("Cancelar", color = Color.Gray) } }
        )
    }
}
