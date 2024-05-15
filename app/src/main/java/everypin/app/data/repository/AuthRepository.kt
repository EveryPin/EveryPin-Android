package everypin.app.data.repository

import everypin.app.core.constant.ProviderType
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(providerType: ProviderType, token: String): Flow<Unit>
}