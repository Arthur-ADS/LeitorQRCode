package com.example.leitorqrcode.data

import androidx.lifecycle.ViewModel
import com.example.leitorqrcode.model.LeituraQr
import com.example.leitorqrcode.ui.LeitorUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LeitorViewModel : ViewModel() {

    private val _estado = MutableStateFlow(LeitorUiState())
    val estado : StateFlow<LeitorUiState> = _estado.asStateFlow()

    fun registrar(leitura : LeituraQr) {
        _estado.update {atual ->
            if(leitura.valor == atual.ultima?.valor) return@update atual

            val semRepeticao = atual.historico.filterNot { it.valor == leitura.valor }

            atual.copy(
                ultima = leitura,
                historico = listOf(leitura) + semRepeticao
            )
        }
    }

    fun limparHistorico(){
        _estado.value = LeitorUiState()
    }

}