package everypin.app.core.extension

import everypin.app.core.error.HttpError
import retrofit2.Response

fun <T> Response<T>.toHttpError(): HttpError {
    return HttpError(
        message = this.message(),
        code = this.code(),
        headers = this.headers().toMultimap(),
        body = this.errorBody()?.string()
    )
}