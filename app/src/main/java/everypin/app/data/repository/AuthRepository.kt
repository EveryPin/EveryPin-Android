package everypin.app.data.repository

import com.google.firebase.auth.FirebaseUser
import everypin.app.core.constant.ProviderType
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(providerType: ProviderType, accessToken: String, fcmToken: String): Flow<Unit>
    fun createFirebaseUser(
        userId: String,
        name: String,
        profileImageUrl: String?
    ): Flow<FirebaseUser>
}