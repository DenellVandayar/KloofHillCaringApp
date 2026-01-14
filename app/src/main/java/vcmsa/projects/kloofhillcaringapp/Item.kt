package vcmsa.projects.kloofhillcaringapp

data class Item(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var imageBase64: String? = null,
    var available: Boolean = true,
    val guide: String? = null,

    // New deposit fields
    var hasDeposit: Boolean = false,
    var depositAmount: Double? = null
)
