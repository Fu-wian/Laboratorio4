package com.example.laboratorio4

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistorialRepositorio(context: Context) {

    private val db = HistorialDatabase(context)

    suspend fun addEntry(entry: HistorialEntry): Long =
        withContext(Dispatchers.IO) {
            db.insertEntry(entry)
        }

    suspend fun getEntries(fieldFilter: String? = null): List<HistorialEntry> =
        withContext(Dispatchers.IO) {
            db.getAllEntries(fieldFilter)
        }

    suspend fun clearHistory(): Int =
        withContext(Dispatchers.IO) {
            db.clearAllEntries()
        }

    suspend fun countEntries(): Int =
        withContext(Dispatchers.IO) {
            db.countEntries()
        }
}