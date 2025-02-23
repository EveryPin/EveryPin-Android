package everypin.app.data.repository

import everypin.app.data.model.PostPin
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    fun getPinsByRange(lng: Double, lat: Double, range: Double): Flow<List<PostPin>>
}