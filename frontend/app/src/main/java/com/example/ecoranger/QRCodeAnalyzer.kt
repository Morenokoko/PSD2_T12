package com.example.ecoranger

import android.content.Context
import android.graphics.ImageFormat
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QRCodeAnalyzer(
    private val context: Context,
    private val onQrCodeScanned: (result: String?) -> Unit
) : ImageAnalysis.Analyzer {

    companion object {
        private val SUPPORTED_IMAGE_FORMATS = listOf(ImageFormat.YUV_420_888, ImageFormat.YUV_422_888, ImageFormat.YUV_444_888)
    }

    private var lastErrorShownTime = 0L // Track the time when the error message was last shown
    private val errorDisplayInterval = 10000L // Set the interval between error messages in milliseconds

    override fun analyze(image: ImageProxy) {
        if (image.format in SUPPORTED_IMAGE_FORMATS) {
            val bytes = image.planes.first().buffer.toByteArray()
            val source = PlanarYUVLuminanceSource(
                bytes,
                image.width,
                image.height,
                0,
                0,
                image.width,
                image.height,
                false
            )
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            try {
                val result = MultiFormatReader().apply {
                    setHints(
                        mapOf(
                            DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)
                        )
                    )
                }.decode(binaryBitmap)
                onQrCodeScanned(result.text)
            } catch (e: Exception) {
                e.printStackTrace()
                    showInvalidQRCodePopup()
            } finally {
                image.close()
            }
        }
    }

    private fun showInvalidQRCodePopup() {
        // Get the current time
        val currentTime = System.currentTimeMillis()

        // Check if enough time has passed since the last error message was shown
        if (currentTime - lastErrorShownTime >= errorDisplayInterval) {
            // If enough time has passed, show the error message
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "Invalid QR Code, please try again.", Toast.LENGTH_SHORT).show()
                lastErrorShownTime = currentTime // Update the last error shown time
            }
        }
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        return ByteArray(remaining()).also { get(it) }
    }
}