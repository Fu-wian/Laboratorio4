package com.example.laboratorio4.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.laboratorio4.data.Perfil
import com.example.laboratorio4.repository.PerfilRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PerfilRepository(application)

    private val _perfil = MutableStateFlow(Perfil())
    val perfil: StateFlow<Perfil> = _perfil.asStateFlow()

    init {
        cargarPerfil()
    }

    fun cargarPerfil() {
        _perfil.value = repository.cargarPerfil()
    }

    fun guardarPerfil(
        nombre: String,
        correo: String,
        tema: String,
        notificaciones: Boolean
    ) {
        val nuevoPerfil = Perfil(
            nombre = nombre,
            correo = correo,
            tema = tema,
            notificaciones = notificaciones
        )

        repository.guardarPerfil(nuevoPerfil)
        _perfil.value = nuevoPerfil
    }
}