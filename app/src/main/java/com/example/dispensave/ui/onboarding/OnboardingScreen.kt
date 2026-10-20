package com.example.dispensave.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dispensave.ui.theme.GreenPrimary

@Composable
fun OnboardingScreen(
    onStartClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(10.dp),
                color = GreenPrimary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("D", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "DISPENSAVE",
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = GreenPrimary,
                fontSize = 20.sp
            )
        }
        Spacer(modifier = Modifier.height(26.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFF0F7F4)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "🥐 🍲 🥖 🥑",
                    fontSize = 42.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "¡Salva comida deliciosa a precio de rescate!",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = GreenPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Conectamos restaurantes y panaderías con personas para rescatar excedentes del día con hasta 70% de descuento. Come rico, ahorra y cuida el planeta.",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(22.dp))

        FeatureItem(
            icon = Icons.Default.Restaurant,
            title = "Packs Sorpresa con 70% desc.",
            description = "Rescata comida fresca recién preparada de locales top de tu zona."
        )
        Spacer(modifier = Modifier.height(12.dp))
        FeatureItem(
            icon = Icons.Default.Store,
            title = "Para Clientes y Comercios",
            description = "Restaurantes y panaderías recuperan costos y evitan desperdiciar."
        )
        Spacer(modifier = Modifier.height(12.dp))
        FeatureItem(
            icon = Icons.Default.Eco,
            title = "Impacto ecológico real",
            description = "Cada pack rescatado evita 2.5 kg de CO2 en el medio ambiente."
        )
        Spacer(modifier = Modifier.height(26.dp))

        Button(
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
            Text(
                text = "Comenzar ahora  ➜",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        TextButton(onClick = onLoginClick) {
            Text(
                text = "¿Ya tienes cuenta? Inicia sesión",
                color = GreenPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun FeatureItem(icon: ImageVector, title: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFE8F5E9)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = description, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
