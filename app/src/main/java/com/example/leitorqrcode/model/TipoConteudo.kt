package com.example.leitorqrcode.model

enum class TipoConteudo(val rotulo : String) {
    LINK("Link"), WIFI("Rede Wi-Fi"), TELEFONE("Telefone"),
    EMAIL("E-mail"), LOCALIZACAO("Localização"), TEXTO("Texto")
}