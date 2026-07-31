package com.example.xml_app.utils.validation

import android.util.Patterns
import com.example.xml_app.utils.dto.ValidationResult

class LoginValidation {

    fun validateEmail(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Enter a valid email"
            )
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Enter a valid email"
            )
        }

        return ValidationResult(true)
    }

    fun validatePassword(password: String): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Enter a password"
            )
        }

        if (password.length < 3) {
            return ValidationResult(
                successful = false,
                errorMessage = "Enter a valid password"
            )
        }

        return ValidationResult(true)
    }
}