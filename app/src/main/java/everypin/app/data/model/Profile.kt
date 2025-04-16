package everypin.app.data.model

data class Profile(
    val id: String,
    val displayId: String,
    val name: String,
    val photoUrl: String?,
    val selfIntroduction: String
)
