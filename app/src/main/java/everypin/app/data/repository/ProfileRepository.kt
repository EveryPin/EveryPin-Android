package everypin.app.data.repository

import everypin.app.data.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfileMe(): Flow<Profile>
}