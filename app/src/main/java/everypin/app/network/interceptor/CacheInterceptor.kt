package everypin.app.network.interceptor

import everypin.app.network.cache.Cacheable
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation

class CacheInterceptor: Interceptor {

    companion object {
        private const val HEADER_CACHE_CONTROL = "Cache-Control"
        private const val HEADER_PRAGMA = "Pragma"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request.tag(Invocation::class.java)?.method()
            ?.getAnnotation(Cacheable::class.java)
            ?.let { cacheable: Cacheable ->

                val cacheControl = CacheControl.Builder()
                    .maxStale(cacheable.value, cacheable.timeUnit)
                    .build()

                request = request.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build()
            }

        return chain.proceed(request)
    }
}