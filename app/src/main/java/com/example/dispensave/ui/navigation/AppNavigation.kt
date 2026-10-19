package com.example.dispensave.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
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
import com.example.dispensave.domain.model.Product
import com.example.dispensave.ui.auth.LoginScreen
import com.example.dispensave.ui.home.HomeScreen
import com.example.dispensave.ui.impact.ImpactScreen
import com.example.dispensave.ui.onboarding.OnboardingScreen
import com.example.dispensave.ui.pantry.AddProductDialog
import com.example.dispensave.ui.pantry.PantryScreen
import com.example.dispensave.ui.pantry.PantryViewModel
import com.example.dispensave.ui.product.ProductDetailScreen
import com.example.dispensave.ui.recipe.RecipeScreen
import com.example.dispensave.ui.recycle.RecycleAssistantScreen
import com.example.dispensave.ui.scan.ScanScreen
import com.example.dispensave.ui.theme.GreenPrimary

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Inicio", Icons.Default.Home)
    object Pantry : BottomNavItem("pantry", "Despensa", Icons.Default.ShoppingBag)
    object Recipe : BottomNavItem("recipe", "Recetario", Icons.Default.Restaurant)
    object Impact : BottomNavItem("impact", "Impacto", Icons.Default.Eco)
}

@Composable
fun AppNavigation(viewModel: PantryViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") {
            OnboardingScreen(
                onStartClick = { navController.navigate("login") },
                onLoginClick = { navController.navigate("login") }
            )
        }
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("onboarding") { inclusive = false }
                    }
                },
                onBackClick = { navController.popBackStack() },
                onGuestClick = {
                    navController.navigate("main") {
                        popUpTo("onboarding") { inclusive = false }
                    }
                }
            )
        }
        composable("main") {
            MainContainer(
                viewModel = viewModel,
                onNavigateToScan = { navController.navigate("scan") },
                onNavigateToRecycle = { navController.navigate("recycle") },
                onNavigateToProductDetail = { product ->
                    viewModel.selectProduct(product)
                    navController.navigate("product_detail")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
        composable("scan") {
            ScanScreen(
                onBackClick = { navController.popBackStack() },
                onManualInputClick = {
                    navController.navigate("main")
                }
            )
        }
        composable("recycle") {
            RecycleAssistantScreen(
                onBackClick = { navController.popBackStack() },
                onScanAnotherClick = { navController.popBackStack() }
            )
        }
        composable("product_detail") {
            ProductDetailScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() },
                onDeleteSuccess = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun MainContainer(
    viewModel: PantryViewModel,
    onNavigateToScan: () -> Unit,
    onNavigateToRecycle: () -> Unit,
    onNavigateToProductDetail: (Product) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("home") }
    var showAddDialog by remember { mutableStateOf(false) }

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Pantry,
        BottomNavItem.Recipe,
        BottomNavItem.Impact
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                items.forEach { item ->
                    val isSelected = selectedTab == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = item.route },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) GreenPrimary else Color.Gray
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) GreenPrimary else Color.Gray
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFFE8F5E9)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                "home" -> HomeScreen(
                    viewModel = viewModel,
                    onScanClick = onNavigateToScan,
                    onRecycleClick = onNavigateToRecycle,
                    onProductClick = onNavigateToProductDetail,
                    onLogoutClick = onLogout
                )
                "pantry" -> PantryScreen(
                    viewModel = viewModel,
                    onAddProductClick = { showAddDialog = true },
                    onProductClick = { product -> onNavigateToProductDetail(product) }
                )
                "recipe" -> RecipeScreen()
                "impact" -> ImpactScreen()
            }
        }

        if (showAddDialog) {
            AddProductDialog(
                viewModel = viewModel,
                onDismiss = { showAddDialog = false },
                onAdd = { name, category, expirationDate, quantity, unit ->
                    viewModel.addProduct(
                        name = name,
                        category = category,
                        expirationDate = expirationDate,
                        quantity = quantity,
                        unit = unit
                    )
                    showAddDialog = false
                }
            )
        }
    }
}