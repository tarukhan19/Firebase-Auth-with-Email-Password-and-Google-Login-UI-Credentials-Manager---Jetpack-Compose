package com.demo.authentication.userauth.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag

@Composable
fun CircularProgressBar() {
    Box(
        modifier =
            Modifier
                .testTag("circularProgressIndicator")
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)) // Semi-transparent overlay
                .clickable(enabled = false) {},
        contentAlignment = Alignment.Center,

    ) {
        CircularProgressIndicator()
    }
}
