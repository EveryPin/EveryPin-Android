package everypin.app.data.repository

import everypin.app.data.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfileMe(): Flow<Profile>
    fun updateProfile(
        displayId: String,
        name: String,
        selfIntroduction: String,
        imagePath: String? = null,
    ): Flow<Unit>
}