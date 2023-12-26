package everypin.app

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EveryPinApplication : Application() {
    companion object {
        private var _DEBUG: Boolean = false
        val DEBUG get() = _DEBUG
    }

    override fun onCreate() {
        super.onCreate()
        _DEBUG = isDebuggable()

        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }

    private fun isDebuggable(): Boolean {
        val debuggable: Boolean

        try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            debuggable = (0 != (appInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE))
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }

        return debuggable
    }
}