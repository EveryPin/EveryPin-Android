package everypin.app.core.utils

import android.content.Context
import java.io.File

object FileUtil {
    private const val TEMP = "temp"

    fun getTempDirectory(context: Context): File {
        val tempDir = File(context.cacheDir, TEMP)
        if (!tempDir.exists()) {
            tempDir.mkdir()
        }
        return tempDir
    }

    fun deleteTempDirectory(context: Context): Boolean {
        return try {
            val tempDir = File(context.cacheDir, TEMP)
            tempDir.deleteRecursively()
        } catch (e: Exception) {
            Logger.e(e.message.toString())
            false
        }
    }
}