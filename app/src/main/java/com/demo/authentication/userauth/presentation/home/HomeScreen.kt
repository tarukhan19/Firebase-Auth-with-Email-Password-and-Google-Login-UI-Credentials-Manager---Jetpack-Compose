package com.demo.authentication.userauth.presentation.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.demo.authentication.R
import com.demo.authentication.userauth.presentation.components.CustomTextForm
import com.demo.authentication.userauth.presentation.components.ScaffoldUi

@Composable
fun HomeScreen() {
    ScaffoldUi {
        Spacer(modifier = Modifier.padding(top = 20.dp))
        CustomTextForm(text = stringResource(R.string.home))
    }
}
