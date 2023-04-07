package ml.adlyq.retrofit2.proxy

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

class ProxyConverterFactory: Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? =
        (type as? ParameterizedType)?.takeIf {
            it.rawType == Result::class.java
        }?.let {
            val inType = it.actualTypeArguments.first().let { inType ->
                when (inType) {
                    //is GenericArrayType -> "GenericArrayType $inType"
                    //is ParameterizedType -> "ParameterizedType $inType"
                    //is TypeVariable<*> -> "TypeVariable $inType"
                    is WildcardType -> inType.upperBounds.first()
                    else -> inType

                }
            }
            val nextCon = retrofit.nextResponseBodyConverter<Any?>(
                    this,
                    inType,
                    annotations
                )
            Converter {
                Result.success(nextCon.convert(it))
            }
        }
}