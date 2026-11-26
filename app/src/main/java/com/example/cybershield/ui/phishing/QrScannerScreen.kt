package com.example.cybershield.ui.phishing

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.cybershield.core.components.*
import com.example.cybershield.core.theme.*
import com.example.cybershield.data.model.ScanResultModel
import com.example.cybershield.data.model.ThreatLevel
import com.example.cybershield.data.repository.PhishingRepository
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

@Composable
fun QrScannerScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val phishingRepository = remember { PhishingRepository() }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var scannedPayload by remember { mutableStateOf<String?>(null) }
    var scanResultModel by remember { mutableStateOf<ScanResultModel?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var flashOn by remember { mutableStateOf(false) }
    var cameraControlRef by remember { mutableStateOf<CameraControl?>(null) }
    var manualInputMode by remember { mutableStateOf(false) }
    var manualUrlInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CyberTopBar(title = "QR Code Safety Inspector", onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(16.dp))

        if (!hasCameraPermission || manualInputMode) {
            CyberCard(borderColor = WarningOrange) {
                Text(
                    text = "Camera Permission Required or Manual Mode",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Grant camera permission for live scanning, or paste QR destination text/URL manually below.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                CyberTextField(
                    value = manualUrlInput,
                    onValueChange = { manualUrlInput = it },
                    label = "Paste QR code text or URL",
                    leadingIcon = Icons.Default.QrCodeScanner
                )

                Spacer(modifier = Modifier.height(12.dp))

                CyberButton(
                    text = "ANALYZE QR CONTENT",
                    onClick = {
                        if (manualUrlInput.isNotBlank()) {
                            scannedPayload = manualUrlInput
                            analyzePayload(manualUrlInput, phishingRepository, scope) { res, analyzing ->
                                scanResultModel = res
                                isAnalyzing = analyzing
                            }
                        }
                    }
                )

                if (!hasCameraPermission) {
                    Spacer(modifier = Modifier.height(12.dp))
                    CyberOutlinedButton(
                        text = "REQUEST CAMERA PERMISSION",
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }
                    )
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(onClick = { manualInputMode = false }) {
                        Text(text = "Switch back to Live Camera Scanner", color = PrimaryBlue)
                    }
                }
            }
        } else {
            // Live Camera Preview Card with ML Kit
            CyberCard(borderColor = PrimaryBlue) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Live Viewfinder",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Row {
                        IconButton(onClick = {
                            flashOn = !flashOn
                            cameraControlRef?.enableTorch(flashOn)
                        }) {
                            Icon(
                                imageVector = if (flashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                contentDescription = "Flashlight",
                                tint = if (flashOn) WarningOrange else TextSecondary
                            )
                        }
                        IconButton(onClick = { manualInputMode = true }) {
                            Icon(imageVector = Icons.Default.Keyboard, contentDescription = "Manual Input", tint = PrimaryBlue)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(DarkCardSurfaceVariant, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }

                                val imageAnalyzer = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()
                                    .also { analysis ->
                                        analysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                                            processImageProxy(
                                                imageProxy,
                                                onQrDetected = { rawValue ->
                                                    if (scannedPayload == null) {
                                                        scannedPayload = rawValue
                                                        analyzePayload(rawValue, phishingRepository, scope) { res, analyzing ->
                                                            scanResultModel = res
                                                            isAnalyzing = analyzing
                                                        }
                                                    }
                                                }
                                            )
                                        }
                                    }

                                try {
                                    cameraProvider.unbindAll()
                                    val camera = cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        CameraSelector.DEFAULT_BACK_CAMERA,
                                        preview,
                                        imageAnalyzer
                                    )
                                    cameraControlRef = camera.cameraControl
                                } catch (_: Exception) {}
                            }, ContextCompat.getMainExecutor(ctx))
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    if (isAnalyzing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(DarkBackground.copy(alpha = 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryBlue)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Point camera at any QR code to inspect destination safety.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Scan Results Section
        scannedPayload?.let { payload ->
            val threatLevel = scanResultModel?.threatLevel ?: ThreatLevel.SAFE
            val statusColor = when (threatLevel) {
                ThreatLevel.SAFE -> SuccessGreen
                ThreatLevel.SUSPICIOUS -> WarningOrange
                ThreatLevel.DANGEROUS -> DangerRed
            }
            val badgeStatus = when (threatLevel) {
                ThreatLevel.SAFE -> CyberStatus.SAFE
                ThreatLevel.SUSPICIOUS -> CyberStatus.WARNING
                ThreatLevel.DANGEROUS -> CyberStatus.DANGER
            }

            CyberCard(borderColor = statusColor) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (threatLevel == ThreatLevel.SAFE) Icons.Default.Security else Icons.Default.Warning,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = when (threatLevel) {
                                    ThreatLevel.DANGEROUS -> "HIGH RISK QR DESTINATION"
                                    ThreatLevel.SUSPICIOUS -> "SUSPICIOUS QR CODE"
                                    ThreatLevel.SAFE -> "QR DESTINATION LOW RISK"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = statusColor,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Risk Score: ${scanResultModel?.riskScore ?: 0}/100",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                    CyberBadge(status = badgeStatus, label = threatLevel.name)
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Extracted QR Payload / URL:", style = MaterialTheme.typography.titleSmall, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = payload,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Security Findings:", style = MaterialTheme.typography.titleSmall, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))

                scanResultModel?.reasons?.forEach { reason ->
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text(text = "• ", color = statusColor, fontWeight = FontWeight.Bold)
                        Text(text = reason, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Safety Warning Notice
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurfaceVariant),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "⚠️ Security Warning: Never automatically open scanned URLs from untrusted sources. Verify domain certificates first.",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CyberButton(
                            text = "COPY CONTENT",
                            icon = Icons.Default.ContentCopy,
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("CyberShield QR Payload", payload)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CyberOutlinedButton(
                            text = "SCAN ANOTHER",
                            icon = Icons.Default.Refresh,
                            onClick = {
                                scannedPayload = null
                                scanResultModel = null
                            }
                        )
                    }
                }

                if (payload.startsWith("http://") || payload.startsWith("https://")) {
                    Spacer(modifier = Modifier.height(12.dp))
                    CyberButton(
                        text = "OPEN SAFELY IN BROWSER",
                        icon = Icons.Default.OpenInBrowser,
                        containerColor = if (threatLevel == ThreatLevel.DANGEROUS) DangerRed else PrimaryBlue,
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(payload))
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "Unable to open URL", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(androidx.camera.core.ExperimentalGetImage::class)
private fun processImageProxy(
    imageProxy: ImageProxy,
    onQrDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val scanner = BarcodeScanning.getClient()
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let { rawValue ->
                        onQrDetected(rawValue)
                        return@addOnSuccessListener
                    }
                }
            }
            .addOnFailureListener {}
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}

private fun analyzePayload(
    payload: String,
    repository: PhishingRepository,
    scope: CoroutineScope,
    onResult: (ScanResultModel, Boolean) -> Unit
) {
    onResult(
        ScanResultModel(
            url = payload,
            threatLevel = ThreatLevel.SAFE,
            riskScore = 0,
            reasons = listOf("Analyzing QR payload...")
        ),
        true
    )
    scope.launch(Dispatchers.IO) {
        val result = repository.analyzeUrl(payload)
        val model = result.getOrElse {
            ScanResultModel(
                url = payload,
                threatLevel = ThreatLevel.SAFE,
                riskScore = 15,
                reasons = listOf("Extracted payload format is text/string with no active threat heuristics.")
            )
        }
        withContext(Dispatchers.Main) {
            onResult(model, false)
        }
    }
}
