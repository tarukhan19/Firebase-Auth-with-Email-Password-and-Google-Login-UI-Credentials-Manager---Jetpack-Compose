package com.demo.authentication.userauth.presentation.signup

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SignupScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun signupScreen_displaysAllUiElements() {
        // Given
        val state = SignupState()
        val action = SignupAction()

        // When
        composeTestRule.setContent {
            SignupScreen(
                signupState = state,
                signupAction = action
            )
        }

        // Then
        composeTestRule.onNodeWithText("Full Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email ID").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Confirm Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Phone Number").assertIsDisplayed()
        composeTestRule.onNodeWithText("SignUp").assertIsDisplayed()
        composeTestRule.onNodeWithText("Already have an account?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
    }

    @Test
    fun signupScreen_withInvalidInput_showsErrors() {
        // Given
        val state = SignupState(
            fullName = "",
            emailId = "invalid-email",
            password = "pass",
            confirmPassword = "different",
            phoneNumber = "123",
            fullNameError = "Name cannot be empty",
            emailIdError = "Invalid email format",
            passwordError = "Password must be at least 6 characters",
            confPasswordError = "Passwords do not match",
            phoneNumberError = "Mobile number must be at least 9 digits"
        )
        val action = SignupAction()

        // When
        composeTestRule.setContent {
            SignupScreen(
                signupState = state,
                signupAction = action
            )
        }

        // Then
        composeTestRule.onNodeWithText("Name cannot be empty").assertIsDisplayed()
        composeTestRule.onNodeWithText("Invalid email format").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password must be at least 6 characters").assertIsDisplayed()
        composeTestRule.onNodeWithText("Passwords do not match").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mobile number must be at least 9 digits").assertIsDisplayed()
    }

    @Test
    fun signupScreen_withValidInput_enablesSignupButton() {
        // Given
        val state = SignupState(
            fullName = "John Doe",
            emailId = "test@example.com",
            password = "Password123",
            confirmPassword = "Password123",
            phoneNumber = "1234567890",
            isTncAccepted = true
        )
        val action = SignupAction(
            isButtonEnabled = true
        )

        // When
        composeTestRule.setContent {
            SignupScreen(
                signupState = state,
                signupAction = action
            )
        }

        // Then
        composeTestRule.onNodeWithText("SignUp").assertIsEnabled()
    }

    @Test
    fun signupScreen_whenLoading_showsProgressIndicator() {
        // Given
        val state = SignupState(
            isLoading = true
        )
        val action = SignupAction()

        // When
        composeTestRule.setContent {
            SignupScreen(
                signupState = state,
                signupAction = action
            )
        }

        // Then
        composeTestRule.onNode(hasTestTag("circularProgressIndicator")).assertIsDisplayed()
    }

    @Test
    fun signupScreen_onNameInput_callsOnFullNameChange() {
        // Given
        var nameChanged = false
        val expectedName = "John Doe"

        val state = SignupState()
        val action = SignupAction(
            onFullNameChange = { name ->
                nameChanged = true
                assert(name == expectedName)
            }
        )

        // When
        composeTestRule.setContent {
            SignupScreen(
                signupState = state,
                signupAction = action
            )
        }

        composeTestRule.onNodeWithText("Full Name").performTextInput(expectedName)

        // Then
        assert(nameChanged)
    }

    @Test
    fun signupScreen_onEmailInput_callsOnEmailChange() {
        // Given
        var emailChanged = false
        val expectedEmail = "test@example.com"

        val state = SignupState()
        val action = SignupAction(
            onEmailChange = { email ->
                emailChanged = true
                assert(email == expectedEmail)
            }
        )

        // When
        composeTestRule.setContent {
            SignupScreen(
                signupState = state,
                signupAction = action
            )
        }

        composeTestRule.onNodeWithText("Email ID").performTextInput(expectedEmail)

        // Then
        assert(emailChanged)
    }

    @Test
    fun signupScreen_onPasswordInput_callsOnPasswordChange() {
        // Given
        var passwordChanged = false
        val expectedPassword = "Password123"

        val state = SignupState()
        val action = SignupAction(
            onPasswordChange = { password ->
                passwordChanged = true
                assert(password == expectedPassword)
            }
        )

        // When
        composeTestRule.setContent {
            SignupScreen(
                signupState = state,
                signupAction = action
            )
        }

        composeTestRule.onNodeWithText("Password").performTextInput(expectedPassword)

        // Then
        assert(passwordChanged)
    }

    @Test
    fun signupScreen_onConfirmPasswordInput_callsOnConfPasswordChange() {
        // Given
        var confPasswordChanged = false
        val expectedConfPassword = "Password123"

        val state = SignupState()
        val action = SignupAction(
            onConfPasswordChange = { confPassword ->
                confPasswordChanged = true
                assert(confPassword == expectedConfPassword)
            }
        )

        // When
        composeTestRule.setContent {
            SignupScreen(
                signupState = state,
                signupAction = action
            )
        }

        composeTestRule.onNodeWithText("Confirm Password").performTextInput(expectedConfPassword)

        // Then
        assert(confPasswordChanged)
    }

    @Test
    fun signupScreen_onMobileNumberInput_callsOnMobileNoChange() {
        // Given
        var mobileChanged = false
        val expectedMobile = "1234567890"

        val state = SignupState()
        val action = SignupAction(
            onMobileNoChange = { mobile ->
                mobileChanged = true
                assert(mobile == expectedMobile)
            }
        )

        // When
        composeTestRule.setContent {
            SignupScreen(
                signupState = state,
                signupAction = action
            )
        }

        composeTestRule.onNodeWithText("Phone Number").performTextInput(expectedMobile)

        // Then
        assert(mobileChanged)
    }

    @Test
    fun signupScreen_onTncCheckChanged_callsOnTncCheck() {
        // Given
        var tncChanged = false

        val state = SignupState()
        val action = SignupAction(
            onTncCheck = { checked ->
                tncChanged = true
                assert(checked)
            }
        )

        // When
        composeTestRule.setContent {
            SignupScreen(
                signupState = state,
                signupAction = action
            )
        }

        // Find and click the checkbox
        composeTestRule.onNodeWithTag("Checkbox").performClick()

        // Then
        assert(tncChanged)
    }

    @Test
    fun signupScreen_onSignInClick_callsOnSignInNavigate() {
        // Given
        var signInClicked = false

        val state = SignupState()
        val action = SignupAction(
            onSignInNavigate = {
                signInClicked = true
            }
        )

        // When
        composeTestRule.setContent {
            SignupScreen(
                signupState = state,
                signupAction = action
            )
        }

        composeTestRule.onNodeWithText("Sign In").performClick()

        // Then
        assert(signInClicked)
    }

    @Test
    fun signupScreen_onSignupButtonClick_callsOnSubmit() {
        // Given
        var submitCalled = false

        val state = SignupState()
        val action = SignupAction(
            isButtonEnabled = true,
            onSubmit = {
                submitCalled = true
            }
        )

        // When
        composeTestRule.setContent {
            SignupScreen(
                signupState = state,
                signupAction = action
            )
        }

        composeTestRule.onNodeWithText("SignUp").performClick()

        // Then
        assert(submitCalled)
    }
}