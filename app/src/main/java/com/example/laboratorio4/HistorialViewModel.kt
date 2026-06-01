package com.example.laboratorio4

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistorialViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HistorialRepositorio(application)

    private val _entries = MutableStateFlow<List<HistorialEntry>>(emptyList())
    val entries: StateFlow<List<HistorialEntry>> = _entries.asStateFlow()

    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount.asStateFlow()

    private val _activeFilter = MutableStateFlow<String?>(null)
    val activeFilter: StateFlow<String?> = _activeFilter.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory(filter: String? = _activeFilter.value) {
        viewModelScope.launch {
            _activeFilter.value = filter
            _entries.value      = repository.getEntries(filter)
            _totalCount.value   = repository.countEntries()
        }
    }

    fun recordChange(field: String, oldValue: String, newValue: String) {
        if (oldValue == newValue) return
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date())
        viewModelScope.launch {
            repository.addEntry(
                HistorialEntry(
                    field     = field,
                    oldValue  = oldValue,
                    newValue  = newValue,
                    timestamp = timestamp
                )
            )
            loadHistory()
        }
    }

    fun applyFilter(field: String?) {
        loadHistory(field)
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _entries.value    = emptyList()
            _totalCount.value = 0
        }
    }
}