package org.hyperskill.login

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), RequireOkHttpClient, RequireCookieJar {

    private val customCookieJar = CustomCookieJar()
    override val cookieJar: CookieJar
        get() = customCookieJar

    override var client = OkHttpClient.Builder().cookieJar(customCookieJar).build()

    private val thread = thread(start = false, isDaemon = false) {
        val tokenRequest = Request.Builder().url("https://hyperskill.org").build()
        val tokenResponse = client.newCall(tokenRequest).execute()
        val token = customCookieJar.cookies.values.flatten().find { it.name == "csrftoken" }!!.value

        val email = email_edit_text.text.toString()
        val password = password_edit_text.text.toString()

        val mediaType = "application/json".toMediaType()
        val requestBody = Gson().toJson(LoginCredentials(email, password)).toRequestBody(mediaType)
        val loginRequest = Request.Builder().post(requestBody).url("https://hyperskill.org/api/profiles/login")
                .addHeader("X-CSRFToken", token)
                .addHeader("Referer", "https://hyperskill.org")
                .build()
        val loginResponse = client.newCall(loginRequest).execute()

        runOnUiThread {
            login_button.isEnabled = true
            login_progress.visibility = View.GONE
        }

        if ((loginResponse.code / 100) == 2) {
            println(loginResponse.body?.string())
        } else {
            message_error.text = loginResponse.body?.string()
            message_error.visibility = View.VISIBLE
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login_button.setOnClickListener {
            if (email_edit_text.text.isBlank()) {
                message_error.visibility = View.VISIBLE
                message_error.setText(R.string.error_empty_email)
                return@setOnClickListener
            }
            if (password_edit_text.text.isBlank()) {
                message_error.visibility = View.VISIBLE
                message_error.setText(R.string.error_empty_password)
                return@setOnClickListener
            }

            message_error.visibility = View.GONE
            login_progress.visibility = View.VISIBLE
            login_button.isEnabled = false

            thread.start()
        }
    }

    private class CustomCookieJar : CookieJar {

        val cookies = HashMap<HttpUrl, ArrayList<Cookie>>()

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookies.values.flatten()
        }

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            this.cookies[url] = (this.cookies[url] ?: ArrayList()).apply { addAll(cookies) }
        }
    }

}