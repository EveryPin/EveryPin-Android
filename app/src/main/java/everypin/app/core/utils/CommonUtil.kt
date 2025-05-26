package everypin.app.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.core.net.toUri
import everypin.app.core.extension.getTempImagesDir
import logcat.asLog
import logcat.logcat
import java.io.File
import java.util.UUID
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object CommonUtil {
    /**
     * 위도, 경도, 거리(단위: m), 방위 값을 사용하여,
     * 해당 거리만큼 떨어진 곳의 위도와 경도를 계산한다.
     */
    fun calculateNewCoordinates(
        lat: Double,
        lng: Double,
        distance: Double,
        bearing: Double
    ): Pair<Double, Double> {
        val earthRadius = 6371000.0 // 지구 반지름 (단위: 미터)

        val latRad = Math.toRadians(lat)
        val lonRad = Math.toRadians(lng)
        val bearingRad = Math.toRadians(bearing)

        val newLatRad = asin(
            sin(latRad) * cos(distance / earthRadius) +
                    cos(latRad) * sin(distance / earthRadius) * cos(bearingRad)
        )
        val newLonRad = lonRad + atan2(
            sin(bearingRad) * sin(distance / earthRadius) * cos(latRad),
            cos(distance / earthRadius) - sin(latRad) * sin(newLatRad)
        )

        val newLat = Math.toDegrees(newLatRad)
        val newLon = Math.toDegrees(newLonRad)

        return Pair(newLat, newLon)
    }

    /**
     * 두 지점 사이의 거리를 계산한다.
     */
    fun calculateDistanceByCoordinates(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        val dx = x2 - x1
        val dy = y2 - y1
        return sqrt(dx.pow(2) + dy.pow(2))
    }

    fun createTempImage(context: Context, uri: Uri): Result<Uri> {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        val bitmap = ImageDecoder.decodeBitmap(source)
        val tempDir = context.getTempImagesDir()
        val imageFileName = "${UUID.randomUUID()}.jpg"
        val pickImageFile = File(tempDir, imageFileName)
        val outputStream = pickImageFile.outputStream()
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            return Result.success(pickImageFile.toUri())
        } catch (e: Exception) {
            logcat { e.asLog() }
            return Result.failure(e)
        } finally {
            outputStream.flush()
            outputStream.close()
            bitmap.recycle()
        }
    }
}