package everypin.app.core.utils

import android.util.Log
import everypin.app.BuildConfig
import everypin.app.EveryPinApplication

object Logger {
    private const val TAG = "EveryPinLogger"

    fun e(message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, buildLogMessage(message), throwable)
        }
    }

    fun w(message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, buildLogMessage(message), throwable)
        }
    }

    fun i(message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, buildLogMessage(message), throwable)
        }
    }

    fun d(message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, buildLogMessage(message), throwable)
        }
    }

    fun v(message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, buildLogMessage(message), throwable)
        }
    }

    private fun buildLogMessage(message: String): String {
        val ste = Thread.currentThread().stackTrace[4]
        val sb = StringBuilder()

        with(sb) {
            append(message)
            append(" ")
            append("(")
            append(ste.fileName)
            append(":")
            append(ste.lineNumber)
            append(")")
            append("#")
            append(ste.methodName)
        }

        return sb.toString()
    }
}