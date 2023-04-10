package ml.adlyq.retrofit2.proxy

import kotlinx.coroutines.runBlocking


fun main(): Unit = runBlocking {
    Api.instance.use { api ->
        api.testSuccess()
            .onSuccess {
                println("onSuccess: $it")
            }.onFailure {
                println("onFailure: $it")
            }
        api.testException()
            .onSuccess {
                println("onSuccess: $it")
            }.onFailure {
                println("onFailure: $it")
            }
        api.testOtherType()
            .onSuccess {
                println("onSuccess: $it")
            }.onFailure {
                println("onFailure: $it")
            }
    }
}