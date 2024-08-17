package everypin.app.data.repository

import android.net.Uri
import everypin.app.data.model.PostModel
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getPostList(): Flow<List<PostModel>>
    fun writePost(
        content: String,
        lat: Double,
        lng: Double,
        imageUris: List<Uri>
    ): Flow<Unit>

    fun getPost(postId: Int): Flow<PostModel>
}