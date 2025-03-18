package com.demo.userauth.presentation.actions

data class SignupAction(
    // UI Actions
    val onFullNameChange: (String) -> Unit = {},
    val onEmailChange: (String) -> Unit = {},
    val onMobileNoChange: (String) -> Unit = {},
    val onPasswordChange: (String) -> Unit = {},
    val onConfPasswordChange: (String) -> Unit = {},
    val onTogglePasswordVisibility: () -> Unit = {},
    val onToggleConfPasswordVisibility: () -> Unit = {},
    val onTncCheck: (Boolean) -> Unit = {},
    val onSubmit: () -> Unit = {},
    val onSignInNavigate: () -> Unit = {},
    val isButtonEnabled: Boolean = false
)
