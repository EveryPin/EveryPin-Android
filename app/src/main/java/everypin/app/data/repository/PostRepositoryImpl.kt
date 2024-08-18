package everypin.app.data.repository

import android.content.Context
import androidx.core.net.toFile
import dagger.hilt.android.qualifiers.ApplicationContext
import everypin.app.data.model.PostModel
import everypin.app.network.api.PostApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postApi: PostApi,
    @ApplicationContext private val context: Context
) : PostRepository {
    override fun getPostList(): Flow<List<PostModel>> = flow {
        val resp = postApi.getPostList()
        val data = resp.body()
        if (data != null) {
            val postList = data.map {
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                val createdDate = LocalDateTime.parse(it.createdDate, formatter)
                PostModel(
                    id = it.postId,
                    content = it.postContent,
                    createdDate = createdDate,
                    latitude = it.y,
                    longitude = it.x
                )
            }
            emit(postList)
        } else {
            throw HttpException(resp)
        }
    }.flowOn(Dispatchers.IO)

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
                MultipartBody.Part.createFormData("PhotoFiles", file.name, reqBody)
            }
        )
        if (resp.isSuccessful) {
            emit(Unit)
        } else {
            throw HttpException(resp)
        }
    }.flowOn(Dispatchers.IO)

    override fun getPost(postId: Int): Flow<PostModel> = flow {
        val resp = postApi.getPost(postId)
        val data = resp.body()

        if (data != null) {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val createdDate = LocalDateTime.parse(data.createdDate, formatter)
            val post = PostModel(
                id = data.postId,
                content = data.postContent,
                createdDate = createdDate,
                latitude = data.x,
                longitude = data.y
            )
            emit(post)
        } else {
            throw HttpException(resp)
        }
    }.flowOn(Dispatchers.IO)
}