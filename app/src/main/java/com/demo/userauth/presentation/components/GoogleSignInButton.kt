package com.demo.userauth.presentation.components

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.common.SignInButton

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    AndroidView(
        factory = { context ->
            SignInButton(context).apply {
                setSize(SignInButton.SIZE_STANDARD)
                setColorScheme(SignInButton.COLOR_DARK)
                setOnClickListener { onClick() }

                // Apply corner radius
                outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        outline.setRoundRect(0, 0, view.width, view.height, 20.dp.value)
                    }
                }
                clipToOutline = true  // Ensures corners are clipped
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}


