package com.khoben.ticker.common

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import com.khoben.ticker.model.Stock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class StockImageRemoteDownloader(private val context: Context) {

    suspend fun downloadLogo(stock: Stock) = withContext(Dispatchers.IO) {
        val image = loadImageFromUrl(stock.logo)
        saveImageOnDisk(
            image,
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath!!,
            stock.ticker
        )
    }

    private suspend fun loadImageFromUrl(url: String?): Bitmap? {
        if (url.isNullOrBlank()) return null
        val request = ImageRequest.Builder(context)
            .data(url)
            .build()
        return ImageLoader(context).execute(request).drawable?.toBitmap()
    }

    private suspend fun saveImageOnDisk(bitmap: Bitmap?, folder: String, filename: String): File? {
        if (bitmap == null) return null
        var file: File? = null
        val dir = File(folder)
        try {
            withContext(Dispatchers.IO) {
                kotlin.runCatching {
                    if (!dir.exists()) {
                        dir.mkdirs()
                    }
                }
            }
            file = File(folder, filename)
            withContext(Dispatchers.IO) {
                kotlin.runCatching {
                    file.createNewFile()
                    FileOutputStream(file).also { fostream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 80, fostream)
                        fostream.flush()
                        fostream.close()
                    }
                }
            }
        } catch (e: IOException) {
            Timber.e(e.localizedMessage, "IOException")
        }
        return file
    }
}