package everypin.app

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import dagger.hilt.android.HiltAndroidApp
import everypin.app.core.utils.Logger

@HiltAndroidApp
class EveryPinApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        Logger.d("카카오 키해시: ${Utility.getKeyHash(this)}")
    }
}