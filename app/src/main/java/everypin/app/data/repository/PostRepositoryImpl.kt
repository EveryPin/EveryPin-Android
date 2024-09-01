package everypin.app.data.repository

import everypin.app.data.model.PhotoPost
import everypin.app.data.model.PostPin
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
    private val postApi: PostApi
) : PostRepository {
    override fun getRangePostPins(lng: Double, lat: Double, range: Int): Flow<List<PostPin>> = flow {
        val resp = postApi.getRangePosts(lng, lat, range)
        val data = resp.body()
        if (resp.isSuccessful) {
            val posts = data?.filter {
                it.postId != null && it.x != null && it.y != null && it.postPhotos != null
            }?.map {
                PostPin(
                    id = it.postId!!,
                    lat = it.y!!,
                    lng = it.x!!,
                    imageUrl = it.postPhotos!!.first().photoUrl!!
                )
            } ?: emptyList()
            emit(posts)
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

    override fun getPost(postId: Int): Flow<PhotoPost> = flow {
        val resp = postApi.getPost(postId)
        val data = resp.body()

        if (resp.isSuccessful && data != null) {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val createdDate = LocalDateTime.parse(data.createdDate, formatter)
            val post = PhotoPost(
                id = data.postId!!,
                content = data.postContent!!,
                createdDate = createdDate,
                latitude = data.x!!,
                longitude = data.y!!,
                photoUrls = data.postPhotos!!.map { it.photoUrl!! }
            )
            emit(post)
        } else {
            throw HttpException(resp)
        }
    }.flowOn(Dispatchers.IO)
}