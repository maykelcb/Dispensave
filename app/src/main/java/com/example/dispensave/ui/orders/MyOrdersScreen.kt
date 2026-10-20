package com.example.dispensave.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dispensave.domain.model.Order
import com.example.dispensave.domain.model.OrderStatus
import com.example.dispensave.domain.model.UserRole
import com.example.dispensave.ui.pantry.PantryViewModel
import com.example.dispensave.ui.theme.AlertRed
import com.example.dispensave.ui.theme.GreenLight
import com.example.dispensave.ui.theme.GreenPrimary

@Composable
fun MyOrdersScreen(viewModel: PantryViewModel) {
    val isMerchant = viewModel.currentUserRole == UserRole.COMERCIO

    if (isMerchant) {
        MerchantOrdersView(viewModel)
    } else {
        ClientOrdersView(viewModel)
    }
}

// ---- Vista de Pedidos para el CLIENTE ----
@Composable
private fun ClientOrdersView(viewModel: PantryViewModel) {
    val orders by viewModel.orders.collectAsState()
    val myStoreName = viewModel.currentStoreName
    // El cliente ve SOLO sus propias órdenes (excluye las del comercio si hubiera)
    val clientOrders = if (myStoreName != null) {
        orders.filter { !it.storeName.equals(myStoreName, ignoreCase = true) }
    } else orders

    var orderToValidate by remember { mutableStateOf<Order?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))
            .padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        Text("Mis Rescates Activos", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
        Text("Presenta tu ticket digital en el restaurante o panadería.", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        if (clientOrders.isEmpty()) {
            Surface(modifier = Modifier.fillMaxWidth().height(160.dp), shape = RoundedCornerShape(16.dp), color = Color.White) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(42.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Aún no tienes rescates reservados", color = Color.Gray, fontSize = 14.sp)
                    Text("Explora los packs disponibles y reserva uno", color = Color.LightGray, fontSize = 12.sp)
                }
            }
        } else {
            clientOrders.forEach { order ->
                ClientOrderTicketCard(order = order, onValidateClick = { orderToValidate = order })
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }

    if (orderToValidate != null) {
        val order = orderToValidate!!
        AlertDialog(
            onDismissRequest = { orderToValidate = null },
            icon = { Icon(Icons.Default.QrCode, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(40.dp)) },
            title = { Text("Validar Entrega en el Local", fontWeight = FontWeight.Bold, fontSize = 17.sp, textAlign = TextAlign.Center) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Muestra este código al encargado del local:", fontSize = 12.sp, color = Color.Gray)
                    Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(10.dp)) {
                        Text(order.reservationCode, fontSize = 24.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp, color = GreenPrimary, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
                    }
                    Text("¿Ya te entregaron tu rescate?", fontSize = 13.sp, textAlign = TextAlign.Center)
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.confirmOrderPickup(order.id); orderToValidate = null }, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) { Text("Confirmar que lo Recibí") }
            },
            dismissButton = { TextButton(onClick = { orderToValidate = null }) { Text("Atrás", color = Color.Gray) } },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun ClientOrderTicketCard(order: Order, onValidateClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = if (order.status == OrderStatus.RECOGIDO) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(order.status.displayName, color = if (order.status == OrderStatus.RECOGIDO) GreenPrimary else Color(0xFFE65100), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Text("Código: " + order.reservationCode, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GreenPrimary)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(order.storeName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
            Text(order.bagTitle, fontSize = 13.sp, color = GreenPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Recojo: " + order.pickupTimeWindow, fontSize = 12.sp, color = Color(0xFFE65100), fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Total: S/. " + String.format("%.2f", order.rescuePrice), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.DarkGray)
                if (order.status == OrderStatus.PENDIENTE_RECOJO) {
                    Button(onClick = onValidateClick, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary), shape = RoundedCornerShape(10.dp)) {
                        Text("Validar en el Local", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("¡Rescatado!", color = GreenPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ---- Vista de Pedidos para el COMERCIO ----
@Composable
private fun MerchantOrdersView(viewModel: PantryViewModel) {
    val allOrders by viewModel.orders.collectAsState()
    val myStoreName = viewModel.currentStoreName ?: viewModel.userName
    val merchantOrders = allOrders.filter { it.storeName.equals(myStoreName, ignoreCase = true) }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))
            .padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        Text("Pedidos de Clientes", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
        Text("Gestiona las reservas hechas en tu establecimiento.", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        if (merchantOrders.isEmpty()) {
            Surface(modifier = Modifier.fillMaxWidth().height(160.dp), shape = RoundedCornerShape(16.dp), color = Color.White) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Receipt, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(42.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No hay pedidos de clientes por ahora", color = Color.Gray, fontSize = 14.sp)
                    Text("Cuando alguien reserve un rescate, aparecerá aquí", color = Color.LightGray, fontSize = 12.sp)
                }
            }
        } else {
            merchantOrders.forEach { order ->
                MerchantOrderCard(order = order, onConfirmDelivery = { viewModel.confirmOrderPickup(order.id) })
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun MerchantOrderCard(order: Order, onConfirmDelivery: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = if (order.status == OrderStatus.RECOGIDO) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(order.status.displayName, color = if (order.status == OrderStatus.RECOGIDO) GreenPrimary else Color(0xFFE65100), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Surface(color = Color(0xFFEDE7F6), shape = RoundedCornerShape(8.dp)) {
                    Text("Código: " + order.reservationCode, color = Color(0xFF4527A0), fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(order.bagTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Recojo: " + order.pickupTimeWindow, fontSize = 12.sp, color = Color(0xFFE65100), fontWeight = FontWeight.SemiBold)
            }
            Text("Precio pagado por cliente: S/. " + String.format("%.2f", order.rescuePrice), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)

            if (order.status == OrderStatus.PENDIENTE_RECOJO) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(color = Color(0xFFF3E5F5), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Acción requerida:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
                        Text("El cliente presentará el código " + order.reservationCode + ". Verifícalo y entrega el rescate.", fontSize = 11.sp, color = Color(0xFF4A148C), lineHeight = 15.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onConfirmDelivery,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Confirmar Entrega al Cliente", fontWeight = FontWeight.Bold)
                }
            } else {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Entregado correctamente al cliente", color = GreenPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
