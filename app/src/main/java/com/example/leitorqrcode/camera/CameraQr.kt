package com.example.leitorqrcode.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

/**
 * Etapas 2 e 3 — pré-visualização da câmera + análise dos quadros pelo ML Kit.
 *
 * @param aoLerCodigo chamado sempre que um QR code é encontrado na imagem
 */
@Composable
fun CameraQr(
    aoLerCodigo: (Barcode) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Garante que o analisador (criado uma única vez) chame sempre a versão
    // mais recente do lambda recebido
    val aoLerAtual by rememberUpdatedState(aoLerCodigo)

    // (Etapa 3) Leitor do ML Kit configurado para procurar SOMENTE QR codes.
    // Restringir o formato deixa a análise mais rápida e ignora códigos de barras.
    val leitor = remember {
        val opcoes = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        BarcodeScanning.getClient(opcoes)
    }

    val controller = remember {
        LifecycleCameraController(context).apply {
            // (Etapa 2) Caso de uso de ANÁLISE: cada quadro da câmera é entregue
            // a um analisador (não precisamos tirar fotos)
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)

            // (Etapa 3) MlKitAnalyzer: a ponte entre a CameraX e o ML Kit
            val executor = ContextCompat.getMainExecutor(context)
            setImageAnalysisAnalyzer(
                executor,
                MlKitAnalyzer(
                    listOf(leitor),
                    ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
                    executor
                ) { resultado ->
                    // Lista de códigos encontrados neste quadro (pode ser vazia)
                    val codigos = resultado.getValue(leitor)
                    codigos?.firstOrNull()?.let { aoLerAtual(it) }
                }
            )
        }
    }

    // Quando a câmera sai da tela: desliga o analisador e libera o leitor do ML Kit
    DisposableEffect(Unit) {
        onDispose {
            controller.clearImageAnalysisAnalyzer()
            leitor.close()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PreviewView(ctx).apply {
                // COMPATIBLE (TextureView) respeita a área definida no Compose;
                // o modo padrão (SurfaceView) pode desenhar por cima de outros elementos
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        }
    )
}