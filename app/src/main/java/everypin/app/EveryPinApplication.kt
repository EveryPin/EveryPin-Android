package everypin.app

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import everypin.app.core.utils.Logger

@HiltAndroidApp
class EveryPinApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}