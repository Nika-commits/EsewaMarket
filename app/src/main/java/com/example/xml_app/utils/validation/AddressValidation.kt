package com.example.xml_app.utils.validation

import com.example.xml_app.utils.dto.ValidationResult

class AddressValidation {
    fun validateFullName(fullName: String): ValidationResult {
        val firstAndLastNames = fullName.trim().split(Regex("\\s+"))

        if (firstAndLastNames.size < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = "Enter your complete name. (Eg: John Doe)"
            )
        }

        if (firstAndLastNames.size > 5) {
            return ValidationResult(
                successful = false,
                errorMessage = "Your name is too long. Please shorten it"
            )
        }

        if (firstAndLastNames.any { it.length < 2 }) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter a valid name"
            )
        }

        return ValidationResult(true)
    }

    fun validateMobileNumber(mobileNumber: String): ValidationResult {
        if (mobileNumber.length != 10 || !mobileNumber.all { it.isDigit() }) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter a valid phone number."
            )
        }

        return ValidationResult(true)
    }

    fun validateAddress(address: String): ValidationResult {
        if (address.length < 10) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter complete address."
            )
        }

        val addressSeparatedByCommas = address.split(",")

        if (addressSeparatedByCommas.size < 3) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter complete address: (E.g: Pulchowk Marga, Pulchowk, Lalitpur-10)"
            )
        }

        return ValidationResult(true)
    }
}