package everypin.app.data.repository

import everypin.app.core.extension.toHttpError
import everypin.app.data.model.PostPin
import everypin.app.network.api.MapApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(
    private val mapApi: MapApi
) : MapRepository {
    override fun getPinsByRange(lng: Double, lat: Double, range: Double): Flow<List<PostPin>> = flow {
        val resp = mapApi.getPinsByRange(lng, lat, range)
        val data = resp.body()
        if (resp.isSuccessful && data != null) {
            val posts = data.map {
                PostPin(
                    id = it.postId,
                    lat = it.y,
                    lng = it.x,
                    imageUrl = it.postPhotos.first().photoUrl
                )
            }
            emit(posts)
        } else {
            throw resp.toHttpError()
        }
    }.flowOn(Dispatchers.IO)
}