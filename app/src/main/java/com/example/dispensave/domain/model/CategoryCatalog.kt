package com.example.dispensave.domain.model

object CategoryCatalog {
    fun getTypicalProducts(category: StoreCategory): List<String> {
        return when (category) {
            StoreCategory.PANADERIA -> listOf(
                "Pan Ciabatta artesanal (bolsa x6)",
                "Croissant de mantequilla francés (x2)",
                "Empanada horneada de carne clásica",
                "Baguette tradicional recién horneada",
                "Pan campesino de masa madre",
                "Pastel de acelga y queso"
            )
            StoreCategory.RESTAURANTE -> listOf(
                "Porción completa de Ají de Gallina con arroz y papas",
                "Porción de Lomo Saltado criollo con arroz",
                "Arroz con Pollo a la norteña con papa a la huancaína",
                "Menú ejecutivo completo del día (Segundo + Entrada)",
                "Tallarines verdes con bistec apanado",
                "Seco de Res con frejoles y arroz"
            )
            StoreCategory.CAFETERIA -> listOf(
                "Sándwich ciabatta de jamón inglés y queso Edam",
                "Porción de Keke artesanal de plátano y nueces",
                "Combo de Galletas de avena con chispas (x3)",
                "Sándwich de pollo deshilachado con mayonesa casera",
                "Quiche horneado de jamón y espinaca",
                "Brownie de chocolate fudge artesanal"
            )
            StoreCategory.PASTELERIA -> listOf(
                "Porción de Torta húmeda de chocolate con fudge",
                "Pie de Limón con merengue italiano",
                "Milhojas relleno de manjar blanco artesanal (x2)",
                "Tartaleta fresca de fresas con crema pastelera",
                "Porción de Cheesecake horneado de frutos rojos",
                "Caja de alfajores artesanales de maicena (x4)"
            )
        }
    }
}
