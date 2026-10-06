package com.example.leitorqrcode.permissoes

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.LifecycleResumeEffect

@Composable
fun ExigePermissao(
    permissao: String,
    justificativa: String,
    conteudo: @Composable () -> Unit
) {
    val context = LocalContext.current
    var concedida by remember { mutableStateOf(context.temPermissao(permissao)) }
    var negadaPermanente by remember { mutableStateOf(false) }

    val solicitar = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { aceitou ->
        concedida = aceitou
        // Depois de uma recusa, o Android informa se ainda vale a pena
        // explicar e pedir de novo (true). Se responder false logo após uma
        // recusa, o usuário negou permanentemente (ou recusou 2 vezes).
        negadaPermanente = !aceitou &&
                !ActivityCompat.shouldShowRequestPermissionRationale(
                    context.encontrarActivity(), permissao
                )
    }

    // Executado sempre que a tela volta ao primeiro plano (ON_RESUME).
    // Cobre o caso em que o usuário foi às Configurações, liberou a
    // permissão e voltou ao app.
    LifecycleResumeEffect(permissao) {
        concedida = context.temPermissao(permissao)
        if (concedida) negadaPermanente = false
        onPauseOrDispose { }
    }

    when {
        concedida -> conteudo()

        negadaPermanente -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "A permissão foi negada permanentemente. " +
                        "Para usar este recurso, habilite-a nas configurações do app."
            )
            Button(onClick = { context.abrirConfiguracoesDoApp() }) {
                Text("Abrir configurações")
            }
        }

        else -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(justificativa)                    // explica ANTES de pedir
            Button(onClick = { solicitar.launch(permissao) }) {
                Text("Conceder permissão")
            }
        }
    }
}
