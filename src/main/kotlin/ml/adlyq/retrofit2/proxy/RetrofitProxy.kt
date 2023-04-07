package ml.adlyq.retrofit2.proxy

import java.lang.reflect.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.intercepted

inline fun <reified T> T.retrofitProxy(): T {
    val retrofitHandler = Proxy.getInvocationHandler(this)
    return Proxy.newProxyInstance(
        T::class.java.classLoader, arrayOf(T::class.java)
    ) { proxy, method, args ->
        method.takeIf { it.isSuspendMethod }
            ?.let {
                @Suppress("UNCHECKED_CAST")
                args[args.lastIndex] =
                    FakeSuccessContinuationWrapper(
                        (args.last() as Continuation<Any>).intercepted()
                    )
            }

        retrofitHandler.invoke(proxy, method, args)
    } as T
}

val Method.isSuspendMethod: Boolean
    get() = genericParameterTypes.lastOrNull()
        ?.let { it as? ParameterizedType }?.rawType == Continuation::class.java

val Method.suspendReturnType: Type?
    get() = genericParameterTypes.lastOrNull()
        ?.let { it as? ParameterizedType }?.actualTypeArguments?.firstOrNull()
        ?.let { it as? WildcardType }?.lowerBounds?.firstOrNull()

class FakeSuccessContinuationWrapper(
    private val original: Continuation<Any>
): Continuation<Any> {
    override val context = original.context

    override fun resumeWith(result: Result<Any>) {
        if (result.isSuccess)
            original.resumeWith(result)
        else
            original.resumeWith(Result.success(result))
    }

}
