package com.demo.authentication.userauth.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldUi(
    showToolBar: Boolean = false,
    title: String? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    keyboardController?.hide()

    Scaffold(
        topBar = {
            if (showToolBar) {
                TopAppBar(
                    title = {
                        title?.let {
                            Text(text = it)
                        }
                    },
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(10.dp)
                    .imePadding(),
        ) {
            content(paddingValues)
        }
    }
}
