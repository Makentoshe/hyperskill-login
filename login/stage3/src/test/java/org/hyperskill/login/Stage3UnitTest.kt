package org.hyperskill.login

import android.os.Looper.getMainLooper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.google.gson.Gson
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode


/** Interceptor for testing csrf token handling */
class Stage3TestInterceptor(private val cookieJar: CookieJar) : Interceptor {

    // Not just a plain checker but a indicator of token request
    // If token was requested previously - value will be true and false otherwise
    var wasUsed: Boolean = false
        private set

    private val httpUrl = HttpUrl.Builder().scheme("https").host("hyperskill.org").port(433).build()
    private val cookies = listOf(
            Cookie.Builder().name("csrftoken").value("OKt6yf1r24sPWo4Lsuj3pWAnn59JMGqlkcXj9w24FN9HMz6LN4UqqBWuHKrM0YD6").domain("hyperskill.org").path("/").build()
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return if (request.url.host == "hyperskill.org") {
            if (!wasUsed) { // if token was not requested yet
                interceptCsrftoken(chain)
            } else {
                interceptLogin(chain)
            }
        } else chain.proceed(chain.request())
    }

    private fun interceptCsrftoken(chain: Interceptor.Chain): Response {
        cookieJar.saveFromResponse(httpUrl, cookies)
        val body = String(javaClass.classLoader.getResourceAsStream("csrftoken_success.html").readBytes())
        wasUsed = true
        return Response.Builder()
                .request(chain.request())
                .code(200)
                .body(body.toResponseBody())
                .message("Mocked")
                .protocol(Protocol.HTTP_1_1).also { builder ->
                    cookies.forEach { cookie -> builder.addHeader("set-cookie", cookie.toString()) }
                }.build()
    }

    private fun interceptLogin(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val credentials = Buffer().apply {
            request.newBuilder().build().body!!.writeTo(this)
        }.readUtf8().let { Gson().fromJson(it, LoginCredentials::class.java) }

        return if (credentials.email == "email" && credentials.password == "password") {
            interceptLoginCorrect(chain)
        } else {
            interceptLoginFailure(chain)
        }
    }

    private fun interceptLoginCorrect(chain: Interceptor.Chain): Response {
        val responseBody = String(javaClass.classLoader.getResourceAsStream("login_success.json").readBytes())
        return Response.Builder()
                .request(chain.request())
                .code(200)
                .body(responseBody.toResponseBody())
                .message("Mocked")
                .protocol(Protocol.HTTP_1_1).also { builder ->
                    cookies.forEach { cookie -> builder.addHeader("set-cookie", cookie.toString()) }
                }.build()
    }

    private fun interceptLoginFailure(chain: Interceptor.Chain): Response {
        val responseBody = String(javaClass.classLoader.getResourceAsStream("login_failure.json").readBytes())
        return Response.Builder()
                .request(chain.request())
                .code(401)
                .body(responseBody.toResponseBody())
                .message("Mocked")
                .protocol(Protocol.HTTP_1_1).also { builder ->
                    cookies.forEach { cookie -> builder.addHeader("set-cookie", cookie.toString()) }
                }.build()
    }
}

// TODO Refactor each test end: remove last assert
@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class Stage3UnitTest {

    private val activityController = Robolectric.buildActivity(MainActivity::class.java)

    private lateinit var interceptor: Stage3TestInterceptor

    @Before
    fun before() {
        val activity = activityController.get()
        interceptor = Stage3TestInterceptor(activity.cookieJar)
        activity.client = OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Test
    fun testShouldCheckFailureLoginResponse() {
        val activity = activityController.setup().get()

        val email = activity.findViewById<EditText>(R.id.email_edit_text)
        val password = activity.findViewById<EditText>(R.id.password_edit_text)
        val login = activity.findViewById<Button>(R.id.login_button)
        val progress = activity.findViewById<ProgressBar>(R.id.login_progress)
        val message = activity.findViewById<TextView>(R.id.message_error)

        email.setText("email")
        password.setText("wrongpassword")
        login.performClick()

        Thread.sleep(1000)

        val message1 = "Url host was invalid. Should be: hyperskill.org"
        assertTrue(message1, interceptor.wasUsed)

        shadowOf(getMainLooper()).idle()
        val message2 = "Login button should be enabled after networking"
        assertTrue(message2, login.isEnabled)

        val message3 = "ProgressBar should be hidden after networking"
        assertNotEquals(message3, View.VISIBLE, progress.visibility)

        val message4 = "Login response should be placed in the message_error TextView"
        val string = String(javaClass.classLoader.getResourceAsStream("login_failure.json").readBytes())
        assertEquals(message4, string, message.text)
    }

    @Test
    fun testShouldCheckSuccessLoginResponse() {
        val activity = activityController.setup().get()

        val email = activity.findViewById<EditText>(R.id.email_edit_text)
        val password = activity.findViewById<EditText>(R.id.password_edit_text)
        val login = activity.findViewById<Button>(R.id.login_button)
        val progress = activity.findViewById<ProgressBar>(R.id.login_progress)
        val message = activity.findViewById<TextView>(R.id.message_error)

        email.setText("email")
        password.setText("password")
        login.performClick()

        Thread.sleep(1000)

        val message1 = "Url host was invalid. Should be: hyperskill.org"
        assertTrue(message1, interceptor.wasUsed)

        shadowOf(getMainLooper()).idle()
        val message2 = "Login button should be enabled after networking"
        assertTrue(message2, login.isEnabled)

        val message3 = "ProgressBar should be hidden after networking"
        assertNotEquals(message3, View.VISIBLE, progress.visibility)

        val message4 = "Login response should be placed in the message_error TextView"
        val string = String(javaClass.classLoader.getResourceAsStream("login_success.json").readBytes())
        assertEquals(message4, string, message.text)
    }
}