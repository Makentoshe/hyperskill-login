package org.hyperskill.login

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

private class DisableOkHttpClientInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain) = Response.Builder()
            .request(chain.request())
            .code(200)
            .body("{\"test\": true }".toResponseBody())
            .message("Disabled")
            .protocol(Protocol.HTTP_1_1)
            .build()
}

@RunWith(RobolectricTestRunner::class)
class Stage1UnitTest {

    private val activityController = Robolectric.buildActivity(MainActivity::class.java)

    @Before
    fun before() {
        activityController.get().client = OkHttpClient.Builder().addInterceptor(DisableOkHttpClientInterceptor()).build()
    }

    @Test
    fun testShouldCheckEmailEditTextExist() {
        val activity = activityController.setup().get()

        val message = "does view with id \"email_edit_text\" placed in activity?"
        assertNotNull(message, activity.findViewById<EditText>(R.id.email_edit_text))
    }

    @Test
    fun testShouldCheckEmailEditTextHint() {
        val activity = activityController.setup().get()

        val message = "in button property \"hint\""
        assertEquals(message, "Email", activity.findViewById<EditText>(R.id.email_edit_text).hint)
    }

    @Test
    fun testShouldCheckPasswordEditTextExist() {
        val activity = activityController.setup().get()

        val message = "does view with id \"password_edit_text\" placed in activity?"
        assertNotNull(message, activity.findViewById<EditText>(R.id.password_edit_text))
    }

    @Test
    fun testShouldCheckPasswordEditTextHint() {
        val activity = activityController.setup().get()

        val message = "in button property \"hint\""
        assertEquals(message, "Password", activity.findViewById<EditText>(R.id.password_edit_text).hint)
    }

    @Test
    fun testShouldCheckLoginButtonExist() {
        val activity = activityController.setup().get()

        val message = "does view with id \"login_button\" placed in activity?"
        assertNotNull(message, activity.findViewById<Button>(R.id.login_button))
    }

    @Test
    fun testShouldCheckLoginButtonText() {
        val activity = activityController.setup().get()

        val message = "in button property \"text\""
        assertEquals(message, "Login", activity.findViewById<Button>(R.id.login_button).text)
    }

    @Test
    fun testShouldCheckLoginProgressBarExist() {
        val activity = activityController.setup().get()

        val message = "does view with id \"login_progress\" placed in activity?"
        assertNotNull(message, activity.findViewById<ProgressBar>(R.id.login_progress))
    }

    @Test
    fun testShouldCheckLoginProgressBarInitialVisibility() {
        val activity = activityController.setup().get()

        val message = "view with id \"login_progress\" should be in GONE state on application launch"
        assertEquals(message, View.GONE, activity.findViewById<ProgressBar>(R.id.login_progress).visibility)
    }

    @Test
    fun testShouldCheckErrorMessageTextViewExist() {
        val activity = activityController.setup().get()

        val message = "does view with id \"message_error\" placed in activity?"
        assertNotNull(message, activity.findViewById<TextView>(R.id.message_error))
    }

    @Test
    fun testShouldCheckErrorMessageTextViewInitialVisibility() {
        val activity = activityController.setup().get()

        val message = "view with id \"message_error\" should be in GONE state on application launch"
        assertEquals(message, View.GONE, activity.findViewById<TextView>(R.id.message_error).visibility)
    }

    @Test
    fun testShouldShowErrorMessageOnBlankEmail() {
        val activity = activityController.setup().get()

        val email = activity.findViewById<EditText>(R.id.email_edit_text)
        val password = activity.findViewById<EditText>(R.id.password_edit_text)
        val login = activity.findViewById<Button>(R.id.login_button)
        val message = activity.findViewById<TextView>(R.id.message_error)

        email.setText("\n \t   ")
        password.setText("password")
        login.performClick()

        val message1 = "does view \"email_edit_text\" checks on blank condition?"
        assertEquals(message1, message.text, "Email field should not be empty")
    }

    @Test
    fun testShouldShowErrorMessageOnEmptyEmail() {
        val activity = activityController.setup().get()

        val password = activity.findViewById<EditText>(R.id.password_edit_text)
        val login = activity.findViewById<Button>(R.id.login_button)
        val message = activity.findViewById<TextView>(R.id.message_error)

        password.setText("password")
        login.performClick()

        val message1 = "does view \"email_edit_text\" checks on blank condition?"
        assertEquals(message1, message.text, "Email field should not be empty")
    }

    @Test
    fun testShouldShowErrorMessageOnEmptyPassword() {
        val activity = activityController.setup().get()

        val email = activity.findViewById<EditText>(R.id.email_edit_text)
        val login = activity.findViewById<Button>(R.id.login_button)
        val message = activity.findViewById<TextView>(R.id.message_error)

        email.setText("email")
        login.performClick()

        val message1 = "does view \"password_edit_text\" checks on blank condition?"
        assertEquals(message1, message.text, "Password field should not be empty")
    }

    @Test
    fun testShouldShowErrorMessageOnBlankPassword() {
        val activity = activityController.setup().get()

        val email = activity.findViewById<EditText>(R.id.email_edit_text)
        val password = activity.findViewById<EditText>(R.id.password_edit_text)
        val login = activity.findViewById<Button>(R.id.login_button)
        val message = activity.findViewById<TextView>(R.id.message_error)

        email.setText("email")
        password.setText(" \n \t   ")
        login.performClick()

        val message1 = "does view \"password_edit_text\" checks on blank condition?"
        assertEquals(message1, message.text, "Password field should not be empty")
    }

    @Test
    fun testShouldShowProgressBarOnLoginClick() {
        val activity = activityController.setup().get()

        val email = activity.findViewById<EditText>(R.id.email_edit_text)
        val password = activity.findViewById<EditText>(R.id.password_edit_text)
        val login = activity.findViewById<Button>(R.id.login_button)
        val progress = activity.findViewById<ProgressBar>(R.id.login_progress)

        email.setText("email")
        password.setText("password")
        login.performClick()

        val message1 = "does view \"login_progress\" visible on login click?"
        assertEquals(message1, View.VISIBLE, progress.visibility)
    }

    @Test
    fun testShouldDisableLoginButtonOnLoginClick() {
        val activity = activityController.setup().get()

        val email = activity.findViewById<EditText>(R.id.email_edit_text)
        val password = activity.findViewById<EditText>(R.id.password_edit_text)
        val login = activity.findViewById<Button>(R.id.login_button)
        val progress = activity.findViewById<ProgressBar>(R.id.login_progress)
        val message = activity.findViewById<TextView>(R.id.message_error)

        email.setText("email")
        password.setText("password")
        login.performClick()

        val message1 = "does view \"login_button\" disabled on login click?"
        assertEquals(message1, false, login.isEnabled)
    }
}