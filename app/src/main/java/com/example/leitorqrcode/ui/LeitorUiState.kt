package com.example.leitorqrcode.ui

import com.example.leitorqrcode.model.LeituraQr

data class LeitorUiState(
    val ultima : LeituraQr? = null,
    val historico : List<LeituraQr> = emptyList()
)
