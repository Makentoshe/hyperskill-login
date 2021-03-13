package org.hyperskill.login

import okhttp3.Interceptor
import okhttp3.Response

// Interceptor for testing purpose.
// If you delete this class: most tests may not properly run or execute
class Stage2TestInterceptor : Interceptor {

    var wasUsed: Boolean = false
        private set

    var wasProperHost: Boolean = true
        private set

    var enable: Boolean = true

    override fun intercept(chain: Interceptor.Chain): Response {
        return if (enable) {
            wasUsed = true
            if (chain.request().url.host != "hyperskill.org") {
                wasProperHost = false
            }
            chain.proceed(chain.request())
        } else {
            Response.Builder().message("Disabled").build()
        }
    }
}