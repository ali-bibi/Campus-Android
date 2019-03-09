package de.tum.`in`.tumcampusapp.api.app

import de.tum.`in`.tumcampusapp.api.app.exception.ChaosMonkeyException
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.ThreadLocalRandom

/**
 * This is a OkHttp Interceptor designed to remind developers, that network requests can and *will*
 * spontaneously fail.
 * If this Interceptor is in the stacktrace you're looking at, you probably didn't gracefully
 * handle failing network conditions
 */
class ChaosMonkeyInterceptor : Interceptor {

    private val failProbability = 0.01

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (ThreadLocalRandom.current().nextDouble() < failProbability) {
            Timber.d("Chaos Monkey is resilience testing your network code")
            val url = chain.call().request().url().toString()
            throw ChaosMonkeyException(url)
        }
        return chain.proceed(chain.request())
    }

}
