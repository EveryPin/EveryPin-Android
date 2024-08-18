package everypin.app.data.repository

import everypin.app.data.model.PostModel
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getPostList(): Flow<List<PostModel>>
    fun writePost(
        content: String,
        lat: Double,
        lng: Double,
        imagePaths: List<String>
    ): Flow<Unit>

    fun getPost(postId: Int): Flow<PostModel>
}