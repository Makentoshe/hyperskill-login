package org.hyperskill.login

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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
        }
    }
}