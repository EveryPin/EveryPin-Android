package everypin.app.core.utils

import android.util.Log
import everypin.app.EveryPinApplication

object Logger {
    private const val TAG = "EveryPinLogger"

    fun e(message: String, throwable: Throwable? = null) {
        if (EveryPinApplication.DEBUG) {
            Log.e(TAG, buildLogMessage(message), throwable)
        }
    }

    fun w(message: String) {
        if (EveryPinApplication.DEBUG) Log.w(TAG, buildLogMessage(message))
    }

    fun i(message: String) {
        if (EveryPinApplication.DEBUG) Log.i(TAG, buildLogMessage(message))
    }

    fun d(message: String) {
        if (EveryPinApplication.DEBUG) Log.d(TAG, buildLogMessage(message))
    }

    fun v(message: String) {
        if (EveryPinApplication.DEBUG) Log.v(TAG, buildLogMessage(message))
    }

    private fun buildLogMessage(message: String): String {
        val ste = Thread.currentThread().stackTrace[4]
        val sb = StringBuilder()

        with(sb) {
            append("[")
            append(ste.fileName.replace(".kt", ""))
            append("::")
            append(ste.methodName)
            append("]")
            append(" ")
            append(message)
        }

        return sb.toString()
    }
}