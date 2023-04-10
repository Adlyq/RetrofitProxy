package ml.adlyq.retrofit2.proxy

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class Factory: Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? =
        when {
            annotations.any { it is Api.TestException } -> Converter {
                throw Exception("Api.TestException")
            }
            annotations.any { it is Api.TestSuccess } -> Converter {
                it.string()
            }
            annotations.any { it is Api.TestOtherType } -> Converter {
                it.string().split(".").map { it.toInt() }
            }
            else -> super.responseBodyConverter(type, annotations, retrofit)
        }


}