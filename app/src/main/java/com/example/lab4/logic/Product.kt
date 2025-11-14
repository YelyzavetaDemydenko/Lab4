package com.example.lab4.logic

// абстрактний клас "Виріб"
abstract class Product(val name: String,
                       val manufacturer: String,
                       val year: Int,
                       val price: Double) {
    abstract fun info(): String
}