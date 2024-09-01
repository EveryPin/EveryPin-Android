package everypin.app.data.repository

import everypin.app.data.model.PhotoPost
import everypin.app.data.model.PostPin
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getRangePostPins(lng: Double, lat: Double, range: Int): Flow<List<PostPin>>
    fun writePost(
        content: String,
        lat: Double,
        lng: Double,
        imagePaths: List<String>
    ): Flow<Unit>

    fun getPost(postId: Int): Flow<PhotoPost>
}