package com.example.lab4.logic

// абстрактний клас "Виріб"
abstract class Product(val name: String,
                       var manufacturer: String,
                       var year: Int,
                       var price: Double) {
    abstract fun info(): String
}