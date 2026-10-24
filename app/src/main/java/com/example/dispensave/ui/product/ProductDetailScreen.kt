package com.example.dispensave.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dispensave.domain.model.ProductStatus
import com.example.dispensave.ui.pantry.PantryViewModel
import com.example.dispensave.ui.theme.AlertOrange
import com.example.dispensave.ui.theme.AlertRed
import com.example.dispensave.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    viewModel: PantryViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    onDeleteSuccess: () -> Unit
) {
    val product = viewModel.selectedProduct
    val productName = product?.name ?: "Yogurt Griego Danlac"
    val category = "${product?.category ?: "Lácteos"} • ${product?.unit ?: "unidad"}"
    var quantity by remember(product) { mutableIntStateOf(product?.quantity ?: 1) }
    var pushNotificationEnabled by remember { mutableStateOf(true) }

    val status = product?.calculatedStatus ?: ProductStatus.DISPONIBLE
    val isExpired = product?.isExpired ?: false

    val badgeColor = when {
        isExpired || status == ProductStatus.VENCIDO -> AlertRed
        status == ProductStatus.POR_VENCER -> AlertOrange
        else -> GreenPrimary
    }

    val statusMessage = when {
        isExpired || status == ProductStatus.VENCIDO -> "Producto vencido (${product?.formattedExpirationDate ?: "Ayer"})"
        status == ProductStatus.POR_VENCER -> "Vence en ${product?.daysUntilExpiration ?: 2} días (${product?.formattedExpirationDate})"
        else -> "Vence el ${product?.formattedExpirationDate ?: "Próximamente"}"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Insumo", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    if (product != null) {
                        IconButton(onClick = {
                            viewModel.deleteProduct(product)
                            onDeleteSuccess()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFD32F2F))
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFE8F5E9)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = productName.take(2).uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = productName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
            Text(
                text = category,
                fontSize = 13.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = badgeColor.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alerta",
                        tint = badgeColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = statusMessage,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Configuración",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Cantidad", fontSize = 14.sp, color = Color.DarkGray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = {
                                // Regla 3: Cantidad mayor a 0 (no permite reducir por debajo de 1)
                                if (quantity > 1) quantity--
                            },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(36.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("-", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "$quantity ${product?.unit ?: "unidad"}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        OutlinedButton(
                            onClick = { quantity++ },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(36.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+", fontSize = 18.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Alertas programadas",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Notificación push", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Avísame 3 días antes de expirar", fontSize = 11.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = pushNotificationEnabled,
                        onCheckedChange = { pushNotificationEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = GreenPrimary)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (product != null) {
                        viewModel.updateProduct(product.copy(quantity = quantity))
                    }
                    onSaveSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text("Guardar cambios", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = {
                    if (product != null) {
                        viewModel.deleteProduct(product)
                    }
                    onDeleteSuccess()
                }
            ) {
                Text("Eliminar insumo", color = Color(0xFFD32F2F), fontSize = 13.sp)
            }
        }
    }
}