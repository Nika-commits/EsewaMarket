package com.example.xml_app.utils.validation

import android.util.Patterns
import com.example.xml_app.utils.dto.ValidationResult

class RegisterValidation {

    fun validateFullName(fullname: String): ValidationResult {
        if (fullname.isBlank() || fullname.length < 3) {
            return ValidationResult(
                successful = false,
                errorMessage = "Enter a longer name"
            )
        }
        return ValidationResult(true)
    }

    fun validateUsername(username: String): ValidationResult {
        if (username.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Username cannot be empty"
            )
        }

        if (username.length < 3) {
            return ValidationResult(
                successful = false,
                errorMessage = "Username must be longer"
            )
        }

        return ValidationResult(true)
    }


    fun validateEmail(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Email cannot be empty"
            )
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Enter a valid email address"
            )
        }

        return ValidationResult(true)
    }


    fun validatePassword(password: String): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Password cannot be empty"
            )
        }

        if (password.length < 8) {
            return ValidationResult(
                successful = false,
                errorMessage = "Enter a longer password"
            )
        }

        val containsDigits = password.any { it.isDigit() } && password.any { it.isLetter() }

        if (!containsDigits) {
            return ValidationResult(
                successful = false,
                errorMessage = "Password should contain both number and letters"
            )
        }
        return ValidationResult(true)

    }
}