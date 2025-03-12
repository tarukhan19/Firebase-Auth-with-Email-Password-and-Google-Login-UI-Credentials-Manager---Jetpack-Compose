package com.demo.userauth.utils

import android.util.Patterns

/*
Returns true if:
The email is empty (emailId.isEmpty()).
The email format is incorrect (!Patterns.EMAIL_ADDRESS.matcher(emailId).matches()).
Returns false if the email is valid.
*/

/*
Returns true if:
The password has 6 or fewer characters (invalid password).
Returns false if the password is strong enough.
 */

fun String.isValidEmail(): Boolean {
    return isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    return length > 6
}

fun String.isValidName(): Boolean {
    return isNotEmpty() && length > 4
}

fun String.isValidPhoneNumber(): Boolean {
    return isNotEmpty() && length >= 9
}

fun String.matchesPassword(confirmPassword: String): Boolean {
    return this.isValidPassword() && confirmPassword.isValidPassword() && this == confirmPassword
}
