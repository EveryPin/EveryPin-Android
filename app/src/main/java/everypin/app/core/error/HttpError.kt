package everypin.app.core.error

class HttpError(
    message: String,
    val code: Int,
    val headers: Map<String, List<String>>,
    val body: String?
): Exception(message)