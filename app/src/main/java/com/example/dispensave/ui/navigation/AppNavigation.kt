package com.example.dispensave.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dispensave.domain.model.UserRole
import com.example.dispensave.ui.auth.LoginScreen
import com.example.dispensave.ui.auth.RegisterScreen
import com.example.dispensave.ui.home.ExploreScreen
import com.example.dispensave.ui.impact.ImpactScreen
import com.example.dispensave.ui.merchant.StoreDashboardScreen
import com.example.dispensave.ui.onboarding.OnboardingScreen
import com.example.dispensave.ui.orders.MyOrdersScreen
import com.example.dispensave.ui.pantry.PantryViewModel
import com.example.dispensave.ui.screens.shopping.ShoppingListScreen
import com.example.dispensave.ui.theme.GreenPrimary

sealed class ItemNavInferior(val ruta: String, val titulo: String, val icono: ImageVector) {
    object Explorar : ItemNavInferior("explore", "Explorar", Icons.Default.Search)
    object Compras : ItemNavInferior("shopping", "Compras", Icons.Default.ShoppingCart)
    object MisRescates : ItemNavInferior("orders", "Mis Rescates", Icons.Default.ConfirmationNumber)
    object Pedidos : ItemNavInferior("orders", "Pedidos", Icons.Default.Receipt)
    object Impacto : ItemNavInferior("impact", "Impacto CO2", Icons.Default.Eco)
    object MiComercio : ItemNavInferior("store", "Mi Comercio", Icons.Default.Store)
}

@Composable
fun AppNavigation(viewModel: PantryViewModel) {
    val navController = rememberNavController()
    var prefilledEmail by remember { mutableStateOf("") }

    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") {
            OnboardingScreen(
                onStartClick = { navController.navigate("register") { launchSingleTop = true } },
                onLoginClick = { navController.navigate("login") { launchSingleTop = true } }
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = viewModel,
                onRegisterSuccess = {
                    navController.navigate("main") { popUpTo("onboarding") { inclusive = true } }
                },
                onBackClick = {
                    navController.navigate("onboarding") { popUpTo("onboarding") { inclusive = false }; launchSingleTop = true }
                },
                onGoToLogin = { email ->
                    prefilledEmail = email
                    navController.navigate("login") { popUpTo("onboarding") { inclusive = false }; launchSingleTop = true }
                }
            )
        }
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                initialEmail = prefilledEmail,
                onLoginSuccess = {
                    navController.navigate("main") { popUpTo("onboarding") { inclusive = true } }
                },
                onBackClick = {
                    navController.navigate("onboarding") { popUpTo("onboarding") { inclusive = false }; launchSingleTop = true }
                },
                onRegisterClick = {
                    navController.navigate("register") { popUpTo("onboarding") { inclusive = false }; launchSingleTop = true }
                }
            )
        }
        composable("main") {
            ContenedorPrincipal(
                viewModel = viewModel,
                onLogout = { navController.navigate("login") { popUpTo("main") { inclusive = true } } }
            )
        }
    }
}

@Composable
fun ContenedorPrincipal(
    viewModel: PantryViewModel,
    onLogout: () -> Unit
) {
    val esComerciante = viewModel.currentUserRole == UserRole.COMERCIO
    var tabSeleccionado by remember { mutableStateOf(if (esComerciante) "store" else "explore") }

    val itemsCliente = listOf(
        ItemNavInferior.Explorar,
        ItemNavInferior.Compras,
        ItemNavInferior.MisRescates,
        ItemNavInferior.Impacto
    )

    val itemsComerciante = listOf(
        ItemNavInferior.MiComercio,
        ItemNavInferior.Pedidos,
        ItemNavInferior.Impacto
    )

    val itemsActuales = if (esComerciante) itemsComerciante else itemsCliente

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                itemsActuales.forEach { item ->
                    val estaSeleccionado = tabSeleccionado == item.ruta
                    NavigationBarItem(
                        selected = estaSeleccionado,
                        onClick = { tabSeleccionado = item.ruta },
                        icon = {
                            Icon(imageVector = item.icono, contentDescription = item.titulo, tint = if (estaSeleccionado) GreenPrimary else Color.Gray)
                        },
                        label = {
                            Text(text = item.titulo, fontSize = 11.sp, fontWeight = if (estaSeleccionado) FontWeight.Bold else FontWeight.Normal, color = if (estaSeleccionado) GreenPrimary else Color.Gray)
                        },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFE8F5E9))
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (tabSeleccionado) {
                "explore" -> ExploreScreen(viewModel = viewModel, onLogoutClick = onLogout)
                "shopping" -> ShoppingListScreen(
                    viewModel = viewModel,
                    onBackClick = { tabSeleccionado = "explore" },
                    onGoToPantryClick = { tabSeleccionado = "explore" }
                )
                "orders" -> MyOrdersScreen(viewModel = viewModel)
                "impact" -> ImpactScreen()
                "store" -> StoreDashboardScreen(viewModel = viewModel, onLogoutClick = onLogout)
            }
        }
    }
}
