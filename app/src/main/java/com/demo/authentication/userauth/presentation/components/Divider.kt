package com.demo.authentication.userauth.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Divider() {
    HorizontalDivider(
        color = Color.LightGray,
        thickness = 0.5.dp,
        modifier = Modifier.padding(vertical = 8.dp),
    )
}
