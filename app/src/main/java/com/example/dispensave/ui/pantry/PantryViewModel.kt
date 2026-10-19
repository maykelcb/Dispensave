package com.example.dispensave.ui.pantry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dispensave.data.repository.PantryRepository
import com.example.dispensave.domain.model.Product
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class ProductValidationResult(
    val isValid: Boolean,
    val nameError: String? = null,
    val expirationError: String? = null,
    val expirationWarning: String? = null,
    val quantityError: String? = null,
    val unitError: String? = null
)

class PantryViewModel(private val repository: PantryRepository) : ViewModel() {

    val products: StateFlow<List<Product>> = repository.allProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    var selectedProduct by mutableStateOf<Product?>(null)
        private set

    var userEmail by mutableStateOf("maykel@universidad.edu.pe")
        private set

    fun setUserEmailAddress(email: String) {
        userEmail = email
    }

    fun selectProduct(product: Product?) {
        selectedProduct = product
    }

    // Regla 1: Validación del Nombre del Producto
    // No se permite registrar productos con nombres vacíos o menores a 2 caracteres.
    // Mensaje de error: "El nombre del producto debe contener al menos 2 caracteres"
    fun validateProductName(name: String): String? {
        val trimmed = name.trim()
        return if (trimmed.length < 2) {
            "El nombre del producto debe contener al menos 2 caracteres"
        } else null
    }

    // Regla 2: Validación de Fecha de Vencimiento
    // La fecha seleccionada debe ser válida y estar definida. Si la fecha es menor a la fecha actual,
    // el sistema asigna automáticamente el estado "Vencido" al producto.
    // Mensaje de advertencia: "Este producto ya está vencido, consúmelo o deséchalo pronto"
    fun validateExpirationDate(expirationDate: Long?): String? {
        return if (expirationDate == null || expirationDate <= 0L) {
            "La fecha seleccionada debe ser válida y estar definida"
        } else null
    }

    fun isDateExpired(expirationDate: Long): Boolean {
        val now = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val exp = Calendar.getInstance().apply {
            timeInMillis = expirationDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        return exp < now || expirationDate < System.currentTimeMillis()
    }

    fun getExpirationWarning(expirationDate: Long): String? {
        return if (isDateExpired(expirationDate)) {
            "Este producto ya está vencido, consúmelo o deséchalo pronto"
        } else null
    }

    // Regla 3: Validación de Cantidad y Unidad
    // La cantidad debe ser un número entero mayor a cero (> 0).
    // Mensaje de error: "La cantidad debe ser mayor a 0"
    fun validateQuantity(quantity: Int?): String? {
        return if (quantity == null || quantity <= 0) {
            "La cantidad debe ser mayor a 0"
        } else null
    }

    fun validateUnit(unit: String): String? {
        return if (unit.trim().isEmpty()) {
            "La unidad es requerida"
        } else null
    }

    // Regla 4: Validación de Formato de Correo Electrónico
    // Utiliza la expresión regular de EmailAddress de Android para garantizar el estándar usuario@dominio.com.
    fun validateEmail(email: String): String? {
        val trimmed = email.trim()
        if (trimmed.isEmpty()) {
            return "El correo electrónico es requerido"
        }
        val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()
        return if (!isValid) {
            "Formato de correo no válido (ejemplo: usuario@dominio.com)"
        } else null
    }

    // Validación integral para el registro de producto
    fun validateProduct(
        name: String,
        expirationDate: Long?,
        quantity: Int?,
        unit: String
    ): ProductValidationResult {
        val nameErr = validateProductName(name)
        val expErr = validateExpirationDate(expirationDate)
        val expWarn = if (expirationDate != null) getExpirationWarning(expirationDate) else null
        val qtyErr = validateQuantity(quantity)
        val unitErr = validateUnit(unit)

        val isValid = nameErr == null && expErr == null && qtyErr == null && unitErr == null
        return ProductValidationResult(
            isValid = isValid,
            nameError = nameErr,
            expirationError = expErr,
            expirationWarning = expWarn,
            quantityError = qtyErr,
            unitError = unitErr
        )
    }

    fun addProduct(
        name: String,
        category: String,
        expirationDate: Long,
        quantity: Int,
        unit: String
    ): Boolean {
        val validation = validateProduct(name, expirationDate, quantity, unit)
        if (!validation.isValid) {
            return false
        }
        viewModelScope.launch {
            val product = Product(
                name = name.trim(),
                category = category.trim().ifEmpty { "Abarrotes" },
                expirationDate = expirationDate,
                quantity = quantity,
                unit = unit.trim().ifEmpty { "unidad" }
            )
            repository.addProduct(product)
        }
        return true
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            if (selectedProduct?.id == product.id) {
                selectedProduct = null
            }
        }
    }
}