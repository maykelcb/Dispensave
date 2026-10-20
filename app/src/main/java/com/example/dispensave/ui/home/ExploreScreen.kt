package com.example.dispensave.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dispensave.domain.model.OfferType
import com.example.dispensave.domain.model.Order
import com.example.dispensave.domain.model.StoreCategory
import com.example.dispensave.domain.model.SurplusBag
import com.example.dispensave.ui.pantry.PantryViewModel
import com.example.dispensave.ui.theme.AlertRed
import com.example.dispensave.ui.theme.GreenLight
import com.example.dispensave.ui.theme.GreenPrimary

@Composable
fun ExploreScreen(
    viewModel: PantryViewModel,
    onLogoutClick: () -> Unit
) {
    val bags by viewModel.surplusBags.collectAsState()
    var selectedCategory by remember { mutableStateOf<StoreCategory?>(null) }
    var selectedBagToReserve by remember { mutableStateOf<SurplusBag?>(null) }
    var generatedOrder by remember { mutableStateOf<Order?>(null) }
    var showProfileDialog by remember { mutableStateOf(false) }

    val filteredBags = remember(bags, selectedCategory) {
        if (selectedCategory == null) bags else bags.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Cabecera: Saludo personalizado sin correo debajo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "¡Hola, " + viewModel.userName + "!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                )
                Text(
                    text = "🌱 Rescatista de comida activo",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )
            }
            IconButton(onClick = { showProfileDialog = true }) {
                Surface(
                    shape = CircleShape,
                    color = GreenLight.copy(alpha = 0.3f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = GreenPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Banner informativo estilo Too Good To Go
        Surface(
            color = Color(0xFFE8F5E9),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Rescata delicias gastronómicas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = GreenPrimary
                    )
                    Text(
                        text = "Packs variados y productos individuales de restaurantes, panaderías y cafeterías locales.",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filtro de categorías específicas (Restaurantes, Panaderías, Cafeterías, Pastelerías)
        Text(
            text = "Filtrar por tipo de comercio:",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("Todos", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
            items(StoreCategory.values()) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category.displayName, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de ofertas activas para rescate
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Rescates Disponibles Hoy (" + filteredBags.size + "):",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (filteredBags.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay excedentes disponibles en esta categoría por ahora. ¡Vuelve más tarde!",
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        } else {
            filteredBags.forEach { bag ->
                SurplusBagCard(
                    bag = bag,
                    onReserveClick = { selectedBagToReserve = bag }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    // Diálogo de Confirmación de Rescate
    if (selectedBagToReserve != null) {
        val bag = selectedBagToReserve!!
        AlertDialog(
            onDismissRequest = { selectedBagToReserve = null },
            icon = {
                Icon(
                    if (bag.offerType == OfferType.PACK) Icons.Default.Inventory2 else Icons.Default.RestaurantMenu,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Confirmar Rescate",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = bag.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GreenPrimary)
                    Text(text = "Establecimiento: " + bag.storeName, fontSize = 13.sp, color = Color.DarkGray)

                    if (bag.includedItems.isNotEmpty()) {
                        Surface(
                            color = Color(0xFFF9F9F9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = if (bag.offerType == OfferType.PACK) "Incluye en este Pack:" else "Producto:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFF2E7D32)
                                )
                                bag.includedItems.forEach { item ->
                                    Text("• " + item, fontSize = 11.sp, color = Color.DarkGray)
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Precio Original:", fontSize = 13.sp, color = Color.Gray)
                        Text(
                            "S/. " + String.format("%.2f", bag.originalPrice),
                            style = androidx.compose.ui.text.TextStyle(textDecoration = TextDecoration.LineThrough),
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Precio de Rescate:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "S/. " + String.format("%.2f", bag.rescuePrice),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Horario de recojo:", fontSize = 12.sp, color = Color.DarkGray)
                        Text(bag.pickupTimeWindow, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val order = viewModel.reserveSurplusBag(bag)
                        selectedBagToReserve = null
                        generatedOrder = order
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("Confirmar y Reservar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { selectedBagToReserve = null }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Diálogo con el Ticket / Código de Recojo Generado
    if (generatedOrder != null) {
        val order = generatedOrder!!
        AlertDialog(
            onDismissRequest = { generatedOrder = null },
            icon = {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(44.dp))
            },
            title = {
                Text(
                    text = "¡Rescate Reservado con Éxito!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Muestra este código al llegar al comercio para retirar tu rescate:",
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray
                    )
                    Surface(
                        color = GreenLight.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = order.reservationCode,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = GreenPrimary,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                    Text(text = "Local: " + order.storeName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(text = "Horario: " + order.pickupTimeWindow, color = Color(0xFFE65100), fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { generatedOrder = null },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("Entendido")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Diálogo de Cuenta
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cuenta de Usuario", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Nombre: " + viewModel.userName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(text = "Correo: " + viewModel.userEmail, fontSize = 13.sp, color = Color.DarkGray)
                    Text(text = "Rol: " + viewModel.currentUserRole.displayName, fontSize = 13.sp, color = GreenPrimary)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showProfileDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("Cerrar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showProfileDialog = false
                    onLogoutClick()
                }) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = AlertRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cerrar Sesión", color = AlertRed)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun SurplusBagCard(
    bag: SurplusBag,
    onReserveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = bag.category.displayName,
                            color = GreenPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Badge de Tipo de Oferta (Pack vs Individual)
                    Surface(
                        color = if (bag.offerType == OfferType.PACK) Color(0xFFFFF3E0) else Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (bag.offerType == OfferType.PACK) "📦 Pack del Día" else "🍽️ Individual",
                            color = if (bag.offerType == OfferType.PACK) Color(0xFFE65100) else Color(0xFF1976D2),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "" + bag.rating, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "· " + bag.distanceKm + " km", fontSize = 11.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = bag.storeName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
            Text(text = bag.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = GreenPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = bag.description, fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp)

            // Si es un Pack y tiene productos incluidos, mostrar la lista para que el cliente aprecie el contenido
            if (bag.includedItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = if (bag.offerType == OfferType.PACK) Color(0xFFF9FBE7) else Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (bag.offerType == OfferType.PACK) Color(0xFFDCEDC8) else Color(0xFFEEEEEE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (bag.offerType == OfferType.PACK) Icons.Default.CheckCircle else Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = if (bag.offerType == OfferType.PACK) Color(0xFF33691E) else Color.DarkGray,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (bag.offerType == OfferType.PACK) "Contenido incluido en este Pack:" else "Producto a rescatar:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (bag.offerType == OfferType.PACK) Color(0xFF33691E) else Color.DarkGray
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        bag.includedItems.forEach { item ->
                            Text(
                                text = "• " + item,
                                fontSize = 11.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(start = 6.dp, top = 1.dp)
                            )
                        }
                        if (bag.offerType == OfferType.PACK) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🎉 Mayor descuento por rescatar en pack combinado",
                                fontSize = 10.sp,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Horario de recojo y badge de stock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Recoge hoy: " + bag.pickupTimeWindow, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFE65100))
                }
                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Quedan " + bag.stockAvailable,
                        color = Color(0xFFE65100),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(12.dp))

            // Precios y botón de rescate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "S/. " + String.format("%.2f", bag.originalPrice),
                        textDecoration = TextDecoration.LineThrough,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "S/. " + String.format("%.2f", bag.rescuePrice),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = if (bag.discountPercent >= 65) Color(0xFFFFEBEE) else Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = (if (bag.discountPercent >= 65) "🔥 -" else "-") + bag.discountPercent + "%",
                                color = if (bag.discountPercent >= 65) AlertRed else Color(0xFFE65100),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Button(
                    onClick = onReserveClick,
                    enabled = bag.isAvailable,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = if (bag.isAvailable) "Rescatar" else "Agotado", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
