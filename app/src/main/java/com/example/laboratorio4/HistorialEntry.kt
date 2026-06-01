package com.example.laboratorio4

data class HistorialEntry(
    val id: Long = 0L,
    val field: String,
    val oldValue: String,
    val newValue: String,
    val timestamp: String
)