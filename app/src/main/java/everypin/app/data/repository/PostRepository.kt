package everypin.app.data.repository

import everypin.app.data.model.PostDetail
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun writePost(
        content: String,
        lat: Double,
        lng: Double,
        imagePaths: List<String>
    ): Flow<Unit>

    fun getPost(postId: Int): Flow<PostDetail>
}