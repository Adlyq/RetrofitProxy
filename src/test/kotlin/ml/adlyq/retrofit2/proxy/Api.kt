package ml.adlyq.retrofit2.proxy

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET
import java.io.Closeable
import java.util.concurrent.TimeUnit

interface Api {

    @TestException
    @GET("/")
    suspend fun testException(): Result<String>

    @TestSuccess
    @GET("/")
    suspend fun testSuccess(): Result<String>

    @TestOtherType
    @GET("/")
    suspend fun testOtherType(): Result<String>


    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TestException

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TestSuccess

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TestOtherType

    companion object {
        class CloseableXywApi(
            private val client: OkHttpClient,
            private val api: Api
        ): Closeable, Api by api {
            override fun close() {
                client.dispatcher.executorService.shutdown()
            }
        }

        val instance by lazy { newInstance }

        val newInstance: CloseableXywApi
            get() {
                val client = OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .cookieJar(object: CookieJar {
                        val cs = mutableSetOf<Cookie>()
                        override fun loadForRequest(url: HttpUrl) = cs.toList()
                        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                            cs.addAll(cookies)
                        }
                    })
                    .build()

                val api = Retrofit.Builder()
                    .client(client)
                    .baseUrl("https://4.ipw.cn/")
                    .addConverterFactory(ProxyConverterFactory())
                    .addConverterFactory(Factory())
                    .build()
                    .create(Api::class.java).retrofitProxy()

                return CloseableXywApi(client, api)
            }
    }
}