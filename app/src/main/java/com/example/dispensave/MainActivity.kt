package com.example.dispensave

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.dispensave.data.local.AppDatabase
import com.example.dispensave.data.repository.PantryRepository
import com.example.dispensave.data.repository.RescueRepository
import com.example.dispensave.data.repository.UserRepository
import com.example.dispensave.ui.navigation.AppNavigation
import com.example.dispensave.ui.pantry.PantryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Parte 3 (Rubén): Inicialización de Room Database con inyección de dependencias
        val database = AppDatabase.getDatabase(this)
        val productDao = database.productDao()
        val pantryRepository = PantryRepository(productDao)

        val userRepository = UserRepository(this)
        val rescueRepository = RescueRepository()
        val viewModel = PantryViewModel(pantryRepository, userRepository, rescueRepository)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
