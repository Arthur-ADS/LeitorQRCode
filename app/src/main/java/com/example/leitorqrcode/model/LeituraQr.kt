package com.example.leitorqrcode.model

import com.google.mlkit.vision.barcode.common.Barcode

data class LeituraQr(
    val valor : String,
    val tipo : TipoConteudo,
    val descricao : String,
    val uriAcao : String?,
    val instante : Long = System.currentTimeMillis()
)

fun Barcode.paraLeitura() : LeituraQr? {

    val bruto = rawValue ?: return null

    return when (valueType) {
        Barcode.TYPE_URL -> {
            val link = url?.url ?: bruto
            LeituraQr(bruto, TipoConteudo.LINK, link, link)
        }

        Barcode.TYPE_WIFI -> {
            val rede = wifi?.ssid ?: bruto
            LeituraQr(bruto, TipoConteudo.WIFI, "Rede: $rede", null)
        }

        Barcode.TYPE_PHONE -> {
            val numero = phone?.number ?: bruto
            LeituraQr(bruto, TipoConteudo.TELEFONE, numero, "tel: $numero")
        }

        Barcode.TYPE_EMAIL -> {
            val endereco = email?.address ?: bruto
            LeituraQr(bruto, TipoConteudo.EMAIL, endereco, "mailto: $endereco")
        }

        Barcode.TYPE_GEO -> {
            val lat = geoPoint?.lat ?: bruto
            val lng = geoPoint?.lng ?: bruto
            LeituraQr(bruto, TipoConteudo.LOCALIZACAO, "Latitude: $lat, Longitude: $lng", "geo:$lat,$lng?q=$lat,$lng")
        }

        else -> LeituraQr(bruto, TipoConteudo.TEXTO, bruto, null)
    }
}
