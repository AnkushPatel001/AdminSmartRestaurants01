package com.example.adminsmartrestaurants.model

data class OrderModel(
    var userName: String? = null,
    var phoneNumber: String? = null,
    var totalPrice: String? = null,
    var orderAccepted: Boolean = false,
    var paymentReceived: Boolean = false,
    var foodNames: List<String>? = null,
    var foodImages: List<String>? = null,
    var foodQuantities: List<Int>? = null,
    var foodPrices: List<String>? = null,
    var itemPushKey: String? = null,
    var currentTime: Long? = null,
    var address: String? = null,
    var userId: String? = null
)
