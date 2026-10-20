package com.example.dispensave.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dispensave.domain.model.Product
import com.example.dispensave.domain.model.ProductStatus
import com.example.dispensave.ui.pantry.PantryViewModel
import com.example.dispensave.ui.theme.AlertOrange
import com.example.dispensave.ui.theme.AlertRed
import com.example.dispensave.ui.theme.GreenLight
import com.example.dispensave.ui.theme.GreenPrimary

@Composable
fun HomeScreen(
    viewModel: PantryViewModel,
    onScanClick: () -> Unit,
    onRecycleClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onLogoutClick: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    var showProfileDialog by remember { mutableStateOf(false) }

    val urgentProducts = remember(products) {
        products.filter {
            it.isExpired || it.calculatedStatus == ProductStatus.VENCIDO || it.calculatedStatus == ProductStatus.POR_VENCER
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Cabecera de usuario: Solo muestra el nombre del usuario sin el correo abajo
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
            }
            IconButton(onClick = { showProfileDialog = true }) {
                Icon(Icons.Default.Settings, contentDescription = "Ajustes y Cuenta", tint = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Alerta de insumos por vencer o vencidos
        if (urgentProducts.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alerta",
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "¡Atención! Insumos por vencer",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100),
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Tienes " + urgentProducts.size + " producto(s) crítico(s). Prioriza su consumo hoy.",
                            fontSize = 12.sp,
                            color = Color(0xFF8D6E63)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Resumen rápido
        Text(
            text = "Resumen de tu Despensa",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Total Alimentos", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "" + products.size,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Por Vencer / Vencidos", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "" + urgentProducts.size,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (urgentProducts.isNotEmpty()) AlertRed else GreenPrimary
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Insumos críticos
        if (urgentProducts.isNotEmpty()) {
            Text(
                text = "Consumir Primero",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(10.dp))

            urgentProducts.take(3).forEach { product ->
                UrgentItemCard(
                    name = product.name,
                    location = "" + product.quantity + " " + product.unit + " · " + product.category,
                    badgeText = if (product.isExpired) "Vencido" else "Por Vencer",
                    badgeColor = if (product.isExpired) AlertRed else AlertOrange,
                    onClick = { onProductClick(product) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Accesos directos rápidos
        Text(
            text = "¿Qué deseas hacer hoy?",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onScanClick() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GreenPrimary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Escanear",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Escanear Compra",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Registra con boleta",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onRecycleClick() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.Autorenew,
                        contentDescription = "Reciclar",
                        tint = GreenPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "¿Cómo reciclar?",
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Usa el clasificador IA",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta ecológica
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GreenLight)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = "Eco",
                    tint = GreenPrimary,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "¡Esta semana has evitado generar 2.4 kg de CO2! Vas por excelente camino.",
                    fontSize = 12.sp,
                    color = GreenPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    // Diálogo de Cuenta y Perfil
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
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Usuario: " + viewModel.userName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Correo: " + viewModel.userEmail,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
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
private fun UrgentItemCard(
    name: String,
    location: String,
    badgeText: String,
    badgeColor: Color,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = Color(0xFFF5F5F5)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = name.take(1), fontWeight = FontWeight.Bold, color = GreenPrimary)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(text = location, fontSize = 11.sp, color = Color.Gray)
            }
            Surface(
                color = badgeColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = badgeText,
                    color = badgeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
