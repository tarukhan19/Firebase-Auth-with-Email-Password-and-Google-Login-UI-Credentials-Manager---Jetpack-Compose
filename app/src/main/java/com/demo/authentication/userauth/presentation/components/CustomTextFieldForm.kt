package com.demo.authentication.userauth.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp

@Composable
fun CustomTextFieldForm(
    value: String,
    onValueChange: (String) -> Unit,
    label: Int,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions,
    placeholder: Int,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    contentDescription: Int = 0,
    leadingContentDescription: Int = 0,
    onTrailingIconClicked: (() -> Unit)? = null,
    maxLines: Int = 1,
    color: Color = MaterialTheme.colorScheme.onBackground,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = stringResource(label),
                style = TextStyle(fontSize = 12.sp, color = Color.Gray),
            )
        },
        modifier = modifier,
        placeholder = {
            Text(
                text = stringResource(placeholder),
                style = TextStyle(fontSize = 12.sp, color = Color.LightGray),
            )
        },
        leadingIcon =
            leadingIcon?.let {
                { Icon(imageVector = it, contentDescription = stringResource(contentDescription)) }
            },
        trailingIcon =
            trailingIcon?.let {
                {
                    IconButton(onClick = { onTrailingIconClicked?.invoke() }) {
                        Icon(
                            imageVector = it,
                            contentDescription = stringResource(leadingContentDescription),
                        )
                    }
                }
            },
        textStyle =
            TextStyle(
                fontSize = 14.sp,
                color = color,
            ),
        singleLine = singleLine,
        isError = isError,
        keyboardOptions = keyboardOptions,
        maxLines = maxLines,
        visualTransformation = visualTransformation,
        shape = RoundedCornerShape(percent = 20),
    )
}
