package everypin.app.data.repository

import everypin.app.core.extension.toHttpError
import everypin.app.data.model.Profile
import everypin.app.network.api.ProfileApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileApi: ProfileApi
) : ProfileRepository {
    override fun getProfileMe(): Flow<Profile> = flow {
        val resp = profileApi.getProfileMe()
        val data = resp.body()

        if (resp.isSuccessful && data != null) {
            val profile = Profile(
                id = data.userId,
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
}