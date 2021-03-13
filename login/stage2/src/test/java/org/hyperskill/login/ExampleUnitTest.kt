package org.hyperskill.login

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import okhttp3.HttpUrl
import okhttp3.Request
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {

    private val activityController = Robolectric.buildActivity(MainActivity::class.java)
    private val client = activityController.get().client

    private val interceptor = client.interceptors.find { it is Stage2TestInterceptor } as? Stage2TestInterceptor
            ?: throw IllegalStateException("${Stage2TestInterceptor::class.simpleName} was not added to the client")

    @Test
    fun testShouldCheckEmailEditTextExist() {
        interceptor.enable = false
        val activity = activityController.setup().get()

        val message = "does view with id \"email_edit_text\" placed in activity?"
        assertNotNull(message, activity.findViewById<EditText>(R.id.email_edit_text))
    }

    @Test
    fun testShouldCheckEmailEditTextHint() {
        interceptor.enable = false
        val activity = activityController.setup().get()

        val message = "in button property \"hint\""
        assertEquals(message, "Email", activity.findViewById<EditText>(R.id.email_edit_text).hint)
    }

    @Test
    fun testShouldCheckPasswordEditTextExist() {
        interceptor.enable = false
        val activity = activityController.setup().get()

        val message = "does view with id \"password_edit_text\" placed in activity?"
        assertNotNull(message, activity.findViewById<EditText>(R.id.password_edit_text))
    }

    @Test
    fun testShouldCheckPasswordEditTextHint() {
        interceptor.enable = false
        val activity = activityController.setup().get()

        val message = "in button property \"hint\""
        assertEquals(message, "Password", activity.findViewById<EditText>(R.id.password_edit_text).hint)
    }

    @Test
    fun testShouldCheckLoginButtonExist() {
        interceptor.enable = false
        val activity = activityController.setup().get()

        val message = "does view with id \"login_button\" placed in activity?"
        assertNotNull(message, activity.findViewById<Button>(R.id.login_button))
    }

    @Test
    fun testShouldCheckLoginButtonText() {
        interceptor.enable = false
        val activity = activityController.setup().get()

        val message = "in button property \"text\""
        assertEquals(message, "Login", activity.findViewById<Button>(R.id.login_button).text)
    }

    @Test
    fun testShouldCheckLoginProgressBarExist() {
        interceptor.enable = false
        val activity = activityController.setup().get()

        val message = "does view with id \"login_progress\" placed in activity?"
        assertNotNull(message, activity.findViewById<ProgressBar>(R.id.login_progress))
    }

    @Test
    fun testShouldCheckLoginProgressBarInitialVisibility() {
        interceptor.enable = false
        val activity = activityController.setup().get()

        val message = "view with id \"login_progress\" should be in GONE state on application launch"
        assertEquals(message, View.GONE, activity.findViewById<ProgressBar>(R.id.login_progress).visibility)
    }

    @Test
    fun testShouldCheckErrorMessageTextViewExist() {
        interceptor.enable = false
        val activity = activityController.setup().get()

        val message = "does view with id \"message_error\" placed in activity?"
        assertNotNull(message, activity.findViewById<TextView>(R.id.message_error))
    }

    @Test
    fun testShouldCheckErrorMessageTextViewInitialVisibility() {
        interceptor.enable = false
        val activity = activityController.setup().get()

        val message = "view with id \"message_error\" should be in GONE state on application launch"
        assertEquals(message, View.GONE, activity.findViewById<TextView>(R.id.message_error).visibility)
    }

    @Test
    fun testShouldShowErrorMessageOnBlankEmail() {
        interceptor.enable = false
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
        interceptor.enable = false
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
        interceptor.enable = false
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
        interceptor.enable = false
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
        interceptor.enable = false
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
        interceptor.enable = false
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

        val message1 = "Interceptor was not used."
        assertTrue(message1, interceptor.wasUsed)

        val message2 = "Url host was invalid. Should be: hyperskill.org"
        assertTrue(message2, interceptor.wasProperHost)
    }

}