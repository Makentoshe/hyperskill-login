package org.hyperskill.login

import android.widget.Button
import android.widget.EditText
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

/** Interceptor for testing csrf token handling */
class Stage2TestInterceptor(private val cookieJar: CookieJar) : Interceptor {

    var wasUsed: Boolean = false
        private set

    private val httpUrl = HttpUrl.Builder().scheme("https").host("hyperskill.org").port(433).build()
    private val cookies = listOf(
            Cookie.Builder().name("csrftoken").value("OKt6yf1r24sPWo4Lsuj3pWAnn59JMGqlkcXj9w24FN9HMz6LN4UqqBWuHKrM0YD6").domain("hyperskill.org").path("/").build()
    )

    override fun intercept(chain: Interceptor.Chain) = if (chain.request().url.host != "hyperskill.org") {
        chain.proceed(chain.request())
    } else {
        cookieJar.saveFromResponse(httpUrl, cookies)
// TODO mb add html to resources and return it?
//      val responseString = String(javaClass.classLoader.getResourceAsStream("csrftoken_success").readBytes())
        wasUsed = true
        Response.Builder()
                .request(chain.request())
                .code(200)
                .body("{\"test\": true }".toResponseBody())
                .message("Mocked")
                .protocol(Protocol.HTTP_1_1).also { builder ->
                    cookies.forEach { cookie -> builder.addHeader("set-cookie", cookie.toString()) }
                }.build()
    }
}

@RunWith(RobolectricTestRunner::class)
class Stage2UnitTest {

    private val activityController = Robolectric.buildActivity(MainActivity::class.java)

    private lateinit var interceptor: Stage2TestInterceptor

    @Before
    fun before() {
        val activity = activityController.get()
        interceptor = Stage2TestInterceptor(activity.cookieJar)
        activity.client = OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Test
    fun testShouldStartNetworkRequestOnLoginClick() {
        val activity = activityController.setup().get()

        val email = activity.findViewById<EditText>(R.id.email_edit_text)
        val password = activity.findViewById<EditText>(R.id.password_edit_text)
        val login = activity.findViewById<Button>(R.id.login_button)

        email.setText("email")
        password.setText("password")
        login.performClick()

        Thread.sleep(1000)

        val message1 = "Url host was invalid. Should be: hyperskill.org"
        assertTrue(message1, interceptor.wasUsed)
    }
}