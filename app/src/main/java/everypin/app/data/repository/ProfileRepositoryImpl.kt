package everypin.app.data.repository

import everypin.app.core.extension.toHttpError
import everypin.app.data.model.Profile
import everypin.app.network.api.ProfileApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileApi: ProfileApi
) : ProfileRepository {

    override fun getProfileMe(): Flow<Profile> = flow {
        val resp = profileApi.getProfileMe()
        val data = resp.body()

        if (resp.isSuccessful && data != null) {
            val profile = Profile(
                email = data.email,
                displayId = data.profileDisplayId,
                name = data.profileName,
                photoUrl = data.photoUrl,
                selfIntroduction = data.selfIntroduction ?: ""
            )
            emit(profile)
        } else {
            throw resp.toHttpError()
        }
    }

    override fun updateProfile(
        displayId: String,
        name: String,
        selfIntroduction: String,
        imagePath: String?
    ): Flow<Unit> = flow {
        val resp = if (imagePath == null) {
            profileApi.updateProfile(
                profileDisplayId = displayId,
                name = name,
                selfIntroduction = selfIntroduction
            )
        } else {
            val file = File(imagePath)
            val reqBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("PhotoFile", file.name, reqBody)

            profileApi.updateProfile(
                profileDisplayId = displayId,
                name = name,
                selfIntroduction = selfIntroduction,
                file = part
            )
        }

        if (resp.isSuccessful) {
            emit(Unit)
        } else {
            throw resp.toHttpError()
        }
    }
}