package everypin.app.data.repository

import everypin.app.core.extension.toHttpError
import everypin.app.core.utils.Logger
import everypin.app.data.model.PostDetail
import everypin.app.network.api.PostApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postApi: PostApi
) : PostRepository {
    override fun writePost(
        content: String,
        lat: Double,
        lng: Double,
        imagePaths: List<String>
    ): Flow<Unit> = flow {
        val resp = postApi.writePost(
            postContent = content.toRequestBody("text/plain".toMediaTypeOrNull()),
            x = lng.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            y = lat.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            files = imagePaths.map {
                val file = File(it)
                val reqBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("photoFiles", file.name, reqBody)
            }
        )
        if (resp.isSuccessful) {
            Logger.d("게시글 생성됨. post: ${resp.body()}")
            emit(Unit)
        } else {
            throw resp.toHttpError()
        }
    }.flowOn(Dispatchers.IO)

    override fun getPost(postId: Int): Flow<PostDetail> = flow {
        val resp = postApi.getPost(postId)
        val data = resp.body()

        if (resp.isSuccessful && data != null) {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val createdDate = LocalDateTime.parse(data.createdDate, formatter)
            val post = PostDetail(
                id = data.postId!!,
                name = data.name ?: "알 수 없음",
                content = data.postContent!!,
                createdDate = createdDate,
                latitude = data.y!!,
                longitude = data.x!!,
                photoUrls = data.postPhotos!!.map { it.photoUrl!! },
                likeCount = data.likeCount ?: 0,
                userId = data.userId ?: "null"
            )
            emit(post)
        } else {
            throw resp.toHttpError()
        }
    }.flowOn(Dispatchers.IO)
}