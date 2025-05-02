package com.demo.authentication.userauth.presentation.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun loginScreen_displayAllUiElements() {
        // given
        val state = LoginState()
        val action = LoginAction()

        // when
        composeTestRule.setContent {
            LoginScreen(
                loginState = state,
                loginAction = action
            )
        }

        // then
        composeTestRule.onNodeWithText("Email ID").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
        composeTestRule.onNodeWithText("Don't have an account?").assertIsDisplayed()
        composeTestRule.onNodeWithText("SignUp").assertIsDisplayed()

    }

    @Test
    fun loginScreen_withInvalidInput_showErrors() {
        // given
        val state = LoginState(
            emailId = "invalidEmail",
            password = "123",
            emailIdError = "Invalid email format",
            passwordError = "Password must be at least 6 characters"
        )
        val action = LoginAction()

        // when
        composeTestRule.setContent {
            LoginScreen(
                loginState = state,
                loginAction = action
            )
        }

        // then
        composeTestRule.onNodeWithText("Invalid email format").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password must be at least 6 characters").assertIsDisplayed()
    }

    @Test
    fun loginScreen_withValidInput_enablesLoginButton() {
        // Given
        val state = LoginState(
            emailId = "test@example.com",
            password = "Password123",
            isLoginButtonEnabled = true
        )

        val action = LoginAction()

        // when
        composeTestRule.setContent {
            LoginScreen(
                loginState = state,
                loginAction = action
            )
        }
        // then
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
    }

    @Test
    fun loginScreen_whenLoading_showsProgressIndicator() {
        // Given
        val state = LoginState(
            isLoading = true
        )
        val action = LoginAction()

        // When
        composeTestRule.setContent {
            LoginScreen(
                loginState = state,
                loginAction = action
            )
        }

        // Then
        composeTestRule.onNode(hasTestTag("circularProgressIndicator")).assertIsDisplayed()
    }

    @Test
    fun loginScreen_onEmailInput_callsOnEmailChange() {
        // given
        var emailChanged = false
        val expectedEmail = "test@example.com"

        val state = LoginState()
        val action = LoginAction(
            onEmailChange = { emailId ->
                emailChanged = true
                assert(emailId == expectedEmail)
            }
        )

        // When
        composeTestRule.setContent {
            LoginScreen(
                loginState = state,
                loginAction = action
            )
        }

        composeTestRule.onNodeWithText("Email ID").performTextInput(expectedEmail)

        // Then
        assert(emailChanged)
    }

    @Test
    fun loginScreen_onPasswordInput_callsOnPasswordChange() {
        // given
        var passwordChange = false
        var expectedPassword = "Password123"

        var state = LoginState()
        var action = LoginAction(
            onPasswordChange = { password ->
                passwordChange = true
                assert(password == expectedPassword)
            }
        )

        // when
        composeTestRule.setContent {
            LoginScreen(
                loginState = state,
                loginAction = action
            )
        }
        composeTestRule.onNodeWithText("Password").performTextInput(expectedPassword)

        // then
        assert(passwordChange)
    }

    @Test
    fun loginScreen_onSignUpClick_callsOnSignUpNavigate() {
        // Given
        var signUpClicked = false

        val state = LoginState()
        val action = LoginAction(
            onSignUpNavigate = {
                signUpClicked = true
            }
        )

        // When
        composeTestRule.setContent {
            LoginScreen(
                loginState = state,
                loginAction = action
            )
        }

        composeTestRule.onNodeWithText("SignUp").performClick()

        // Then
        assert(signUpClicked)
    }

    @Test
    fun loginScreen_onLoginButtonClick_callsOnSubmit() {
        var signInClicked = false

        val state = LoginState( isLoginButtonEnabled = true )
        val action = LoginAction(onSubmit = {
            signInClicked =  true
        })

        composeTestRule.setContent {
            LoginScreen(
                loginState = state,
                loginAction = action
            )
        }

        composeTestRule.onNodeWithText("Sign In").performClick()

        assert(signInClicked)
    }

}