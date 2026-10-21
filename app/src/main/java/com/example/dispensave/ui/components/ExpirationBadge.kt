package com.example.dispensave.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dispensave.domain.model.Product

enum class ExpirationAlertLevel(
    val title: String,
    val containerColor: Color,
    val contentColor: Color,
    val dotColor: Color,
    val emoji: String
) {
    VIGENTE(
        title = "Vigente",
        containerColor = Color(0xFFE8F5E9), // Verde suave
        contentColor = Color(0xFF1B5E20),  // Verde oscuro
        dotColor = Color(0xFF2E7D32),
        emoji = "🟢"
    ),
    POR_VENCER(
        title = "Por Vencer",
        containerColor = Color(0xFFFFF8E1), // Amarillo/Ámbar suave
        contentColor = Color(0xFFB78103),  // Ámbar oscuro
        dotColor = Color(0xFFF57C00),
        emoji = "🟡"
    ),
    VENCIDO(
        title = "Vencido",
        containerColor = Color(0xFFFFEBEE), // Rojo suave
        contentColor = Color(0xFFC62828),  // Rojo oscuro
        dotColor = Color(0xFFD32F2F),
        emoji = "🔴"
    )
}

/**
 * Determina el nivel semafórico de caducidad según las reglas del negocio:
 * 🟢 Vigente: más de 7 días para vencer (> 7 días)
 * 🟡 Por vencer: 3 días o menos para vencer (≤ 3 días)
 * 🔴 Vencido: fecha superada o ya caducado
 * (Entre 4 y 7 días se considera consumo regular vigente)
 */
fun determineAlertLevel(daysUntilExpiration: Long, isExpired: Boolean): ExpirationAlertLevel {
    return when {
        isExpired || daysUntilExpiration < 0 -> ExpirationAlertLevel.VENCIDO
        daysUntilExpiration <= 3 -> ExpirationAlertLevel.POR_VENCER
        else -> ExpirationAlertLevel.VIGENTE
    }
}

fun formatExpirationDescription(daysUntilExpiration: Long, isExpired: Boolean): String {
    return when {
        isExpired || daysUntilExpiration < 0 -> "Vencido"
        daysUntilExpiration == 0L -> "Vence hoy"
        daysUntilExpiration == 1L -> "Vence mañana"
        daysUntilExpiration <= 3L -> "Vence en $daysUntilExpiration días"
        daysUntilExpiration <= 7L -> "Vence en $daysUntilExpiration días"
        else -> "> 7 días ($daysUntilExpiration d)"
    }
}

@Composable
fun ExpirationBadge(
    daysUntilExpiration: Long,
    isExpired: Boolean,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val level = determineAlertLevel(daysUntilExpiration, isExpired)
    val label = formatExpirationDescription(daysUntilExpiration, isExpired)

    Box(
        modifier = modifier
            .background(
                color = level.containerColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = if (compact) 2.dp else 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color = level.dotColor, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (compact) level.title else "${level.title}: $label",
                color = level.contentColor,
                fontSize = if (compact) 11.sp else 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ExpirationBadge(
    product: Product,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    ExpirationBadge(
        daysUntilExpiration = product.daysUntilExpiration,
        isExpired = product.isExpired,
        modifier = modifier,
        compact = compact
    )
}
