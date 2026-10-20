package com.example.dispensave.data.repository

import com.example.dispensave.domain.model.OfferType
import com.example.dispensave.domain.model.Order
import com.example.dispensave.domain.model.OrderStatus
import com.example.dispensave.domain.model.StoreCategory
import com.example.dispensave.domain.model.SurplusBag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class RescueRepository {

    private val _bags = MutableStateFlow(
        listOf(
            SurplusBag(
                id = 1,
                storeId = 101,
                storeName = "Panadería & Pastelería La Baguette",
                category = StoreCategory.PANADERIA,
                offerType = OfferType.PACK,
                title = "Pack del Día: Panadería Variada",
                description = "Surtido con los mejores panes y bollería horneados hoy. Ideal para el lonche familiar.",
                includedItems = listOf(
                    "Pan Ciabatta artesanal (bolsa x6)",
                    "Croissant de mantequilla francés (x2)",
                    "Empanada horneada de carne clásica"
                ),
                originalPrice = 30.00,
                rescuePrice = 9.90, // ~67% descuento
                pickupTimeWindow = "20:00 - 21:00",
                stockAvailable = 3,
                rating = 4.9,
                address = "Av. Dos de Mayo 890, San Isidro",
                distanceKm = 0.8
            ),
            SurplusBag(
                id = 2,
                storeId = 101,
                storeName = "Panadería & Pastelería La Baguette",
                category = StoreCategory.PANADERIA,
                offerType = OfferType.INDIVIDUAL,
                title = "Baguette Tradicional recién horneada",
                description = "Baguette crocante elaborada con harina de trigo seleccionada.",
                includedItems = listOf("Baguette tradicional recién horneada"),
                originalPrice = 8.00,
                rescuePrice = 3.90, // ~51% descuento
                pickupTimeWindow = "19:30 - 20:30",
                stockAvailable = 5,
                rating = 4.9,
                address = "Av. Dos de Mayo 890, San Isidro",
                distanceKm = 0.8
            ),
            SurplusBag(
                id = 3,
                storeId = 102,
                storeName = "Restaurante Sabor Criollo",
                category = StoreCategory.RESTAURANTE,
                offerType = OfferType.PACK,
                title = "Pack del Día: Menú Criollo Completo",
                description = "Ración abundante de comida criolla fresca con entrada y bebida del día.",
                includedItems = listOf(
                    "Porción completa de Ají de Gallina con arroz y papas",
                    "Papa a la huancaína de entrada",
                    "Chicha morada artesanal (500 ml)"
                ),
                originalPrice = 38.00,
                rescuePrice = 12.00, // ~68% descuento
                pickupTimeWindow = "16:00 - 17:30",
                stockAvailable = 2,
                rating = 4.7,
                address = "Av. Arequipa 2450, Lince",
                distanceKm = 1.5
            ),
            SurplusBag(
                id = 4,
                storeId = 103,
                storeName = "Cafetería & Bakery D'Sala",
                category = StoreCategory.CAFETERIA,
                offerType = OfferType.INDIVIDUAL,
                title = "Sándwich ciabatta de jamón y queso",
                description = "Sándwich tostado artesanal preparado hoy con ingredientes de primera calidad.",
                includedItems = listOf("Sándwich ciabatta de jamón inglés y queso Edam"),
                originalPrice = 16.00,
                rescuePrice = 7.50, // ~53% descuento
                pickupTimeWindow = "19:00 - 20:30",
                stockAvailable = 3,
                rating = 4.8,
                address = "Calle Los Libertadores 140, San Isidro",
                distanceKm = 1.1
            ),
            SurplusBag(
                id = 5,
                storeId = 104,
                storeName = "Pastelería & Dulcería San Antonio",
                category = StoreCategory.PASTELERIA,
                offerType = OfferType.PACK,
                title = "Pack del Día: Dulces & Pasteles",
                description = "Selección de postres recién salidos de vitrina. Excelente para compartir.",
                includedItems = listOf(
                    "Porción de Torta húmeda de chocolate con fudge",
                    "Milhojas relleno de manjar blanco artesanal (x2)",
                    "Tartaleta fresca de fresas"
                ),
                originalPrice = 36.00,
                rescuePrice = 11.50, // ~68% descuento
                pickupTimeWindow = "21:00 - 22:00",
                stockAvailable = 2,
                rating = 4.9,
                address = "Av. Primavera 373, Surco",
                distanceKm = 2.4
            )
        )
    )
    val surplusBags: Flow<List<SurplusBag>> = _bags.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(
        listOf(
            Order(
                id = 1,
                bagId = 1,
                storeName = "Panadería & Pastelería La Baguette",
                bagTitle = "Pack del Día: Panadería Variada",
                rescuePrice = 9.90,
                pickupTimeWindow = "20:00 - 21:00",
                reservationCode = "DS-7429",
                status = OrderStatus.PENDIENTE_RECOJO
            )
        )
    )
    val orders: Flow<List<Order>> = _orders.asStateFlow()

    fun reserveBag(bagId: Int): Order? {
        val currentBags = _bags.value.toMutableList()
        val index = currentBags.indexOfFirst { it.id == bagId }
        if (index == -1 || currentBags[index].stockAvailable <= 0) return null

        val bag = currentBags[index]
        currentBags[index] = bag.copy(stockAvailable = bag.stockAvailable - 1)
        _bags.value = currentBags

        val newOrder = Order(
            id = (_orders.value.maxOfOrNull { it.id } ?: 0) + 1,
            bagId = bag.id,
            storeName = bag.storeName,
            bagTitle = bag.title,
            rescuePrice = bag.rescuePrice,
            pickupTimeWindow = bag.pickupTimeWindow,
            reservationCode = "DS-" + UUID.randomUUID().toString().take(4).uppercase(),
            status = OrderStatus.PENDIENTE_RECOJO
        )

        val currentOrders = _orders.value.toMutableList()
        currentOrders.add(0, newOrder)
        _orders.value = currentOrders
        return newOrder
    }

    fun publishBag(bag: SurplusBag) {
        val current = _bags.value.toMutableList()
        val newId = (current.maxOfOrNull { it.id } ?: 0) + 1
        current.add(0, bag.copy(id = newId))
        _bags.value = current
    }

    fun confirmPickup(orderId: Int): Boolean {
        val current = _orders.value.toMutableList()
        val index = current.indexOfFirst { it.id == orderId }
        if (index == -1) return false
        current[index] = current[index].copy(status = OrderStatus.RECOGIDO)
        _orders.value = current
        return true
    }
}
