package com.felix.mealplanner20.Meals.Data.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

private const val DEFAULT_MAX_BYTES = 1_000_000
private const val MAX_DIMENSION = 2048
private const val START_QUALITY = 85
private const val MIN_QUALITY = 40
private const val QUALITY_STEP = 10
private const val MIN_DIMENSION = 64

fun uriToScaledJpegBytes(
    context: Context,
    uri: Uri,
    maxBytes: Int = DEFAULT_MAX_BYTES,
): ByteArray? {
    return try {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, bounds)
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        val sampleOpts = BitmapFactory.Options().apply {
            inSampleSize = calcInSampleSize(bounds.outWidth, bounds.outHeight, MAX_DIMENSION)
        }
        var bitmap = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, sampleOpts)
        } ?: return null

        var quality = START_QUALITY
        var bytes: ByteArray
        while (true) {
            bytes = ByteArrayOutputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
                out.toByteArray()
            }
            if (bytes.size <= maxBytes) break
            if (quality > MIN_QUALITY) {
                quality -= QUALITY_STEP
                continue
            }
            val newW = bitmap.width / 2
            val newH = bitmap.height / 2
            if (newW < MIN_DIMENSION || newH < MIN_DIMENSION) break
            val scaled = Bitmap.createScaledBitmap(bitmap, newW, newH, true)
            if (scaled !== bitmap) bitmap.recycle()
            bitmap = scaled
            quality = START_QUALITY
        }
        bitmap.recycle()
        bytes
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun calcInSampleSize(width: Int, height: Int, maxDim: Int): Int {
    var sample = 1
    var w = width
    var h = height
    while (w / 2 >= maxDim || h / 2 >= maxDim) {
        w /= 2
        h /= 2
        sample *= 2
    }
    return sample
}
