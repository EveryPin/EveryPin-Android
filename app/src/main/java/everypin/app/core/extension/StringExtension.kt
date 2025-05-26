package everypin.app.core.extension

fun String.checkDisplayId(): Boolean {
    val regex = Regex("^[a-zA-Z0-9._]{1,29}$")
    return regex.matches(this)
}

fun String.checkName(): Boolean {
    val regex = Regex("^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣]{1,29}$")
    return regex.matches(this)
}