package com.example.laboratorio4

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class HistorialActivity : AppCompatActivity() {

    private val viewModel: HistorialViewModel by viewModels()

    private lateinit var listView:    ListView
    private lateinit var spinner:     Spinner
    private lateinit var tvCounter:   TextView
    private lateinit var btnClear:    Button
    private lateinit var tvEmpty:     TextView
    private lateinit var btnRegresar: Button

    private lateinit var historialAdapter: HistorialAdapter

    private val filterOptions = listOf(
        "Todos", "nombre", "correo", "tema", "notificaciones"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historial)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViews()
        setupListView()
        setupSpinner()
        setupClearButton()
        setupBackButton()
        observeViewModel()

        // SOLO PARA PROBAR — borrar antes de entregar
        viewModel.recordChange("nombre", "Juan", "Carlos")
        viewModel.recordChange("correo", "juan@gmail.com", "carlos@gmail.com")
        viewModel.recordChange("tema", "claro", "oscuro")
        viewModel.recordChange("notificaciones", "false", "true")
    }

    private fun bindViews() {
        listView    = findViewById(R.id.lvHistorial)
        spinner     = findViewById(R.id.spinnerFilter)
        tvCounter   = findViewById(R.id.tvTotalCount)
        btnClear    = findViewById(R.id.button2)
        tvEmpty     = findViewById(R.id.tvEmptyHistory)
        btnRegresar = findViewById(R.id.button)
    }

    private fun setupListView() {
        historialAdapter = HistorialAdapter(this)
        listView.adapter = historialAdapter
    }

    private fun setupSpinner() {
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            filterOptions
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val selected = filterOptions[position]
                viewModel.applyFilter(if (selected == "Todos") null else selected)
            }
            override fun onNothingSelected(parent: AdapterView<*>) = Unit
        }
    }

    private fun setupClearButton() {
        btnClear.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("¿Limpiar historial?")
                .setMessage("Se eliminarán todos los cambios. Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar") { _, _ ->
                    viewModel.clearHistory()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun setupBackButton() {
        btnRegresar.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.entries.collect { lista ->
                historialAdapter.updateList(lista)
                tvEmpty.visibility  = if (lista.isEmpty()) View.VISIBLE else View.GONE
                listView.visibility = if (lista.isEmpty()) View.GONE    else View.VISIBLE
            }
        }

        lifecycleScope.launch {
            viewModel.totalCount.collect { count ->
                tvCounter.text = "Total de cambios: $count"
            }
        }
    }
}