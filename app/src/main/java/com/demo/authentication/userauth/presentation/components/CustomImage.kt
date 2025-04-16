package com.demo.authentication.userauth.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource


@Composable
fun CustomImage(
    imageInt: Int = 0,
    contentDescription: Int = 0,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(imageInt),
        contentDescription = stringResource(contentDescription),
        modifier = modifier
    )
}