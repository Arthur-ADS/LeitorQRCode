package com.example.leitorqrcode.permissoes

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat

/**
 * Funções utilitárias usadas pelos exemplos de permissão.
 * São "funções de extensão": podem ser chamadas como se fossem
 * métodos do próprio Context, por exemplo: context.temPermissao(...)
 */

/** Pergunta ao sistema se a permissão JÁ foi concedida. */
fun Context.temPermissao(permissao: String): Boolean =
    ContextCompat.checkSelfPermission(this, permissao) ==
            PackageManager.PERMISSION_GRANTED

/**
 * Encontra a Activity a partir do Context do Compose.
 * O LocalContext.current pode ser um "invólucro" (ContextWrapper) da Activity,
 * por isso subimos pelos baseContext até encontrá-la.
 */
fun Context.encontrarActivity(): Activity {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    error("Activity não encontrada a partir deste Context")
}

/** Abre a tela "Informações do app" nas Configurações do Android. */
fun Context.abrirConfiguracoesDoApp() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
    startActivity(intent)
}
