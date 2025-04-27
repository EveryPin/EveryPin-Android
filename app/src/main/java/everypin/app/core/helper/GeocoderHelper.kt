package everypin.app.core.helper

import android.content.Context
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.util.Locale

class GeocoderHelper(
    context: Context
) {
    private val geocoder = Geocoder(context, Locale.getDefault())

    fun getAddressFromLocation(latitude: Double, longitude: Double): Flow<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            callbackFlow {
                geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
                ) {
                    val address = it.firstOrNull()?.getAddressLine(0) ?: ""
                    trySend(address)
                }

                awaitClose()
            }
        } else {
            flow {
                val address = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
                )?.firstOrNull()?.getAddressLine(0) ?: ""
                emit(address)
            }
        }
    }
}