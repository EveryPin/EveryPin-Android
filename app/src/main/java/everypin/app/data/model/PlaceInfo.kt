package everypin.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceInfo(
    val placeName: String,
    val addressName: String,
    val lat: Double,
    val lng: Double
): Parcelable
