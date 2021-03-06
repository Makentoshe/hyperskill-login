package org.hyperskill.login

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val cookieJar = CustomCookieJar()
    private val client = OkHttpClient.Builder().cookieJar(cookieJar).build()
    private val thread = thread(start = false, isDaemon = true) {
        val request = Request.Builder().url("https://hyperskill.org").build()
        val response = client.newCall(request).execute()
        println(response)
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

        private val cookies = HashMap<HttpUrl, ArrayList<Cookie>>()

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookies.values.flatten()
        }

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            this.cookies[url] = (this.cookies[url] ?: ArrayList()).apply { addAll(cookies) }
        }
    }

}