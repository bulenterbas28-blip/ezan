package com.example.data.model

data class City(
    val plateCode: Int,
    val name: String,
    val lat: Double,
    val lon: Double
) {
    val displayName: String
        get() = "${plateCode.toString().padStart(2, '0')} - $name"
}
