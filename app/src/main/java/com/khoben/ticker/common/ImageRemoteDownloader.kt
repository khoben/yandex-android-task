package com.khoben.ticker.common

import android.graphics.Bitmap
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageRemoteDownloader {
    fun saveImage(bitmap: Bitmap, folder: String, filename: String): File? {
        var file: File? = null
        val dir = File(folder)
        try {
            if (!dir.exists()) {
                dir.mkdirs()
            }
            file = File(folder, filename)
            file.createNewFile()
            val ostream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, ostream)
            ostream.flush()
            ostream.close()
        } catch (e: IOException) {
            Timber.e(e.localizedMessage, "IOException")
        }
        return file
    }
}