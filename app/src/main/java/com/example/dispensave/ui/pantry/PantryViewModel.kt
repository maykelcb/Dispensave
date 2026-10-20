package com.example.dispensave.ui.pantry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dispensave.data.repository.*
import com.example.dispensave.domain.model.*
import com.example.dispensave.util.ValidationUtils
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

class PantryViewModel(
    private val repository: PantryRepository,
    private val userRepository: UserRepository = UserRepository(),
    private val rescueRepository: RescueRepository = RescueRepository()
) : ViewModel() {

    // --- MÓDULO MARKETPLACE DE RESCATE ---
    val surplusBags: StateFlow<List<SurplusBag>> = rescueRepository.surplusBags
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList())

    val orders: StateFlow<List<Order>> = rescueRepository.orders
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList())

    var currentUserRole by mutableStateOf(UserRole.CLIENTE)
        private set

    var currentStoreName by mutableStateOf<String?>(null)
        private set

    var currentStoreCategory by mutableStateOf<StoreCategory?>(null)
        private set

    var userName by mutableStateOf("Maykel Bernaola")
        private set

    var userEmail by mutableStateOf("maykel@universidad.edu.pe")
        private set

    // --- INVENTARIO DEL COMERCIO (catálogo propio de productos que vende) ---
    private val _merchantInventory = mutableStateListOf<MerchantProduct>()
    val merchantInventory: List<MerchantProduct> get() = _merchantInventory

    private var _nextProductId = 1

    fun addMerchantProduct(name: String, stock: Int, unit: String): Boolean {
        val trimmed = name.trim()
        if (trimmed.length < 2) return false
        if (_merchantInventory.any { it.name.equals(trimmed, ignoreCase = true) }) return false
        _merchantInventory.add(MerchantProduct(id = _nextProductId++, name = trimmed, stock = stock, unit = unit))
        return true
    }

    fun updateMerchantProductStock(productId: Int, newStock: Int) {
        val idx = _merchantInventory.indexOfFirst { it.id == productId }
        if (idx >= 0 && newStock >= 0) {
            _merchantInventory[idx] = _merchantInventory[idx].copy(stock = newStock)
        }
    }

    fun removeMerchantProduct(productId: Int) {
        _merchantInventory.removeAll { it.id == productId }
    }

    fun getMerchantInventoryNames(): List<String> = _merchantInventory.map { it.name }

    // Pedidos filtrados para el comercio (los que generaron los clientes para sus bags)
    fun getMerchantPendingOrders(): List<Order> {
        val myStoreName = currentStoreName ?: userName
        return orders.value.filter { it.storeName.equals(myStoreName, ignoreCase = true) }
    }

    fun setCurrentUser(user: User) {
        userName = user.name
        userEmail = user.email
        currentUserRole = user.role
        currentStoreName = user.storeName
        currentStoreCategory = user.storeCategory
        // Limpiar inventario al cambiar de usuario
        _merchantInventory.clear()
        _nextProductId = 1
    }

    fun isEmailRegistered(email: String): Boolean = userRepository.isEmailRegistered(email)

    fun registerUser(
        name: String,
        email: String,
        password: String,
        role: UserRole = UserRole.CLIENTE,
        storeName: String? = null,
        storeCategory: StoreCategory? = null
    ): RegisterResult {
        val user = User(
            name = name,
            email = email,
            password = password,
            role = role,
            storeName = if (role == UserRole.COMERCIO) storeName else null,
            storeCategory = if (role == UserRole.COMERCIO) storeCategory else null
        )
        val result = userRepository.registerUser(user)
        if (result is RegisterResult.Success) setCurrentUser(user)
        return result
    }

    fun loginUser(email: String, password: String): LoginResult {
        val result = userRepository.login(email, password)
        if (result is LoginResult.Success) setCurrentUser(result.user)
        return result
    }

    fun reserveSurplusBag(bag: SurplusBag): Order? = rescueRepository.reserveBag(bag.id)

    fun publishSurplusBag(
        title: String,
        category: StoreCategory,
        description: String,
        originalPrice: Double,
        rescuePrice: Double,
        pickupWindow: String,
        quantity: Int,
        offerType: OfferType = OfferType.PACK,
        includedItems: List<String> = emptyList()
    ): Boolean {
        if (title.isBlank() || originalPrice <= 0 || rescuePrice <= 0 || quantity <= 0) return false
        val newBag = SurplusBag(
            id = 0,
            storeId = 999,
            storeName = currentStoreName ?: userName,
            category = category,
            offerType = offerType,
            title = title.trim(),
            description = description.trim().ifEmpty {
                if (offerType == OfferType.PACK) "Pack variado con excedentes frescos del día empacados con cuidado."
                else "Producto excedente del día preparado con ingredientes de primera calidad."
            },
            includedItems = includedItems,
            originalPrice = originalPrice,
            rescuePrice = rescuePrice,
            pickupTimeWindow = pickupWindow.trim().ifEmpty { "20:00 - 21:00" },
            stockAvailable = quantity
        )
        rescueRepository.publishBag(newBag)
        // Si el producto existe en inventario, descontar stock
        includedItems.forEach { itemName ->
            val idx = _merchantInventory.indexOfFirst { it.name.equals(itemName, ignoreCase = true) }
            if (idx >= 0) {
                val current = _merchantInventory[idx]
                if (current.stock > 0) {
                    _merchantInventory[idx] = current.copy(stock = current.stock - 1)
                }
            }
        }
        return true
    }

    fun confirmMerchantOrderPickup(orderId: Int): Boolean = rescueRepository.confirmPickup(orderId)

    fun confirmOrderPickup(orderId: Int): Boolean = rescueRepository.confirmPickup(orderId)

    // --- MÓDULO ANTERIOR DE DESPENSA (Mantenido para compatibilidad) ---
    val products: StateFlow<List<Product>> = repository.allProducts
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList())

    var selectedProduct by mutableStateOf<Product?>(null)
        private set

    fun selectProduct(product: Product?) { selectedProduct = product }

    fun validateProductName(name: String): String? {
        return if (name.trim().length < 2) "El nombre debe tener al menos 2 caracteres" else null
    }

    fun validateExpirationDate(expirationDate: Long?): String? {
        return if (expirationDate == null || expirationDate <= 0L) "La fecha seleccionada debe ser válida" else null
    }

    fun isDateExpired(expirationDate: Long): Boolean {
        val now = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        return expirationDate < now
    }

    fun getExpirationWarning(expirationDate: Long): String? {
        return if (isDateExpired(expirationDate)) "Este producto ya está vencido" else null
    }

    fun validateQuantity(quantity: Int?): String? {
        return if (quantity == null || quantity <= 0) "La cantidad debe ser mayor a 0" else null
    }

    fun validateUnit(unit: String): String? {
        return if (unit.trim().isEmpty()) "La unidad es requerida" else null
    }

    fun validateEmail(email: String): String? = ValidationUtils.validateEmailStrict(email)

    fun validateProduct(name: String, expirationDate: Long?, quantity: Int?, unit: String): ProductValidationResult {
        val nameErr = validateProductName(name)
        val expErr = validateExpirationDate(expirationDate)
        val expWarn = if (expirationDate != null) getExpirationWarning(expirationDate) else null
        val qtyErr = validateQuantity(quantity)
        val unitErr = validateUnit(unit)
        return ProductValidationResult(
            isValid = nameErr == null && expErr == null && qtyErr == null && unitErr == null,
            nameError = nameErr, expirationError = expErr, expirationWarning = expWarn,
            quantityError = qtyErr, unitError = unitErr
        )
    }

    fun addProduct(name: String, category: String, expirationDate: Long, quantity: Int, unit: String): Boolean {
        val validation = validateProduct(name, expirationDate, quantity, unit)
        if (!validation.isValid) return false
        viewModelScope.launch {
            repository.addProduct(Product(
                name = name.trim(),
                category = category.trim().ifEmpty { "Abarrotes" },
                expirationDate = expirationDate,
                quantity = quantity,
                unit = unit.trim().ifEmpty { "unidad" }
            ))
        }
        return true
    }

    fun updateProduct(product: Product) { viewModelScope.launch { repository.updateProduct(product) } }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            if (selectedProduct?.id == product.id) selectedProduct = null
        }
    }
}
