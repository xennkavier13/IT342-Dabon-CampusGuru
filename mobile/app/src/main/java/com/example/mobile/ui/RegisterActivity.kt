package com.example.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mobile.R
import com.example.mobile.ui.viewmodel.AuthViewModel
import com.example.mobile.ui.viewmodel.AuthViewModelFactory
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        viewModel = ViewModelProvider(this, AuthViewModelFactory(applicationContext))[AuthViewModel::class.java]

        val firstNameInput = findViewById<TextInputEditText>(R.id.etFirstName)
        val lastNameInput = findViewById<TextInputEditText>(R.id.etLastName)
        val emailInput = findViewById<TextInputEditText>(R.id.etEmail)
        val passwordInput = findViewById<TextInputEditText>(R.id.etPassword)
        val confirmPasswordInput = findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val roleGroup = findViewById<RadioGroup>(R.id.rgRole)
        val registerButton = findViewById<Button>(R.id.btnRegister)
        val loginRedirect = findViewById<TextView>(R.id.tvGoToLogin)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        registerButton.setOnClickListener {
            val role = if (roleGroup.checkedRadioButtonId == R.id.rbTutor) "TUTOR" else "LEARNER"
            viewModel.register(
                firstName = firstNameInput.text?.toString().orEmpty(),
                lastName = lastNameInput.text?.toString().orEmpty(),
                institutionalEmail = emailInput.text?.toString().orEmpty(),
                password = passwordInput.text?.toString().orEmpty(),
                confirmPassword = confirmPasswordInput.text?.toString().orEmpty(),
                role = role
            )
        }

        loginRedirect.setOnClickListener {
            finish()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            registerButton.isEnabled = !isLoading
        }

        viewModel.errorMessage.observe(this) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                viewModel.consumeError()
            }
        }

        viewModel.authResult.observe(this) { response ->
            if (response != null) {
                Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}
