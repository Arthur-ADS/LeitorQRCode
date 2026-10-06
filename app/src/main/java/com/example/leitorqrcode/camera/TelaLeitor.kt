package com.example.leitorqrcode.camera

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.leitorqrcode.data.LeitorViewModel
import com.example.leitorqrcode.model.LeituraQr
import com.example.leitorqrcode.model.paraLeitura
import com.example.leitorqrcode.permissoes.ExigePermissao
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.net.Uri

private val formatoHora = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

@Composable
fun TelaLeitor(
    modifier: Modifier = Modifier,
    viewModel: LeitorViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Leitor de QR Code", style = MaterialTheme.typography.headlineSmall)

        ExigePermissao(
            permissao = Manifest.permission.CAMERA,
            justificativa = "O leitor usa a câmera para ler QR codes. " +
                    "Nenhuma imagem é salva."
        ) {
            CameraQr(
                aoLerCodigo = { codigo -> codigo.paraLeitura()?.let { viewModel.registrar(it) } },
                modifier = Modifier.fillMaxWidth().height(260.dp)
            )
        }

        UltimaLeitura(estado.ultima, aoAbrir = { abrirLeitura(context, it) })

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Histórico (${estado.historico.size})", Modifier.weight(1f))
            TextButton(onClick = { viewModel.limparHistorico() }) {
                Text("Limpar")
            }
        }

        LazyColumn(Modifier.weight(1f)) {
            items(estado.historico, key = { it.valor }) { leitura ->
                ItemHistorico(leitura)
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun UltimaLeitura(leitura: LeituraQr?, aoAbrir: (LeituraQr) -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (leitura == null) {
                Text("Aponte a câmera para um QR code.")
            } else {
                Text(leitura.tipo.rotulo, style = MaterialTheme.typography.labelLarge)
                Text(leitura.descricao)

                // 6.3 — só aparece quando há uma Uri para abrir
                if (leitura.uriAcao != null) {
                    Button(onClick = { aoAbrir(leitura) }) {
                        Text("Abrir")
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemHistorico(leitura: LeituraQr) {
    val hora = remember(leitura.instante) {
        SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            .format(Date(leitura.instante))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(leitura.tipo.rotulo, style = MaterialTheme.typography.labelLarge)
            Text(hora, style = MaterialTheme.typography.labelMedium)
        }
        Text(leitura.descricao, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun abrirLeitura(context: Context, leitura: LeituraQr) {
    val uri = leitura.uriAcao ?: return


    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))


    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            context,
            "Nenhum aplicativo encontrado para abrir este conteúdo.",
            Toast.LENGTH_SHORT
        ).show()
    }
}